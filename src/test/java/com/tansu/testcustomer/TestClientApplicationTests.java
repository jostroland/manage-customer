
package com.tansu.testcustomer;

import com.tansu.testcustomer.controller.CustomerController;
import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.enumeration.Sex;
import com.tansu.testcustomer.repository.CustomerRepository;
import com.tansu.testcustomer.utils.Constants;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static com.tansu.testcustomer.utils.Constants.*;
import static com.tansu.testcustomer.utils.Constants.CREATE_CUSTOMER_ENDPOINT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testcontainers.shaded.org.hamcrest.collection.IsCollectionWithSize.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CustomersApplicationTests {

    @Autowired
    private static MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    public static final String APP_SLASH = "/";


    @BeforeAll
    static void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(CustomerController.class)
                .build();
    }


    @Test
    void should_create_one_user() throws Exception {
        var customer = CustomerDto.builder()
                .firstName("Julien Mael")
                .lastName("Arnaud")
                .sex(Sex.MALE)
                .email("laura.royer@yahoo.fr")
                .build();

        mockMvc.perform(post(APP_SLASH+CREATE_CUSTOMER_ENDPOINT)
                        .contentType(APPLICATION_JSON)
                        .content(customer.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());

        Assertions.assertEquals(this.customerRepository.findAll().size(),11);
    }

    @Test
    void should_retrieve_one_user() throws Exception {
        mockMvc.perform(get(APP_SLASH+FIND_BY_ID_CUSTOMER_ENDPOINT, 11))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.firstName").value("Julien Mael"))
                .andExpect(jsonPath("$.lastName").value("Arnaud"))
                .andExpect(jsonPath("$.sex").value(Sex.MALE))
                .andExpect(jsonPath("$.email").value("laura.royer@yahoo.fr"));
    }


    @Test
    void should_retrieve_all_customers() throws Exception {

        mockMvc.perform(get(APP_SLASH+FIND_ALL_CUSTOMER_ENDPOINT)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*").value(hasSize(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[3].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[4].id").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[5].id").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[6].id").value(6))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[7].id").value(7))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[8].id").value(8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[9].id").value(9))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[10].id").value(10));

       //9 Assertions.assertEquals(this.customerRepository.findAll().size(),11);
    }

    @Test
    void should_delete_one_customer() throws Exception {
        mockMvc.perform(delete(APP_SLASH+DELETE_BY_ID_CUSTOMER_ENDPOINT, 2))
                .andDo(print())
                .andExpect(status().isOk());

        Assertions.assertEquals(this.customerRepository.findAll().size(),9);
    }



}

