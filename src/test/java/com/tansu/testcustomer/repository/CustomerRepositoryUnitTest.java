package com.tansu.testcustomer.repository;

import com.tansu.testcustomer.entities.Customer;
import lombok.val;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;




@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CustomerRepositoryUnitTest {
    @Autowired private CustomerRepository customerRepository;
    private final Faker faker = new Faker();

    @BeforeEach
    public void setUp() {
        IntStream.rangeClosed(1,3)
                .mapToObj(value ->
                        new Customer(null, faker.name().firstName(), faker.name().lastName(), faker.number().numberBetween(15,60)))
                        .forEach(customerRepository::save);
    }

    @AfterEach
    public void destroy() {
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("should test get all customers")
    public void should_test_get_all_customers() {
        val customerList = customerRepository.findAll();
        assertEquals(customerList.size(),3);
        assertNotNull(customerList.get(0).getFirstName());
    }

    @Test
    @DisplayName("should get invalid customer")
    public void should_get_invalid_customer() {
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            customerRepository.findById(120).get();
        });

        assertAll("exception",
                () -> assertNotNull(exception),
                () ->  assertEquals(exception.getClass(),NoSuchElementException.class),
                () -> assertTrue(exception.getMessage().contains("No value present"))
        );
    }

    @Test
    @DisplayName("should create customer")
    public void should_create_customer() {
        val saved = Customer.builder().firstName(faker.name().firstName()).firstName(faker.name().lastName()).age(faker.number().numberBetween(15,60)).build();
        val returned = customerRepository.save(saved);

        assertNotNull(returned);
        assertEquals(returned.getFirstName(),saved.getFirstName());
        assertEquals(returned.getLastName(),saved.getLastName());
        assertEquals(returned.getAge(),saved.getAge());
    }

    @Test
    @DisplayName("should test delete customer")
    public void should_test_delete_customer() {
        val saved = Customer.builder().firstName(faker.name().firstName()).firstName(faker.name().lastName()).age(faker.number().numberBetween(15,60)).build();
        customerRepository.save(saved);
        customerRepository.delete(saved);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            customerRepository.findById(400).get();
        });
        assertNotNull(exception);
        assertEquals(exception.getClass(),NoSuchElementException.class);
        assertEquals(exception.getMessage(),"No value present");
    }
}