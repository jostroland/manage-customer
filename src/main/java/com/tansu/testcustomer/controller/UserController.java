package com.tansu.testcustomer.controller;


import com.tansu.testcustomer.controller.api.UserApi;
import com.tansu.testcustomer.dto.HttpResponse;
import com.tansu.testcustomer.dto.UserDto;
import com.tansu.testcustomer.dto.UserRequest;
import com.tansu.testcustomer.services.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserServiceImpl userService;

    @Override
    public ResponseEntity<HttpResponse<UserDto>> saveUser(UserRequest userRequest) {
        return ResponseEntity.created(
                URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/all").toUriString())
        ).body(userService.save(userRequest));
    }

    @Override
    public ResponseEntity<HttpResponse<UserDto>> updateUser(Integer id, UserRequest userRequest) {
        return ResponseEntity.ok().body(userService.update(id,userRequest));
    }

    @Override
    public ResponseEntity<HttpResponse<UserDto>> findUserById(Integer id) {
        return ResponseEntity.ok().body(userService.findById(id));
    }

    @Override
    public ResponseEntity<HttpResponse<List<UserDto>>> findAllUsers() {
        return ResponseEntity.ok().body(userService.findAll());
    }

    @Override
    public ResponseEntity<HttpResponse<Map<String, Object>>> findAllUsersByPage(int page, int size) {
        return ResponseEntity.ok().body(userService.findAll(page, size));
    }

    @Override
    public ResponseEntity<HttpResponse<UserDto>> deleteUser(Integer id) {
        return ResponseEntity.ok().body(userService.delete(id));
    }
}
