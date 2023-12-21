package com.tansu.testcustomer.controller.api;


import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.dto.HttpResponse;
import com.tansu.testcustomer.dto.UserDto;
import com.tansu.testcustomer.dto.UserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.tansu.testcustomer.utils.Constants.*;


@Tag(name = "Users", description = "Users API")
public interface UserApi {


    @PostMapping(value = CREATE_USER_ENDPOINT,consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "This method allows you to create a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User create"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    ResponseEntity<HttpResponse<UserDto>> saveUser(@RequestBody @Valid UserRequest userRequest);


    @PutMapping(value = UPDATE_USER_ENDPOINT,consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "This method allows you to modify a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User has been modified"),
            @ApiResponse(responseCode = "404", description = "User does not exist in the database")
    })
    ResponseEntity<HttpResponse<UserDto>> updateUser(@PathVariable("id") Integer id,@RequestBody @Valid UserRequest userRequest);



    @GetMapping (value =  FIND_BY_ID_USER_ENDPOINT,produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "This method allows you to search for a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was found in the database"),
            @ApiResponse(responseCode = "404", description = "No user was found in the database with the ID provided")
    })
    ResponseEntity<HttpResponse<UserDto>> findUserById(@PathVariable("id") Integer id);

    

    @GetMapping (value = FIND_ALL_USER_ENDPOINT,produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "This method allows you to select all customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was found in the database"),
            @ApiResponse(responseCode = "404", description = "No user was found in the database with the ID provided")
    })
    ResponseEntity<HttpResponse<List<UserDto>>> findAllUsers();

    @GetMapping (value = FIND_ALL_USER_PAGE_ENDPOINT,produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "This method allows you to paginate users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was found in the database"),
            @ApiResponse(responseCode = "404", description = "No user was found in the database with the ID provided")
    })
    ResponseEntity<HttpResponse<Map<String, Object>>> findAllUsersByPage(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "3") int size);


    @DeleteMapping  (value =DELETE_BY_ID_USER_ENDPOINT)
    @Operation(description = "This method allows you to delete a user by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User has been deleted from the database"),
            @ApiResponse(responseCode = "404", description = "No user was found in the database with the ID provided")
    })
    ResponseEntity<HttpResponse<UserDto>> deleteUser(@PathVariable("id") Integer id);





}
