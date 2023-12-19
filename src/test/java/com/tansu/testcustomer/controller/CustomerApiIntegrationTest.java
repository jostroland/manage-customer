package com.tansu.testcustomer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tansu.testcustomer.entities.Customer;
import com.tansu.testcustomer.mapper.CustomerMapper;
import com.tansu.testcustomer.repository.CustomerRepository;
import com.tansu.testcustomer.services.CustomerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerServiceImpl customerService;

    private static HttpHeaders headers;
    private JsonObject jsonObject;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("test all customers")
    @Sql(statements = "INSERT INTO customers (id, firstname, lastname, age) VALUES (1,'jost', 'roland', 17)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM customers WHERE id='1'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void test_customers_list() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort()+"/all", HttpMethod.GET, entity, new ParameterizedTypeReference<>(){});


        jsonObject = JsonParser.parseString(Objects.requireNonNull(response.getBody()))
                .getAsJsonObject();

        assertEquals(response.getStatusCodeValue(), 200);
        assertTrue(!jsonObject.get("message").getAsString().equals("No customers to display"));
    }


    @Test
    @DisplayName("test find by id customers")
    @Sql(statements = "INSERT INTO customers (id, firstname, lastname, age) VALUES (1,'jost', 'roland', 17)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM customers WHERE id='1'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void test_find_by_id_customer() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort()+"/findById/1", HttpMethod.GET, entity, new ParameterizedTypeReference<>(){});
        
        jsonObject = JsonParser.parseString(Objects.requireNonNull(response.getBody()))
                .getAsJsonObject();

        assertEquals(response.getStatusCodeValue(), 200);
        assertTrue(jsonObject.get("message").getAsString().equals("Customer founded successfully"));
    }

    @Test
    @Sql(statements = "INSERT INTO customers (id, firstname, lastname, age) VALUES (3, 'jost', 'roland', 17)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void test_delete_by_id_customer()  {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                (createURLWithPort() + "/delete/3"), HttpMethod.DELETE, entity, String.class);

        jsonObject =
                JsonParser
                .parseString(Objects.requireNonNull(response.getBody()))
                .getAsJsonObject();

        assertEquals(response.getStatusCodeValue(), 200);
        assertTrue(jsonObject.get("message").getAsString().equals("Customer deleted successfully"));
    }

    @Test
    @Sql(statements = "DELETE FROM customers", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void test_create_customer() throws JsonProcessingException {
        Customer customer = new Customer(1, "jost", "roland", 17);
        final String stringCustomer = objectMapper.writeValueAsString(customer);
        HttpEntity<String> entity = new HttpEntity<>(stringCustomer, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort()+"/save", HttpMethod.POST, entity, String.class);

        jsonObject =
                JsonParser
                        .parseString(Objects.requireNonNull(response.getBody()))
                        .getAsJsonObject();

        /*final Object[] array = jsonObject.getAsJsonArray("data").asList().toArray();
        final Object lastCustomer =  Arrays.stream(array).findFirst().get();
        log.info("stringCustomer :"+ stringCustomer);
        assertEquals(stringCustomer, lastCustomer);*/
        
        assertEquals(jsonObject.get("message").getAsString(), "Customer created successfully");

    }



    private String createURLWithPort() {
        return "http://localhost:" + port + "/api/v1/customer";
    }

}
