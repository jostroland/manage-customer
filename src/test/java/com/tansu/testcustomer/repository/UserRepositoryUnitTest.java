package com.tansu.testcustomer.repository;

import com.tansu.testcustomer.dto.UserRequest;
import com.tansu.testcustomer.entities.Customer;
import com.tansu.testcustomer.entities.User;
import com.tansu.testcustomer.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryUnitTest {



    @Autowired private UserRepository userRepository;
    @Qualifier("passEncoder")
    @Autowired private PasswordEncoder passEncoder;


    private final Faker faker = new Faker();
    private List<UserRequest> userRequestList = new ArrayList<>();
    private List<User> users = new ArrayList<>(3);




    @BeforeEach
    public void setUp() {
         userRequestList = IntStream.rangeClosed(1, 3)
                .mapToObj(value -> {
                    String name = faker.name().firstName();
                    return UserRequest.builder()
                            .name(name)
                            .email("%s@gmail.com".formatted(name))
                            .password(passEncoder.encode("password"))
                            .roles(
                                    value % 2 == 0
                                            ? "ROLE_ADMIN"
                                            : "ROLE_USER"
                            ).build();
                }).toList();

        users = userRequestList.stream().map(UserMapper::fromRequestToEntity).toList();
        //Save all user in the database
        userRepository.saveAll(users);


    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("should save return user")
    public void should_save_return_user() {
        final User save = userRepository.save(users.get(1));
        assertNotNull(save);
    }

    @Test
    @DisplayName("should get all return more then one user")
    public void should_get_all__return_more_then_one_user() {
        List<User> userList = userRepository.findAll();

        assertNotNull(userList);
        assertEquals(userList.size(),3);
    }

    @Test
    @DisplayName("should find by id return user")
    public void should_find_by_id__return_user() {
        final User user = userRepository.findById(users.get(1).getId()).get();

        assertNotNull(user);
    }



    @Test
    @DisplayName("should update user return not null name")
    public void should_update_user_return_not_null_name() {
        final User user = userRepository.findById(users.get(1).getId()).get();

        final String newName = "New name";
        user.setName(newName);
        User  updatedUser = userRepository.save(user);

       assertEquals(updatedUser.getName(), newName);
    }

    @Test
    @DisplayName("should delete and return user empty")
    public void should_delete_and_return_user_empty() {
        final int id = 3;
        userRepository.deleteById(id);
        Optional<User> user = userRepository.findById(id);

        assertTrue(user.isEmpty());
    }
    
    @Test
    @DisplayName("should find by email and return same email")
    void should_find_by_email_and_return_same_email() {
        final User firstUser = users.get(1);
        final User user = userRepository.findByEmail(firstUser.getEmail()).get();

        assertNotNull(user);
        assertEquals(user.getEmail(), firstUser.getEmail());
    }
}