package com.tansu.testcustomer.services;

import com.tansu.testcustomer.dto.HttpResponse;
import com.tansu.testcustomer.dto.UserDto;
import com.tansu.testcustomer.dto.UserRequest;
import com.tansu.testcustomer.entities.User;
import com.tansu.testcustomer.exception.ObjectValidationException;
import com.tansu.testcustomer.mapper.UserMapper;
import com.tansu.testcustomer.repository.UserRepository;
import com.tansu.testcustomer.validation.ObjectsValidator;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {
     @Mock static UserRepository userRepository;
     @Mock static PasswordEncoder passwordEncoder;
     @Mock static ObjectsValidator<UserDto> validator;

     @InjectMocks static UserServiceImpl userService;

    private final static List<UserRequest> users  = new ArrayList<>();
    private static List<User> listOfUsers   = new ArrayList<>();

    private UserRequest admin;
    private UserRequest user;
    private UserRequest adminUser;

    @BeforeEach
    public void init() {
        admin     = UserRequest.builder().name("admin").email("admin@gmail.com").password(passwordEncoder.encode("password")).roles("ROLE_ADMIN").build();
        user      = UserRequest.builder().name("user").email("user@gmail.com").password(passwordEncoder.encode("password")).roles("ROLE_USER").build();
        adminUser = UserRequest.builder().name("adminuser").email("adminuser@gmail.com").password(passwordEncoder.encode("password")).roles("ROLE_ADMIN,ROLE_USER").build();

        users.addAll(List.of(admin,user,adminUser));
        listOfUsers = users.stream().map(UserMapper::fromRequestToEntity).toList();
    }

    @Test
    @DisplayName("should create user with success")
    void should_create_user_with_success() {
        UserRequest adminUser = UserRequest.builder()
                .name("adminuser")
                .email("adminuser@gmail.com")
                .password(passwordEncoder.encode("password"))
                .roles("ROLE_ADMIN,ROLE_USER").build();

        User user = UserMapper.fromRequestToEntity(adminUser);

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        HttpResponse<UserDto> save = userService.save(adminUser);
        UserDto userDto = save.data().stream().findFirst().get();

        assertNotNull(save);
        assertEquals(userDto.name(),user.getName());
        assertEquals(userDto.email(),user.getEmail());
    }

    @Test
    @DisplayName("should create user with error")
    void should_create_user_with_error() {
        val adminUser = UserRequest.builder()
                .password(passwordEncoder.encode("password")).build();

        var mock = mock(ObjectValidationException.class);
        when(userRepository.save(Mockito.any(User.class))).thenThrow(mock);

        val objectValidationException =
                        assertThrows(ObjectValidationException.class, () -> userService.save(adminUser));

        assertNotNull(objectValidationException);
        assertEquals(objectValidationException.getViolations(),mock.getViolations());
    }

    @Test
    @DisplayName("should update user with error")
    void should_update_user_with_success() {
        when(userRepository.findById(user.id())).thenReturn(Optional.of(UserMapper.fromRequestToEntity(user)));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(UserMapper.fromRequestToEntity(user));

        val userDto = userService.update(user.id(), user).data().stream().findFirst().get();

        assertNotNull(userDto);
    }

    @Test
    @DisplayName("should find by id")
    void should_find_by_id() {
        int id = 1;
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(UserMapper.fromRequestToEntity(user)));

        List<? extends UserDto> list = userService.findById(id).data().stream().toList();
        assertNotNull(list);
    }

    @Test
    @DisplayName("should find user by id is null")
    void should_find_user_by_id_is_null() {
        when(userRepository.findById(Integer.MAX_VALUE)).thenReturn(null);

        NullPointerException nullPointerException =
                assertThrows(NullPointerException.class, () -> userService.findById(Integer.MAX_VALUE));

        assertNotNull(nullPointerException);
    }

    @Test
    @DisplayName("should find all with success message")
    void should_find_all_with_success() {
        when(userRepository.findAll())
                .thenReturn(listOfUsers);

        var userServiceAll = userService.findAll();
        var list = userServiceAll.data().stream().flatMap(Collection::stream).toList();

        verify(userRepository).findAll();
        assertEquals(listOfUsers.size(), list.size());
    }

    @Test
    @DisplayName("should find all with error message")
    void should_find_all_with_error_message() {
        when(userRepository.findAll())
                .thenReturn(List.of());

        var userServiceAll = userService.findAll();

        verify(userRepository).findAll();
        assertEquals(userServiceAll.message(), "No users to display");
    }

    @Test
    @DisplayName("should find all user page with success")
    void should_find_all_user_page_with_success() {
        var userPage =  mock(Page.class);
        when(userRepository.findAll(Mockito.any(Pageable.class))).thenReturn(userPage);

        val userServiceAll = userService.findAll(1, 3);

        assertNotNull(userServiceAll.pageUsers());
    }

    @Test
    @DisplayName("should delete")
    void should_delete() {
        var user = UserMapper.fromRequestToEntity(adminUser);

        int id = 1;
        when(userRepository.findById(id)).thenReturn(Optional.of(adminUser).map(UserMapper::fromRequestToEntity));
        doNothing().when(userRepository).delete(user);

        val deleted = userService.delete(id);

        assertNotNull(deleted);
        assertEquals(deleted.message(),"User deleted successfully");
    }

    @Test
    @DisplayName("should load user by username")
    void should_load_user_by_username() {
        var user = Optional.ofNullable(UserMapper.fromRequestToEntity(adminUser));

        when(userRepository.findByEmail(Mockito.any(String.class))).thenReturn(user);

        var userGet = user.get();
        var userDetails = userService.loadUserByUsername(userGet.getName());
        assertEquals(userDetails.getUsername(), userGet.getEmail());
        assertEquals(userDetails.getPassword(), userGet.getPassword());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
    }
}