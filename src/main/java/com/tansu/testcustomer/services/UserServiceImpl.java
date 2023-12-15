package com.tansu.testcustomer.services;

import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.dto.HttpResponse;
import com.tansu.testcustomer.dto.UserDto;
import com.tansu.testcustomer.dto.UserRequest;
import com.tansu.testcustomer.entities.Customer;
import com.tansu.testcustomer.entities.User;
import com.tansu.testcustomer.exception.EntityNotFoundException;
import com.tansu.testcustomer.mapper.CustomerMapper;
import com.tansu.testcustomer.mapper.UserMapper;
import com.tansu.testcustomer.repository.UserRepository;
import com.tansu.testcustomer.validation.ObjectsValidator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.tansu.testcustomer.util.DateUtil.dateTimeFormatter;
import static java.util.Collections.singleton;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.OK;


@Component
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService<UserDto,UserRequest>, UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final ObservationRegistry registry;
    private final ObjectsValidator<UserDto> validator;

    @Override
    public HttpResponse<UserDto> save(UserRequest userRequest) {
        log.info("Saving User to the database");

        User user = User.builder()
                .name(userRequest.name())
                .email(userRequest.email())
                .password(encoder.encode(userRequest.password()))
                .roles(userRequest.roles()).build();


        validator.validate(UserMapper.toDto(user));

        User userSave = repository.save(user);
        UserDto userDtoSave = UserMapper.toDto(userSave);


        return Observation.createNotStarted("save-user", registry)
                .observe(
                        HttpResponse.<UserDto>builder()
                                .data(Collections.singleton(
                                        UserMapper.toDto(userSave)
                                ))
                                .message("Customer created successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                                ::build
                );

    }



    @Override
    public HttpResponse<UserDto> update(Integer id, UserRequest dto) {

        log.info("Updating User to the database");


        Optional<User> optionalUser = ofNullable(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("This User was not found on the server")));

        UserDto userDto   = UserMapper.toDto(optionalUser.get());
        User user         = UserMapper.toEntity(userDto);

        validator.validate(userDto);

        User updateUser = optionalUser .orElseThrow(() -> new EntityNotFoundException("This User was not found on the server"));
        updateUser.setName(user.getName());
        updateUser.setEmail(user.getEmail());
        updateUser.setRoles(user.getRoles());
        repository.save(updateUser);

        return Observation.createNotStarted("update-Customer",registry)
                .observe(
                        HttpResponse.<UserDto>builder()
                                .data(Collections.singleton(
                                        UserMapper.toDto(updateUser)
                                ))
                                .message("User updated successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                                ::build
                );

    }

    @Override
    public HttpResponse<UserDto> findById(Integer id) throws EntityNotFoundException {
        log.info("Updating user to the database");
        if(isNull(id)){
            log.error("The ID must not be null");
            return null;
        }

        log.info("FindById user from the database by id {}", id);
        Optional<User> optionalUser = ofNullable(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("This user was not found on the server")));


        return Observation.createNotStarted("find-by-id-user",registry)
                .observe(
                        HttpResponse.<UserDto>builder()
                                .data(Collections.singleton(
                                        UserMapper.toDto(optionalUser.orElseThrow(()-> new NoSuchElementException("This user was not found on the server")))
                                ))
                                .message("User founded successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                                ::build
                );
    }

    @Override
    public HttpResponse<List<UserDto>> findAll() {
        log.info("Find all customers to the database");

        List<UserDto> userDtos = repository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();


        return Observation.createNotStarted("find-all-users",registry)
                .observe(
                        HttpResponse.<List<UserDto>>builder()
                                .data(singleton(userDtos))
                                .message(repository.count() > 0 ? repository.count() + " users retrieved" : "No users to display")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                                ::build
                );
    }

    @Override
    public HttpResponse<Map<String, Object>> findAll(int page, int size) {

        log.info("Find all by page users to the database");

        Pageable pageable = PageRequest.of(page, size);
        var userPage = repository.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        var content = userPage.getContent().stream().map(UserMapper::toDto).collect(Collectors.toList());

        response.put("userDto", content);
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());

        return  Observation.createNotStarted("find-all-user-page",registry)
                .observe(
                        HttpResponse.<Map<String, Object>>builder()
                                .pageCustomers(response)
                                .message("Users found successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))::build
                );
    }

    @Override
    public HttpResponse<UserDto> delete(Integer id) throws EntityNotFoundException {
        log.info("Deleting User from the database by id {}", id);
        if(isNull(id)){
            log.error("The ID must not be null");
            return null;
        }

        log.info("Deleting note from the database by id {}", id);
        Optional<User> optionalUser = ofNullable(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("This User was not found on the server")));

        optionalUser.ifPresent(repository::delete);

        return Observation.createNotStarted("delete-user",registry)
                .observe(
                        () -> HttpResponse.<UserDto>builder()
                                .data(Collections.singleton(
                                        UserMapper.toDto(optionalUser.orElseThrow(()-> new NoSuchElementException("This User was not found on the server")))
                                ))
                                .message("User deleted successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                                .build()
                );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws EntityNotFoundException {
        Optional<User> user = repository.findByEmail(username);

        return user.map(com.tansu.testcustomer.services.UserDetailsService::new)
                .orElseThrow(() -> new EntityNotFoundException("Given user not found : " + username));

    }


}
