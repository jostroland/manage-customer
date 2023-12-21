//package com.tansu.testcustomer.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import com.tansu.testcustomer.dto.UserRequest;
//import com.tansu.testcustomer.repository.UserRepository;
//import com.tansu.testcustomer.services.UserServiceImpl;
//import com.tansu.testcustomer.utils.Constants;
//import lombok.extern.slf4j.Slf4j;
//import org.json.JSONException;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.*;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.test.context.support.WithUserDetails;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.jdbc.Sql;
//
//import java.util.Objects;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//
//@Slf4j
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
////@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
//class UserApiIntergrationTest {
//
//    @LocalServerPort
//    private int port;
//
//
//    @Autowired private TestRestTemplate restTemplate;
//    @Autowired private  PasswordEncoder passwordEncoder;
//
//
//    private static HttpHeaders headers;
//    private JsonObject jsonObject;
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @BeforeAll
//    public static void init() {
//        headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//    }
//
//    @Test
//    @DisplayName("test all users")
//    @Sql(statements = "INSERT INTO users (id, name, email, password,roles) VALUES (3, 'jost', 'jost@gmal.com','$2a$10$yMSEJhNgxY.Sw/D41ulDAuesx69bCwm9NO8C3F0Cf8LKCdVpa71F6', 'ROLE_USER')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(statements = "DELETE FROM users WHERE id='3'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    public void test_users_list()  {
//        HttpEntity<String> entity = new HttpEntity<>(null, headers);
//        ResponseEntity<String> response = restTemplate.exchange(
//                createURLWithPort()+"/all", HttpMethod.GET, entity, new ParameterizedTypeReference<>(){});
//
//
//        jsonObject = JsonParser.parseString(Objects.requireNonNull(response.getBody()))
//                .getAsJsonObject();
//
//        assertEquals(response.getStatusCodeValue(), 200);
//        assertTrue(!jsonObject.get("message").getAsString().equals("No users to display"));
//    }
//
//
//    @Test
//    @DisplayName("test find by id users")
//    @Sql(statements = "INSERT INTO users (id, name, email, password,roles) VALUES (5, 'roland', 'roland@gmal.com','$2a$10$yMSEJhNgxY.Sw/D41ulDAuesx69bCwm9NO8C3F0Cf8LKCdVpa71F6', 'ROLE_USER')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(statements = "DELETE FROM users WHERE id='5'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    public void test_find_by_id_user() throws JSONException {
//        HttpEntity<String> entity = new HttpEntity<>(null, headers);
//        ResponseEntity<String> response = restTemplate.exchange(
//                createURLWithPort()+"/findById/5", HttpMethod.GET, entity, new ParameterizedTypeReference<>(){});
//
//        jsonObject = JsonParser.parseString(Objects.requireNonNull(response.getBody()))
//                .getAsJsonObject();
//
//        assertEquals(response.getStatusCodeValue(), 200);
//        assertTrue(jsonObject.get("message").getAsString().equals("User founded successfully"));
//    }
//
//    @Test
//    @Sql(statements = "INSERT INTO users (id, name, email, password,roles) VALUES (13, 'arnaud', 'arnaud@gmal.com','$2a$10$yMSEJhNgxY.Sw/D41ulDAuesx69bCwm9NO8C3F0Cf8LKCdVpa71F6', 'ROLE_USER')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    public void test_delete_by_id_user()  {
//        HttpEntity<String> entity = new HttpEntity<>(null, headers);
//        ResponseEntity<String> response = restTemplate.exchange(
//                (createURLWithPort() + "/delete/13"), HttpMethod.DELETE, entity, String.class);
//
//        jsonObject =
//                JsonParser
//                        .parseString(Objects.requireNonNull(response.getBody()))
//                        .getAsJsonObject();
//
//        assertEquals(response.getStatusCodeValue(), 200);
//        assertTrue(jsonObject.get("message").getAsString().equals("User deleted successfully"));
//    }
//
//    @Test
//    @Sql(statements = "DELETE FROM users", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    public void test_create_user() throws JsonProcessingException {
//        UserRequest user = UserRequest.builder().name("admin").email("admin@gmail.com").password(passwordEncoder.encode("password")).roles("ROLE_ADMIN").build();
//
//        final String stringuser = objectMapper.writeValueAsString(user);
//        HttpEntity<String> entity = new HttpEntity<>(stringuser, headers);
//        ResponseEntity<String> response = restTemplate.exchange(
//                createURLWithPort()+"/save", HttpMethod.POST, entity, String.class);
//
//        jsonObject =
//                JsonParser
//                        .parseString(Objects.requireNonNull(response.getBody()))
//                        .getAsJsonObject();
//
//
//        assertEquals(jsonObject.get("message").getAsString(), "User created successfully");
//
//    }
//
//
//
//    private String createURLWithPort() {
//        return "http://localhost:" + port + "/api/v1/user";
//    }
//
//}