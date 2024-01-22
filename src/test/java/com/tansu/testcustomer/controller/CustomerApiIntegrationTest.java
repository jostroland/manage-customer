package com.tansu.testcustomer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.mapper.CustomerMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.datafaker.Faker;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Objects;

import static com.tansu.testcustomer.utils.Constants.TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@ActiveProfiles(TEST)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerApiIntegrationTest {
    private final static Faker faker = new Faker();
    private final CustomerDto customerDto = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,60)).build();
    private static MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .alwaysExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .apply(springSecurity())
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("test create customer")
    @WithMockUser("user@gmail.com")
    public void test_create_customer() throws Exception {
        val result = mockMvc
                .perform(post("/api/v1/customer/save")
                        .content(Objects.requireNonNull(new ObjectMapper().writeValueAsString(customerDto)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
    }

    @Test
    @Order(2)
    @DisplayName("test all customers")
    @WithMockUser("user@gmail.com")
    public void test_customers_list() throws Exception {
         val result = mockMvc.perform(get("/api/v1/customer/all"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*").exists())
                .andReturn();
         log.info("result.getResponse().getContentAsString() {}",result.getResponse().getContentAsString());
         assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    @Order(3)
    @DisplayName("test customers by page")
    @WithMockUser("user@gmail.com")
    public void test_customers_by_page() throws Exception {
        val response = mockMvc.perform(get("/api/v1/customer/all/page")
                .contentType(MediaType.APPLICATION_JSON)
                .param("page","1")
                .param("size", "2"));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(4)
    @DisplayName("test find by id customers")
    @WithMockUser("user@gmail.com")
    public void test_find_by_id_customer() throws Exception {
        val result = mockMvc.perform(get("/api/v1/customer/findById/1"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    @Order(5)
    @DisplayName("test update by id customer")
    @WithMockUser("user@gmail.com")
    public void test_update_by_id_customer() throws Exception {
        var customer = CustomerMapper.toEntity(customerDto);
        customer.setFirstName("Jost");

        val response = mockMvc.perform(put("/api/v1/customer/update/1")
                .content(Objects.requireNonNull(new ObjectMapper().writeValueAsString(CustomerMapper.toDto(customer))))
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].firstName", CoreMatchers.is(customer.getFirstName())));
    }

    @Test
    @Order(6)
    @DisplayName("test delete by id customer")
    @WithMockUser("user@gmail.com")
    public void test_delete_by_id_customer() throws Exception {
        val response = mockMvc.perform(delete("/api/v1/customer/delete/1"));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }
}
