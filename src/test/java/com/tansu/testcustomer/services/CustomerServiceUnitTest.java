package com.tansu.testcustomer.services;

import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.dto.HttpResponse;
import com.tansu.testcustomer.entities.Customer;
import com.tansu.testcustomer.entities.User;
import com.tansu.testcustomer.exception.EntityNotFoundException;
import com.tansu.testcustomer.exception.ObjectValidationException;
import com.tansu.testcustomer.mapper.CustomerMapper;
import com.tansu.testcustomer.mapper.UserMapper;
import com.tansu.testcustomer.repository.CustomerRepository;
import com.tansu.testcustomer.validation.ObjectsValidator;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class CustomerServiceUnitTest {
    @Mock private CustomerRepository customerRepository;
    @Mock static ObjectsValidator<CustomerDto> validator;
    @InjectMocks static CustomerServiceImpl customerService;

    private final static Faker faker                     = new Faker();
    private final static List<CustomerDto> customersDtos = new ArrayList<>();
    private  static List<Customer> customers             = new ArrayList<>();

    private CustomerDto customerDto1;
    private CustomerDto customerDto2;

    @BeforeEach
    public void init() {
         customerDto1 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,60)).build();
         customerDto2 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,60)).build();

         customersDtos.addAll(List.of(customerDto1,customerDto2));
         customers    = customersDtos.stream().map(CustomerMapper::toEntity).toList();
    }

    @Test
    @DisplayName("should create customer with success")
    void should_create_customer_with_success() {
        when(customerRepository.save(Mockito.any(Customer.class))).thenReturn(CustomerMapper.toEntity(customerDto1));
        var customerSaved = customerService.save(customerDto1);
        CustomerDto customerDto = customerSaved.data().stream().findFirst().get();

        assertAll(
                () -> assertNotNull(customerSaved),
                () -> assertEquals(customerDto.firstName(),customerDto1.firstName()),
                () -> assertEquals(customerDto.lastName(),customerDto1.lastName())
        );
    }

    @Test
    @DisplayName("should create customer with error")
    void should_create_customer_with_error() {
        CustomerDto customerDto = CustomerDto.builder().firstName(null).age(1).build();

        ObjectValidationException mock = mock(ObjectValidationException.class);
        when(customerRepository.save(Mockito.any(Customer.class))).thenThrow(mock);

        ObjectValidationException exception =
                assertThrows(ObjectValidationException.class, () -> customerService.save(customerDto));

        assertNotNull(exception);
        assertEquals(exception.getClass(),ObjectValidationException.class);
        assertEquals(exception.getViolations(),mock.getViolations());
    }

    @Test
    @DisplayName("should update customer with success")
    void should_update_customer_with_success() {
        var customer = customers.get(0);
        val id = customerDto1.id();
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.save(Mockito.any(Customer.class))).thenReturn(customer);

        var customerUpdated = customerService.update(id,customerDto2);

        assertAll(() -> assertNotNull(customerUpdated.data()));
    }

    @Test
    @DisplayName("should find by id customer")
    void should_find_by_id_customer() {
        var customer = customers.get(1);
        val id = 1;
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        final HttpResponse<CustomerDto> customerServiceById = customerService.findById(id);
        String message = customerService.findById(id).message();

        final var customerDto = customerServiceById.data().stream().findFirst();

        assertNotNull(customerDto.orElse(null));
        assertEquals( customerDto.orElse(null).firstName(),customers.get(1).getFirstName());
        assertEquals(message,"Customer founded successfully");
    }

    @Test
    @DisplayName("should find by id customer is null")
    void should_find_by_id_customer_is_null() {
        final String MESSAGE = "This Customer was not found on the server";

        when(customerRepository.findById(Integer.MAX_VALUE)).thenThrow(new EntityNotFoundException(MESSAGE));
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            customerService.findById(Integer.MAX_VALUE);
        });

        assertTrue(exception.getMessage().contains(MESSAGE));
    }

    @Test
    @DisplayName("should find all with success")
    void should_find_all_with_success() {
        when(customerRepository.findAll()).thenReturn(customers);

        val customerServiceAll = customerService.findAll();
        val customerDtos = customerServiceAll.data().stream().flatMap(Collection::stream).toList();
        assertEquals(customerDtos.stream().map(CustomerMapper::toEntity).toList(), customers);
    }

    @Test
    @DisplayName("should find all customers with error message")
    void should_find_all_customers_with_error_message() {
        when(customerRepository.findAll()).thenReturn(List.of());

        val customerServiceAll = customerService.findAll();
        val message = customerServiceAll.message();

        assertTrue(message.contains("No customers to display"));
    }

    @Test
    @DisplayName("should find all customer page with success")
    void should_find_all_customer_page_with_success() {
        val customerPage =  mock(Page.class);
        when(customerRepository.findAll(Mockito.any(Pageable.class))).thenReturn(customerPage);

        val customerServiceAll = customerService.findAll(1, 3);

        assertNotNull(customerServiceAll.pageCustomers());
    }

    @Test
    @DisplayName("should find all page customer with error message")
    void should_find_all_page_customer_with_error_message() {
        final Page<Customer> mock = Page.empty(mock(Pageable.class));
        when(customerRepository.findAll(Mockito.any(Pageable.class))).thenReturn(mock);

        val customerServiceAll = customerService.findAll(0, 1);

        assertNotNull(customerServiceAll.pageCustomers());
        Assertions.assertEquals(customerServiceAll.message(),"No customers to display");
    }

    @Test
    @DisplayName("should customer delete")
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
    @DisplayName("should return null delete with null")
    void should_return_null_delete_with_null(Integer id) {
        val MESSAGE = "This Customer was not found on the server";

        when(customerRepository.findById(id)).thenThrow(new EntityNotFoundException(MESSAGE));
        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> customerService.delete(id));

        assertNotNull(entityNotFoundException);
        assertTrue(entityNotFoundException.getMessage().contains(MESSAGE));
    }
}