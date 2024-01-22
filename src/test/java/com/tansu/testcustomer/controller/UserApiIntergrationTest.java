package com.tansu.testcustomer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tansu.testcustomer.dto.UserRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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


@Slf4j
@ActiveProfiles(TEST)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserApiIntergrationTest {
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
    @DisplayName("test create user")
    @WithMockUser("user@gmail.com")
    public void test_create_user() throws Exception {
        UserRequest userRequest =
                UserRequest.builder()
                        .name("user")
                        .email("user@gmail.com")
                        .password("password")
                        .roles("ROLE_USER").build();

        val response = mockMvc
                .perform(post("/api/v1/user/save")
                        .content(Objects.requireNonNull(new ObjectMapper().writeValueAsString(userRequest)))
                        .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @Order(2)
    @DisplayName("test all users")
    @WithMockUser("user@gmail.com")
    public void test_users_list() throws Exception {
        val response = mockMvc.perform(get("/api/v1/user/all"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.*").exists());

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(3)
    @DisplayName("test find by id users")
    @WithMockUser("user@gmail.com")
    public void test_find_by_id_user() throws Exception {
        val result = mockMvc.perform(get("/api/v1/user/findById/1")
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    @Order(4)
    @DisplayName("test delete by id users")
    @WithMockUser("user@gmail.com")
    public void test_delete_by_id_user() throws Exception {
        val response = mockMvc.perform(delete("/api/v1/user/delete/1")
                .contentType(MediaType.APPLICATION_JSON));
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }
}