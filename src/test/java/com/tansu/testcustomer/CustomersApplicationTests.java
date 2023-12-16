
package com.tansu.testcustomer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tansu.testcustomer.controller.CustomerController;
import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.dto.UserRequest;
import com.tansu.testcustomer.entities.Customer;
import com.tansu.testcustomer.repository.CustomerRepository;
import com.tansu.testcustomer.repository.UserRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
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
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomersApplicationTests {

    private final Logger LOG = LoggerFactory.getLogger(CustomersApplicationTests.class);

    @Autowired private CustomerRepository customerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @LocalServerPort private Integer port;
    private final static Faker faker = new Faker();


    private final static List<CustomerDto> customersDtos = new ArrayList<>();
    private final static List<UserRequest> users         = new ArrayList<>();


    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:11.1");



    static {

        CustomerDto emp1 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,50)).build();
        CustomerDto emp2 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,50)).build();
        CustomerDto emp3 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,50)).build();
        CustomerDto emp4 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,50)).build();
        CustomerDto emp5 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,50)).build();

        customersDtos.addAll(List.of(emp1,emp2,emp3,emp4,emp5));

        UserRequest admin = UserRequest.builder()
                .name("admin")
                .email("admin@gmail.com")
                .password("password")
                .roles("ROLE_ADMIN").build();

        UserRequest user = UserRequest.builder()
                .name("user")
                .email("user@gmail.com")
                .password("password")
                .roles("ROLE_USER").build();

        UserRequest adminUser = UserRequest.builder()
                .name("adminuser")
                .email("adminuser@gmail.com")
                .password("password")
                .roles("ROLE_ADMIN,ROLE_USER").build();

        users.addAll(List.of(admin,user,adminUser));
    }

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


//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders
//                .standaloneSetup(CustomerController.class)
//                .build();
//    }


    @Test
    @Order(value = 1)
    void testConnectionToDatabase() {
        Assertions.assertNotNull(customerRepository);
        Assertions.assertNotNull(userRepository);
    }




    @Test
    @Order(value = 2)
    @WithMockUser(username = "admin@gmail.com", roles = { "USER", "ADMIN" })
    void testAddEmployees() throws Exception {
        for (UserRequest userRequest : users) {
            String emp = objectMapper.writeValueAsString(userRequest);
            mockMvc.perform(MockMvcRequestBuilders.post(CREATE_USER_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
                    .content(emp)).andExpect(status().isCreated());
        }

        LOG.info(() ->  String.valueOf("SIZE " + userRepository.findAll().size()));
        Assertions.assertEquals(5, userRepository.findAll().size());
    }
//
//    @Test
//    @Order(value = 3)
//    @WithMockUser(username = "admin@gmail.com", roles = { "ADMIN" })
//    void testGetAllEmployees() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees")).andExpect(status().isOk());
//        Assertions.assertEquals(employees.get(3).getName(), employeeRepository.findById(4).get().getName());
//    }
//
//    @Test
//    @Order(value = 4)
//    @WithMockUser(username = "user@gmail.com", roles = { "USER" })
//    void testGetAllEmployeesFailed() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees")).andExpect(status().isForbidden());
//    }
//
//    @Test
//    @Order(value = 5)
//    @WithMockUser(username = "user@gmail.com", roles = { "USER" })
//    void testGetEmployeeById() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees/2")).andExpect(status().isOk());
//        Assertions.assertEquals(employees.get(1).getName(), employeeRepository.findById(2).get().getName());
//    }
//
//    @Test
//    @Order(value = 6)
//    @WithMockUser(username = "admin@gmail.com", roles = { "ADMIN" })
//    void testGetEmployeeByIdFailed() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees/2")).andExpect(status().isForbidden());
//        Assertions.assertEquals(employees.get(1).getName(), employeeRepository.findById(2).get().getName());
//    }
//
//    @Test
//    @Order(value = 7)
//    @WithMockUser(username = "admin@gmail.com", roles = { "USER", "ADMIN" })
//    void testDeleteEmployeeById() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/employees/2")).andExpect(status().isOk());
//    }
//
//    @Test
//    @Order(value = 8)
//    @WithMockUser(username = "admin@gmail.com", roles = { "USER", "ADMIN" })
//    void testUpdateEmployee() throws Exception {
//        Employee employee = Employee.builder().id(3).name("Saurav Kumar Shah").address("India East").build();
//        String emp = objectMapper.writeValueAsString(employee);
//        mockMvc.perform(
//                        MockMvcRequestBuilders.put("/api/v1/employees").contentType(MediaType.APPLICATION_JSON).content(emp))
//                .andExpect(status().isOk());
//        Assertions.assertEquals(employee.getName(), employeeRepository.findById(3).get().getName());
//    }
//
//    @Test
//    @Order(value = 9)
//    @WithMockUser(username = "admin@gmail.com", roles = { "USER", "ADMIN" })
//    void testDeleteAllEmployees() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/employees")).andExpect(status().isOk());
//        Assertions.assertEquals(0, employeeRepository.findAll().size());
//    }
//
//    @Test
//    void testAddUsers() throws Exception {
//        for (UserInfoRequest user : users) {
//            String usr = objectMapper.writeValueAsString(user);
//            mockMvc.perform(
//                            MockMvcRequestBuilders.post("/api/v1/user").contentType(MediaType.APPLICATION_JSON).content(usr))
//                    .andExpect(status().isCreated());
//        }
//        Assertions.assertEquals(3, userInfoRepository.findAll().size());
//    }
//
//    @Test
//    void testGetUserByUsername() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user")).andExpect(status().isOk());
//        Assertions.assertEquals(users.get(1).getName(),
//                userInfoRepository.findByEmail("user@gmail.com").get().getName());
//    }







}

