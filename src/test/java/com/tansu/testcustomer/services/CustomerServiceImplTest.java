package com.tansu.testcustomer.services;

import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.dto.HttpResponse;
import com.tansu.testcustomer.entities.Customer;
import com.tansu.testcustomer.exception.EntityNotFoundException;
import com.tansu.testcustomer.exception.ObjectValidationException;
import com.tansu.testcustomer.mapper.CustomerMapper;
import com.tansu.testcustomer.repository.CustomerRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
@Slf4j
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CustomerServiceImplTest {

    @Autowired CustomerService customerService;
    @Autowired CustomerRepository customerRepository;

    private final static Faker faker = new Faker();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void should_create_with_success() {
        log.info("should_create_with_success");
        CustomerDto customerDto = getCustomerDto();
        HttpResponse<CustomerDto> customerSaved = getCustomerSaved(customerDto);
        CustomerDto customer = customerSaved.data().stream().findFirst().get();

        Assertions.assertNotNull(customer);
        Assertions.assertNotNull(customer.id());
        Assertions.assertEquals(customer.firstName(),customerDto.firstName());
        Assertions.assertEquals(customer.lastName(),customerDto.lastName());
        Assertions.assertEquals(customer.age(),customerDto.age());
    }

    private HttpResponse<CustomerDto> getCustomerSaved(CustomerDto customerDto) {
        return customerService.save(customerDto);
    }


    @Test
    void should_create_with_error() {
        CustomerDto customerDto =
                CustomerDto
                .builder().age(12)
                .build();

        ObjectValidationException objectValidationException =
                assertThrows(ObjectValidationException.class, () -> getCustomerSaved(customerDto));

        Assertions.assertNotNull(objectValidationException);
        Assertions.assertEquals(objectValidationException.getViolations().size(),5);
    }

    @Test
    void should_update_with_success() {
        log.info("should_create_with_success");
        CustomerDto customerDto = getCustomerDto();
        HttpResponse<CustomerDto> customerSaved = getCustomerSaved(customerDto);
        Customer customer =
                customerSaved.data().stream()
                        .map(CustomerMapper::toEntity)
                        .findFirst().get();

        customer.setFirstName("James");
        customer.setAge(17);


        Customer customerUpdateReturned =
                customerService.update(customer.getId(),CustomerMapper.toDto(customer))
                            .data().stream()
                            .map(CustomerMapper::toEntity)
                            .findFirst().get();

        Assertions.assertNotNull(customerUpdateReturned);
        Assertions.assertEquals(customerUpdateReturned.getId(),customer.getId());
        Assertions.assertEquals(customerUpdateReturned.getFirstName(),customer.getFirstName());
        Assertions.assertEquals(customerUpdateReturned.getLastName(),customer.getLastName());
        Assertions.assertEquals(customerUpdateReturned.getAge(),customer.getAge());
    }

    private static CustomerDto getCustomerDto() {
        return CustomerDto
                .builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().firstName())
                .age(faker.number().numberBetween(15, 60))
                .build();
    }

    @Test
    void should_find_by_id() {
        CustomerDto customerDto = getCustomerDto();
        HttpResponse<CustomerDto> customerSaved = getCustomerSaved(customerDto);
        Customer customer =
                customerSaved.data().stream()
                        .map(CustomerMapper::toEntity)
                        .findFirst().get();

        String message = customerService.findById(customer.getId()).message();
        Assertions.assertEquals(message,"Customer founded successfully");

    }


    @Test
    void should_find_by_id_is_null() {
        log.info("should_find_by_id_is_null");
        var customerDtoHttpResponse = customerService.findById(null);
        Assertions.assertNull(customerDtoHttpResponse);
    }

    @Test
    void should_find_all_with_success() {
        log.info("should_find_all_with_success");
        HttpResponse<List<CustomerDto>> customerServiceAll = customerService.findAll();
        Assertions.assertNotNull(customerServiceAll.data());
    }


    @Test
    void should_find_all_with_error_message() {
        log.info("should_find_all_with_error_message");

        customerRepository.deleteAll();

        String message = customerService.findAll().message();
        Assertions.assertEquals(message,"No customers to display");
    }


    @Test
    void should_find_all_page_with_success() {
        log.info("should_find_all_with_success");

        CustomerDto customerDto = getCustomerDto();
        getCustomerSaved(customerDto);

        HttpResponse<Map<String, Object>> all = customerService.findAll(1, 3);
        log.info("all.pageCustomers() "+all.pageCustomers());
        Assertions.assertNotNull(all.pageCustomers());
    }


    @Test
    void should_find_all_page_with_error_message() {
        log.info("should_find_all_with_error_message");

        customerRepository.deleteAll();

        String message = customerService.findAll(0, 3).message();
        Assertions.assertEquals(message,"No customers to display");
    }



    @Test
    void should_delete() {
        log.info("should_delete");
        HttpResponse<CustomerDto> customerSaved = getCustomerSaved(getCustomerDto());
        CustomerDto customerDtoSaved = customerSaved.data().stream().findFirst().get();

        HttpResponse<CustomerDto> deleted = customerService.delete(customerDtoSaved.id());
        CustomerDto customerDtoDeleted = deleted.data().stream().findFirst().get();

        Assertions.assertNotNull(customerDtoDeleted);
        assertEquals(customerDtoDeleted.id(),customerDtoSaved.id());
        assertEquals(deleted.message(),"Customer deleted successfully");
    }


    @Test
    void should_delete_id_is_null() {
        log.info("should_delete_id_is_null");
        var deleted = customerService.delete(null);
        Assertions.assertNull(deleted);
    }


    @Test
    void should_throws_error_delete() {
        log.info("should_throws_error_delete");
        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> customerService.delete(Integer.MAX_VALUE));

        Assertions.assertNotNull(entityNotFoundException);
        Assertions.assertEquals(entityNotFoundException.getMessage(),"This customer was not found on the server");
    }


    @ParameterizedTest
    @NullSource
    void should_return_null_delete_with_nullEmptyAndBlankStrings(Integer id) {
        log.info("should_throws_error_delete");
        assertNull(customerService.delete(id));
    }



}