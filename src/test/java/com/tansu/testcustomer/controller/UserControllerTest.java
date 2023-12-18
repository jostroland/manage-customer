//package com.tansu.testcustomer.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.tansu.testcustomer.dto.CustomerDto;
//import com.tansu.testcustomer.dto.UserDto;
//import com.tansu.testcustomer.dto.UserRequest;
//import com.tansu.testcustomer.entities.User;
//import com.tansu.testcustomer.mapper.UserMapper;
//import com.tansu.testcustomer.repository.CustomerRepository;
//import com.tansu.testcustomer.repository.UserRepository;
//import com.tansu.testcustomer.services.UserServiceImpl;
//import com.tansu.testcustomer.validation.ObjectsValidator;
//import lombok.extern.slf4j.Slf4j;
//import net.datafaker.Faker;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.testcontainers.containers.PostgreSQLContainer;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.tansu.testcustomer.utils.Constants.CREATE_USER_ENDPOINT;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@Slf4j
//@WebMvcTest
//@AutoConfigureMockMvc(addFilters = false)
//@ExtendWith(MockitoExtension.class)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class UserControllerTest {
//
//
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired private ObjectMapper objectMapper;
//
//    @Mock
//    private CustomerRepository customerRepository;
//    @Mock static UserRepository userRepository;
//    @Mock static PasswordEncoder passwordEncoder;
//    @Mock static ObjectsValidator<UserDto> validator;
//    @InjectMocks
//    static UserServiceImpl userService;
//
//    @LocalServerPort
//    private Integer port;
//
//    private final static Faker faker = new Faker();
//
//
//    private final static List<CustomerDto> customersDtos = new ArrayList<>();
//    private final static List<UserRequest> users         = new ArrayList<>();
//    private static List<User> listOfUsers                = new ArrayList<>();
//
//    private static UserRequest admin;
//    private static UserRequest user;
//    private static UserRequest adminUser;
//
//
//    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:11.1");
//
//    @BeforeEach
//    void setUp() {
//        admin = UserRequest.builder().name("admin").email("admin@gmail.com").password(passwordEncoder.encode("password")).roles("ROLE_ADMIN").build();
//        user = UserRequest.builder().name("user").email("user@gmail.com").password(passwordEncoder.encode("password")).roles("ROLE_USER").build();
//        adminUser = UserRequest.builder().name("adminuser").email("adminuser@gmail.com").password(passwordEncoder.encode("password")).roles("ROLE_ADMIN,ROLE_USER").build();
//
//        users.addAll(List.of(admin,user,adminUser));
//        listOfUsers = users.stream().map(UserMapper::fromRequestToEntity).toList();
//    }
//
//    @AfterEach
//    void tearDown() {
//
//    }
//
//    @Test
//    @Order(value = 1)
//    void testConnectionToDatabase() {
//        Assertions.assertNotNull(customerRepository);
//        Assertions.assertNotNull(userRepository);
//    }
//
//
//
//
//    @Test
//    @Order(value = 2)
//    @WithMockUser(username = "admin@gmail.com", roles = { "ROLE_USER", "ROLE_ADMIN" })
//    void testAddUsers() throws Exception {
//        for (UserRequest userRequest : ) {
//            String user = objectMapper.writeValueAsString(userRequest);
//            mockMvc.perform(MockMvcRequestBuilders.post(CREATE_USER_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
//                    .content(user)).andExpect(status().isCreated());
//        }
//
//        //log.info(() ->  "SIZE " + userRepository.findAll().size());
//        assertEquals(5, userRepository.findAll().size());
//    }
//
//    @Test
//    @Order(value = 3)
//    @WithMockUser(username = "admin@gmail.com", roles = { "ADMIN" })
//    void testGetAll() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users")).andExpect(status().isOk());
//        Assertions.assertEquals(users.get(3).name(), userRepository.findById(4).get().getName());
//    }
//
//    @Test
//    @Order(value = 4)
//    @WithMockUser(username = "user@gmail.com", roles = { "USER" })
//    void testGetAllUsersFailed() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/Users")).andExpect(status().isForbidden());
//    }
//
//    @Test
//    @Order(value = 5)
//    @WithMockUser(username = "user@gmail.com", roles = { "USER" })
//    void testGetEmployeeById() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/2")).andExpect(status().isOk());
//        Assertions.assertEquals(users.get(1).name(), userRepository.findById(2).get().getName());
//    }
//
//    @Test
//    @Order(value = 6)
//    @WithMockUser(username = "admin@gmail.com", roles = { "ADMIN" })
//    void testGetEmployeeByIdFailed() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/2")).andExpect(status().isForbidden());
//        Assertions.assertEquals(users.get(1).name(), userRepository.findById(2).get().getName());
//    }
//
//    @Test
//    @Order(value = 7)
//    @WithMockUser(username = "admin@gmail.com", roles = { "USER", "ADMIN" })
//    void testDeleteEmployeeById() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/2")).andExpect(status().isOk());
//    }
//
//    @Test
//    @Order(value = 8)
//    @WithMockUser(username = "admin@gmail.com", roles = { "USER", "ADMIN" })
//    void testUpdateUser() throws Exception {
//        User user1 = User.builder().id(3).name("Saurav Kumar Shah").email("India East").build();
//        String emp = objectMapper.writeValueAsString(user1);
//        mockMvc.perform(
//                        MockMvcRequestBuilders.put("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(emp))
//                .andExpect(status().isOk());
//        Assertions.assertEquals(user1.getName(), userRepository.findById(3).get().getName());
//    }
//
//    @Test
//    @Order(value = 9)
//    @WithMockUser(username = "admin@gmail.com", roles = { "USER", "ADMIN" })
//    void testDeleteAllUsers() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users")).andExpect(status().isOk());
//        Assertions.assertEquals(0, userRepository.findAll().size());
//    }
//
//    @Test
//    void testAddUsers() throws Exception {
//        for (UserRequest userRequest : users) {
//            String usr = objectMapper.writeValueAsString(userRequest);
//            mockMvc.perform(
//                            MockMvcRequestBuilders.post("/api/v1/user").contentType(MediaType.APPLICATION_JSON).content(usr))
//                    .andExpect(status().isCreated());
//        }
//        Assertions.assertEquals(3, userRepository.findAll().size());
//    }
//
//    @Test
//    void testGetUserByUsername() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user")).andExpect(status().isOk());
//        Assertions.assertEquals(users.get(1).name(),
//                userRepository.findByEmail("user@gmail.com").get().getName());
//    }
//}