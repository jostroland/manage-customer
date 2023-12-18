package com.tansu.testcustomer.services;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.dto.HttpResponse;
import com.tansu.testcustomer.dto.UserDto;
import com.tansu.testcustomer.dto.UserRequest;
import com.tansu.testcustomer.entities.Customer;
import com.tansu.testcustomer.entities.User;
import com.tansu.testcustomer.exception.EntityNotFoundException;
import com.tansu.testcustomer.exception.ObjectValidationException;
import com.tansu.testcustomer.mapper.CustomerMapper;
import com.tansu.testcustomer.mapper.UserMapper;
import com.tansu.testcustomer.repository.UserRepository;
import com.tansu.testcustomer.validation.ObjectsValidator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.tansu.testcustomer.utils.DateUtil.dateTimeFormatter;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Slf4j
class UserServiceImplTest {

     @Mock
     static UserRepository userRepository;

     @Mock
     static PasswordEncoder passwordEncoder;

     @Mock
     static ObjectsValidator<UserDto> validator;

     @InjectMocks
     static UserServiceImpl userService;


    private final static List<UserRequest> users  = new ArrayList<>();
    private static List<User> listOfUsers   = new ArrayList<>();

    private UserRequest admin;
    private UserRequest user;
    private UserRequest adminUser;



    @BeforeEach
    public void init() {
        admin = UserRequest.builder().name("admin").email("admin@gmail.com").password(passwordEncoder.encode("password")).roles("ROLE_ADMIN").build();
        user = UserRequest.builder().name("user").email("user@gmail.com").password(passwordEncoder.encode("password")).roles("ROLE_USER").build();
        adminUser = UserRequest.builder().name("adminuser").email("adminuser@gmail.com").password(passwordEncoder.encode("password")).roles("ROLE_ADMIN,ROLE_USER").build();

        users.addAll(List.of(admin,user,adminUser));
        listOfUsers = users.stream().map(UserMapper::fromRequestToEntity).toList();
    }



    @Test
    void should_create_user_with_success() {
        log.info("should_create_user_with_success");

        UserRequest adminUser = UserRequest.builder()
                .name("adminuser")
                .email("adminuser@gmail.com")
                .password(passwordEncoder.encode("password"))
                .roles("ROLE_ADMIN,ROLE_USER").build();

        User user = UserMapper.fromRequestToEntity(adminUser);


        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        HttpResponse<UserDto> save = userService.save(adminUser);
        UserDto userDto = save.data().stream().findFirst().get();

        Assertions.assertNotNull(save);
        Assertions.assertEquals(userDto.name(),user.getName());
        Assertions.assertEquals(userDto.email(),user.getEmail());


    }




    @Test
    void should_create_user_with_error() {
        UserRequest adminUser = UserRequest.builder()
                .password(passwordEncoder.encode("password")).build();

        ObjectValidationException mock = mock(ObjectValidationException.class);
        when(userRepository.save(Mockito.any(User.class))).thenThrow(mock);

        ObjectValidationException objectValidationException =
                        assertThrows(ObjectValidationException.class, () -> userService.save(adminUser));


        Assertions.assertNotNull(objectValidationException);
        Assertions.assertEquals(objectValidationException.getViolations(),mock.getViolations());
    }

    @Test
    void should_update_user_with_success() {
        log.info("should_update_user_with_success");

        when(userRepository.findById(user.id())).thenReturn(Optional.of(UserMapper.fromRequestToEntity(user)));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(UserMapper.fromRequestToEntity(user));

        UserDto userDto = userService.update(user.id(), user).data().stream().findFirst().get();

        assertNotNull(userDto);
    }



    @Test
    void should_find_by_id() {
        int id = 1;
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(UserMapper.fromRequestToEntity(user)));

        List<? extends UserDto> list = userService.findById(id).data().stream().toList();
        assertNotNull(list);
    }


    @Test
    void should_find_user_by_id_is_null() {
        log.info("should_find_user_by_id_is_null");
        when(userRepository.findById(Integer.MAX_VALUE)).thenReturn(null);

        NullPointerException nullPointerException =
                assertThrows(NullPointerException.class, () -> userService.findById(Integer.MAX_VALUE));

        Assertions.assertNotNull(nullPointerException);
    }

    @Test
    void should_find_all_with_success() {
        log.info("should_find_all_with_success");

        when(userRepository.findAll())
                .thenReturn(listOfUsers);
        //test
        HttpResponse<List<UserDto>> userServiceAll = userService.findAll();
        List<UserDto> list = userServiceAll.data().stream().flatMap(Collection::stream).toList();

        verify(userRepository).findAll();
        assertEquals(listOfUsers.size(), list.size());
    }


    @Test
    void should_find_all_with_error_message() {
        log.info("should_find_all_with_error_message");
        when(userRepository.findAll())
                .thenReturn(List.of());
        //test
        HttpResponse<List<UserDto>> userServiceAll = userService.findAll();

        verify(userRepository).findAll();
        assertEquals(userServiceAll.message(), "No users to display");


    }


    @Test
    void should_find_all_user_page_with_success() {
        log.info("should_find_all_user_page_with_success"); //HttpResponse<Map<String, Object>> users = Mockito.mock(HttpResponse.class);
        Page userPage =  mock(Page.class);
        when(userRepository.findAll(Mockito.any(Pageable.class))).thenReturn(userPage);

        HttpResponse<Map<String, Object>> userServiceAll = userService.findAll(1, 3);

        Assertions.assertNotNull(userServiceAll.pageCustomers());
    }




    @Test
    void should_delete() {
        log.info("should_delete");

        User user = UserMapper.fromRequestToEntity(adminUser);

        int id = 1;
        when(userRepository.findById(id)).thenReturn(Optional.of(adminUser).map(UserMapper::fromRequestToEntity));
        doNothing().when(userRepository).delete(user);

        HttpResponse<UserDto> deleted = userService.delete(id);

        Assertions.assertNotNull(deleted);
        Assertions.assertEquals(deleted.message(),"User deleted successfully");

    }


    @Test
    void should_load_user_by_username() {
        Optional<User> user = Optional.ofNullable(UserMapper.fromRequestToEntity(adminUser));

        when(userRepository.findByEmail(Mockito.any(String.class))).thenReturn(user);

        User userGet = user.get();
        UserDetails userDetails = userService.loadUserByUsername(userGet.getName());
        assertEquals(userDetails.getUsername(), userGet.getEmail());
        assertEquals(userDetails.getPassword(), userGet.getPassword());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isAccountNonLocked());

    }
}