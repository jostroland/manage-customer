package com.tansu.testcustomer.services;

import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.dto.HttpResponse;
import com.tansu.testcustomer.dto.UserDto;
import com.tansu.testcustomer.dto.UserRequest;
import com.tansu.testcustomer.entities.Customer;
import com.tansu.testcustomer.entities.User;
import com.tansu.testcustomer.exception.EntityNotFoundException;
import com.tansu.testcustomer.exception.ObjectValidationException;
import com.tansu.testcustomer.mapper.CustomerMapper;
import com.tansu.testcustomer.mapper.UserMapper;
import com.tansu.testcustomer.repository.CustomerRepository;
import com.tansu.testcustomer.validation.ObjectsValidator;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class CustomerServiceUnitTest {


    @Mock private CustomerRepository customerRepository;
    @Mock static ObjectsValidator<CustomerDto> validator;

    @InjectMocks static CustomerServiceImpl customerService;

    private final static Faker faker = new Faker();
    private final static List<CustomerDto> customersDtos = new ArrayList<>();
    private  static List<Customer> customers = new ArrayList<>();

    private CustomerDto customerDto1;
    private CustomerDto customerDto2;
    private CustomerDto customerDto3;
    private CustomerDto customerDto4;
    private CustomerDto customerDto5;



    @BeforeEach
    public void init() {
         customerDto1 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,60)).build();
         customerDto2 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,60)).build();
         customerDto3 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,60)).build();
         customerDto4 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,60)).build();
         customerDto5 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,60)).build();

         customersDtos.addAll(List.of(customerDto1,customerDto2,customerDto3,customerDto4,customerDto5));
         customers = customersDtos.stream().map(CustomerMapper::toEntity).toList();
    }
    @Test
    @DisplayName("should_create_customer_with_success")
    void should_create_customer_with_success() {
        when(customerRepository.save(Mockito.any(Customer.class))).thenReturn(customers.get(0));
        HttpResponse<CustomerDto> customerSaved = customerService.save(customerDto1);
        CustomerDto customerDto = customerSaved.data().stream().findFirst().get();

        assertAll(
                () -> assertNotNull(customerSaved),
                () -> assertEquals(customerDto.firstName(),customerDto1.firstName()),
                () -> assertEquals(customerDto.lastName(),customerDto1.lastName())
        );
    }

    @Test
    @DisplayName("should_create_customer_with_error")
    void should_create_customer_with_error() {
        CustomerDto customerDto = CustomerDto.builder().age(12).build();

        ObjectValidationException mock = mock(ObjectValidationException.class);
        when(customerRepository.save(Mockito.any(Customer.class))).thenThrow(mock);

        ObjectValidationException exception =
                assertThrows(ObjectValidationException.class, () -> customerService.save(customerDto));


        assertNotNull(exception);
        assertEquals(exception.getClass(),ObjectValidationException.class);
        assertEquals(exception.getViolations(),mock.getViolations());
    }


    @Test
    @DisplayName("should_update_customer_with_success")
    void should_update_customer_with_success() {

        final int id = 1;
        when(customerRepository.findById(id)).thenReturn(Optional.ofNullable(customers.get(0)));
        when(customerRepository.save(Mockito.any(Customer.class))).thenReturn(customers.get(0));

        HttpResponse<CustomerDto> customerUpdated = customerService.update(id,customerDto1);

        assertAll(() -> assertNotNull(customerUpdated.data()));
    }

    @Test
    @DisplayName("should_find_by_id_customer")
    void should_find_by_id_customer() {
        Integer id = 1;
        when(customerRepository.findById(id)).thenReturn(Optional.ofNullable(customers.get(1)));

        final HttpResponse<CustomerDto> customerServiceById = customerService.findById(id);
        String message = customerService.findById(id).message();

        final Optional<? extends CustomerDto> customerDto = customerServiceById.data().stream().findFirst();

        assertNotNull(customerDto.orElse(null));
        assertEquals( customerDto.orElse(null).firstName(),customers.get(1).getFirstName());
        assertEquals(message,"Customer founded successfully");

    }


    @Test
    @DisplayName("should_find_by_id_customer_is_null")
    void should_find_by_id_customer_is_null() {

        final String MESSAGE = "This Customer was not found on the server";

        when(customerRepository.findById(Integer.MAX_VALUE)).thenThrow(new EntityNotFoundException(MESSAGE));
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            customerService.findById(Integer.MAX_VALUE);
        });

        assertTrue(exception.getMessage().contains(MESSAGE));
    }


    @Test
    @DisplayName("should_find_all_with_success")
    void should_find_all_with_success() {
        when(customerRepository.findAll()).thenReturn(customers);

        final HttpResponse<List<CustomerDto>> customerServiceAll = customerService.findAll();
        final List<CustomerDto> customerDtos = customerServiceAll.data().stream().flatMap(Collection::stream).toList();
        assertEquals(customerDtos.stream().map(CustomerMapper::toEntity).toList(), customers);
    }


    @Test
    @DisplayName("should_find_all_customers_with_error_message")
    void should_find_all_customers_with_error_message() {
        when(customerRepository.findAll()).thenReturn(List.of());

        final HttpResponse<List<CustomerDto>> customerServiceAll = customerService.findAll();
        String message = customerServiceAll.message();

        assertTrue(message.contains("No customers to display"));

    }


    @Test
    @DisplayName("should_find_all_customer_page_with_success")
    void should_find_all_customer_page_with_success() {
        Page customerPage =  mock(Page.class);
        when(customerRepository.findAll(Mockito.any(Pageable.class))).thenReturn(customerPage);

        HttpResponse<Map<String, Object>> customerServiceAll = customerService.findAll(1, 3);

        assertNotNull(customerServiceAll.pageCustomers());
    }


    @Test
    @DisplayName("should_find_all_page_customer_with_error_message")
    void should_find_all_page_customer_with_error_message() {

        final Page<Customer> mock = Page.empty(mock(Pageable.class));
        when(customerRepository.findAll(Mockito.any(Pageable.class))).thenReturn(mock);

        HttpResponse<Map<String, Object>> customerServiceAll = customerService.findAll(0, 1);

        assertNotNull(customerServiceAll.pageCustomers());
        Assertions.assertEquals(customerServiceAll.message(),"No customers to display");
    }



    @Test
    @DisplayName("should_customer_delete")
    void should_customer_delete() {

        final int id = 1;

        when(customerRepository.findById(id)).thenReturn(Optional.of(customers.get(id)));

        customerService.delete(id);
        verify(customerRepository, times(1)).delete(customers.get(id));

        ArgumentCaptor<Customer> orderArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).delete(orderArgumentCaptor.capture());
        Customer customerDeleted = orderArgumentCaptor.getValue();

        assertNotNull(customerDeleted);
    }








    @ParameterizedTest
    @ValueSource(ints = { Integer.MAX_VALUE,869776 })
    @DisplayName("should_return_null_delete_with_null")
    void should_return_null_delete_with_null(Integer id) {

        final String MESSAGE = "This Customer was not found on the server";

        when(customerRepository.findById(id)).thenThrow(new EntityNotFoundException(MESSAGE));
        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> customerService.delete(id));

        assertNotNull(entityNotFoundException);
        assertTrue(entityNotFoundException.getMessage().contains(MESSAGE));
    }



}