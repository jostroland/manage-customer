package com.tansu.testcustomer.services;

import com.tansu.testcustomer.dto.HttpResponse;
import com.tansu.testcustomer.dto.UserDto;
import com.tansu.testcustomer.dto.UserRequest;
import com.tansu.testcustomer.entities.User;
import com.tansu.testcustomer.exception.EntityNotFoundException;
import com.tansu.testcustomer.mapper.UserMapper;
import com.tansu.testcustomer.repository.UserRepository;
import com.tansu.testcustomer.validation.ObjectsValidator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.tansu.testcustomer.utils.DateUtil.DATE_TIME_FORMATTER;
import static java.util.Collections.singleton;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@NoArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService<UserDto,UserRequest>, UserDetailsService {

    private  PasswordEncoder passwordEncoder;
    private  UserRepository repository;
    private  ObservationRegistry registry;
    private  ObjectsValidator<UserDto> validator;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository repository, ObservationRegistry registry, ObjectsValidator<UserDto> validator) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
        this.registry = registry;
        this.validator = validator;
    }

    @Override
    public HttpResponse<UserDto> save(UserRequest userRequest) {
        log.info("Saving user to the database");
        var user = User.builder()
                .name(userRequest.name())
                .email(userRequest.email())
                .password(passwordEncoder.encode(userRequest.password()))
                .roles(userRequest.roles()).build();

        validator.validate(UserMapper.toDto(user));

        var userSave = repository.save(user);
        var userDtoSave = UserMapper.toDto(userSave);

        return Observation.createNotStarted("save-user", registry)
                .observe(
                        HttpResponse.<UserDto>builder()
                                .data(singleton(
                                        userDtoSave
                                ))
                                .message("User created successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                                ::build
                );
    }

    @Override
    public HttpResponse<UserDto> update(Integer id, UserRequest dto) {
        log.info("Updating User to the database");
        validator.validate(UserMapper.fromRequestToDto(dto));

        val optionalUser = ofNullable(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("This User was not found on the server")));

        var updateUser = optionalUser .orElseThrow(() -> new EntityNotFoundException("This User was not found on the server"));
        updateUser.setName(dto.name());
        updateUser.setEmail(dto.email());
        updateUser.setPassword(dto.password());
        updateUser.setRoles(dto.roles());
        repository.save(updateUser);

        return Observation.createNotStarted("update-user",registry)
                .observe(
                        HttpResponse.<UserDto>builder()
                                .data(singleton(
                                        UserMapper.toDto(updateUser)
                                ))
                                .message("User updated successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                                ::build
                );
    }

    @Override
    public HttpResponse<UserDto> findById(Integer id) throws EntityNotFoundException {
        log.info("FindById user from the database by id {}", id);
        if(isNull(id)){
            log.error("The ID must not be null");
            return null;
        }

        val optionalUser = ofNullable(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("This user was not found on the server")));

        return Observation.createNotStarted("find-by-id-user",registry)
                .observe(
                        HttpResponse.<UserDto>builder()
                                .data(singleton(
                                        UserMapper.toDto(optionalUser.orElseThrow(()-> new NoSuchElementException("This user was not found on the server")))
                                ))
                                .message("User founded successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                                ::build
                );
    }

    @Override
    public HttpResponse<List<UserDto>> findAll() {
        log.info("Find all users to the database");
        val userDtos = repository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();

        return Observation.createNotStarted("find-all-users",registry)
                .observe(
                        HttpResponse.<List<UserDto>>builder()
                                .data(singleton(userDtos))
                                .message(repository.count() > 0 ? repository.count() + " users retrieved" : "No users to display")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                                ::build
                );
    }

    @Override
    public HttpResponse<Map<String, Object>> findAll(int page, int size) {
        log.info("Find all by page users to the database");
        Pageable pageable = PageRequest.of(page, size);
        var userPage = repository.findAll(pageable);

        var response = new HashMap<String, Object>();
        var content = userPage.getContent().stream().map(UserMapper::toDto).collect(Collectors.toList());

        response.put("userDto", content);
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());

        return  Observation.createNotStarted("find-all-user-page",registry)
                .observe(
                        HttpResponse.<Map<String, Object>>builder()
                                .pageUsers(response)
                                .message("Users found successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))::build
                );
    }

    @Override
    public HttpResponse<UserDto> delete(Integer id) throws EntityNotFoundException {
        log.info("Deleting User from the database by id {}", id);
        if(isNull(id)){
            log.error("The ID must not be null");
            return null;
        }

        val optionalUser = ofNullable(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("This User was not found on the server")));

        optionalUser.ifPresent(repository::delete);

        return Observation.createNotStarted("delete-user",registry)
                .observe(
                        () -> HttpResponse.<UserDto>builder()
                                .data(singleton(
                                        UserMapper.toDto(optionalUser.orElseThrow(()-> new NoSuchElementException("This User was not found on the server")))
                                ))
                                .message("User deleted successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                                .build()
                );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws EntityNotFoundException {
        val user = repository.findByEmail(username);

        return user.map(com.tansu.testcustomer.services.UserDetailsService::new)
                .orElseThrow(() -> new EntityNotFoundException("Given user not found : " + username));
    }
}

