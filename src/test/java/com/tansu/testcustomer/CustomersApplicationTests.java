
package com.tansu.testcustomer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tansu.testcustomer.controller.CustomerController;
import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.entities.Customer;
import com.tansu.testcustomer.repository.CustomerRepository;
import com.tansu.testcustomer.utils.Constants;
import net.datafaker.Faker;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Objects;

import static com.tansu.testcustomer.utils.Constants.*;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testcontainers.shaded.org.hamcrest.collection.IsCollectionWithSize.hasSize;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomersApplicationTests {

    private final Logger LOG = LoggerFactory.getLogger(CustomersApplicationTests.class);

    @Autowired private CustomerRepository customerRepository;
    @Autowired private MockMvc mockMvc;
    @LocalServerPort private Integer port;



    private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:1");




    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }


    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }


    @BeforeEach
    void setUp() {
        List<Customer> customers = List.of(
                new Customer(null, "John", "Armel",17),
                new Customer(null, "Dennis", "Mark",34)
        );

        customerRepository.saveAll(customers);

        mockMvc = MockMvcBuilders
                .standaloneSetup(CustomerController.class)
                .build();
    }



    @Test
    void should_create_one_customer() throws Exception {
        var faker = new Faker();
        var customer = CustomerDto.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().firstName())
                .age(faker.number().numberBetween(15,60))
                .build();

        mockMvc.perform(post(APP_SLASH+CREATE_CUSTOMER_ENDPOINT)
                        .contentType(APPLICATION_JSON)
                        .content(Objects.requireNonNull(asJsonString(customer))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", aMapWithSize(3)));

        Assertions.assertEquals(this.customerRepository.findAll().size(),1);
    }

    @Test
    void should_retrieve_one_customer() throws Exception {
        mockMvc.perform(get(APP_SLASH + FIND_BY_ID_CUSTOMER_ENDPOINT, 4))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4));

    }


    @Test
    void should_get_all_customers() throws Exception {
        mockMvc.perform(get(APP_SLASH+FIND_ALL_CUSTOMER_ENDPOINT)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*").value(hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].id").value(2));

       Assertions.assertEquals(customerRepository.findAll().size(),2);
    }

    @Test
    void should_delete_one_customer() throws Exception {
        mockMvc.perform(delete(APP_SLASH+DELETE_BY_ID_CUSTOMER_ENDPOINT, 2))
                .andDo(print())
                .andExpect(status().isOk());

        Assertions.assertEquals(customerRepository.findAll().size(),1);
    }

    private String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }



}

