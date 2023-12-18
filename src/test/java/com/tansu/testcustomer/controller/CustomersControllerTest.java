//
//package com.tansu.testcustomer.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.tansu.testcustomer.Application;
//import com.tansu.testcustomer.TestCustomerAppApplication;
//import com.tansu.testcustomer.dto.CustomerDto;
//import com.tansu.testcustomer.dto.HttpResponse;
//import com.tansu.testcustomer.dto.UserDto;
//import com.tansu.testcustomer.dto.UserRequest;
//import com.tansu.testcustomer.entities.User;
//import com.tansu.testcustomer.mapper.CustomerMapper;
//import com.tansu.testcustomer.mapper.UserMapper;
//import com.tansu.testcustomer.repository.CustomerRepository;
//import com.tansu.testcustomer.repository.UserRepository;
//import com.tansu.testcustomer.services.CustomerServiceImpl;
//import com.tansu.testcustomer.services.UserServiceImpl;
//import com.tansu.testcustomer.validation.ObjectsValidator;
//import lombok.extern.slf4j.Slf4j;
//import net.datafaker.Faker;
//import org.hamcrest.CoreMatchers;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.platform.commons.logging.Logger;
//import org.junit.platform.commons.logging.LoggerFactory;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.util.*;
//
//import static com.tansu.testcustomer.utils.Constants.*;
//import static org.hamcrest.Matchers.aMapWithSize;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.testcontainers.shaded.org.hamcrest.collection.IsCollectionWithSize.hasSize;
//
//
//
//@Slf4j
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//@ExtendWith(MockitoExtension.class)
//class CustomersControllerTest {
//
//    @Autowired private MockMvc mockMvc;
//    @Autowired private ObjectMapper objectMapper;
//
//    @Mock private CustomerRepository customerRepository;
//    @Mock static PasswordEncoder passwordEncoder;
//    @Mock static ObjectsValidator<UserDto> validator;
//
//    @Autowired private WebApplicationContext webApplicationContext;
//
//    @InjectMocks static CustomerServiceImpl customerService;
//
//    private final static Faker faker = new Faker();
//    private final static List<CustomerDto> customersDtos = new ArrayList<>();
//
//    private CustomerDto customerDto1;
//    private CustomerDto customerDto2;
//    private CustomerDto customerDto3;
//    private CustomerDto customerDto4;
//    private CustomerDto customerDto5;
//
//
//
//    @BeforeEach
//    public void init() {
//         customerDto1 = CustomerDto.builder().id(1).firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,50)).build();
//         customerDto2 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,50)).build();
//         customerDto3 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,50)).build();
//         customerDto4 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,50)).build();
//         customerDto5 = CustomerDto.builder().firstName(faker.name().firstName()).lastName(faker.name().firstName()).age(faker.number().numberBetween(15,50)).build();
//         customersDtos.addAll(List.of(customerDto1,customerDto2,customerDto3,customerDto4,customerDto5));
//         customersDtos.stream().map(CustomerMapper::toEntity).forEach(customerRepository::save);
//    }
//
//    @Test
//    public void ReviewController_GetReviewsByPokemonId_ReturnReviewDto() throws Exception {
//        int id = 1;
//        when(customerService.findById(id)).thenReturn(HttpResponse.<CustomerDto>builder().data(Collections.singleton(customerDto1)).build());
//
//        ResultActions response = mockMvc.perform(get("/api/v1/customer/findById/%d".formatted(id))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(customerDto1)));
//
//        response.andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(customerDto1.firstName())));
//    }
//
////    @Test
////    public void ReviewController_UpdateReview_ReturnReviewDto() throws Exception {
////        int pokemonId = 1;
////        int reviewId = 1;
////        when(reviewService.updateReview(pokemonId, reviewId, reviewDto)).thenReturn(reviewDto);
////
////        ResultActions response = mockMvc.perform(put("/api/pokemon/1/reviews/1")
////                .contentType(MediaType.APPLICATION_JSON)
////                .content(objectMapper.writeValueAsString(reviewDto)));
////
////        response.andExpect(MockMvcResultMatchers.status().isOk())
////                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(reviewDto.getTitle())))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(reviewDto.getContent())))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.stars", CoreMatchers.is(reviewDto.getStars())));
////    }
////
////
////    @Test
////    public void ReviewController_CreateReview_ReturnReviewDto() throws Exception {
////
////        HttpResponse<CustomerDto> customerSaved  = HttpResponse.<CustomerDto>builder().data(Collections.singleton(customerDto1)).build();
////
////        when(customerService.save(customerDto1)).thenReturn(customerSaved);
////
////        ResultActions response = mockMvc.perform(post("/api/v1/customer/save")
////                .contentType(MediaType.APPLICATION_JSON)
////                .content(objectMapper.writeValueAsString(customerDto1)));
////
////        response.andExpect(MockMvcResultMatchers.status().isCreated())
////                .andDo(print())
////                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(customerSaved.data().stream().findFirst().get().firstName())))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(customerSaved.data().stream().findFirst().get().lastName())))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.age", CoreMatchers.is(customerSaved.data().stream().findFirst().get().age())));
////    }
////
////    @Test
////    public void ReviewController_GetReviewId_ReturnReviewDto() throws Exception {
////        int pokemonId = 1;
////        int reviewId = 1;
////        when(reviewService.getReviewById(reviewId, pokemonId)).thenReturn(reviewDto);
////
////        ResultActions response = mockMvc.perform(get(FIND_BY_ID_CUSTOMER_ENDPOINT+"1")
////                .contentType(MediaType.APPLICATION_JSON));
////
////        response.andExpect(MockMvcResultMatchers.status().isOk())
////                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(reviewDto.getTitle())))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(reviewDto.getContent())))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.stars", CoreMatchers.is(reviewDto.getStars())));
////    }
////
////    @Test
////    public void ReviewController_DeleteReview_ReturnOk() throws Exception {
////        int pokemonId = 1;
////        int reviewId = 1;
////
////        doNothing().when(reviewService).deleteReview(pokemonId, reviewId);
////
////        ResultActions response = mockMvc.perform(delete("/api/pokemon/1/reviews/1")
////                .contentType(MediaType.APPLICATION_JSON));
////
////        response.andExpect(MockMvcResultMatchers.status().isOk());
////    }
//
//
//
//
//
//
//
//
//
//
//
//}
//
