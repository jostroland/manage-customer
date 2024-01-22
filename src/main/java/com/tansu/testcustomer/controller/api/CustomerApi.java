package com.tansu.testcustomer.controller.api;


import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.dto.HttpResponse;
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


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.tansu.testcustomer.utils.Constants.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@Tag(name = "Customers", description = "Customers API")
public interface CustomerApi {

    @PostMapping(value = CREATE_CUSTOMER_ENDPOINT,consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "This method allows you to create a Customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer create"),
            @ApiResponse(responseCode = "404", description = "Customer does not exist")
    })
    ResponseEntity<HttpResponse<CustomerDto>> saveCustomer(@RequestBody CustomerDto customerDto);

    @PutMapping(value = UPDATE_CUSTOMER_ENDPOINT,consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "This method allows you to modify a Customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer has been modified"),
            @ApiResponse(responseCode = "404", description = "Customer does not exist in the database")
    })
    ResponseEntity<HttpResponse<CustomerDto>> updateCustomer(@PathVariable("id") Integer id,@RequestBody CustomerDto customerDto );

    @GetMapping (value =  FIND_BY_ID_CUSTOMER_ENDPOINT,produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "This method allows you to search for a Customer by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer was found in the database"),
            @ApiResponse(responseCode = "404", description = "No Customer was found in the database with the ID provided")
    })
    ResponseEntity<HttpResponse<CustomerDto>> findCustomerById(@PathVariable("id") Integer id);

    @GetMapping (value = FIND_ALL_CUSTOMER_ENDPOINT,produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "This method allows you to select all customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer was found in the database"),
            @ApiResponse(responseCode = "404", description = "No Customer was found in the database with the ID provided")
    })
    ResponseEntity<HttpResponse<List<CustomerDto>>> findAllCustomers();

    @GetMapping (value = FIND_ALL_CUSTOMER_PAGE_ENDPOINT,produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "This method allows you to paginate Customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer was found in the database"),
            @ApiResponse(responseCode = "404", description = "No Customer was found in the database with the ID provided")
    })
    ResponseEntity<HttpResponse<Map<String, Object>>> findAllCustomersByPage(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "3") int size);

    @DeleteMapping  (value =DELETE_BY_ID_CUSTOMER_ENDPOINT)
    @Operation(description = "This method allows you to delete a customer by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer has been deleted from the database"),
            @ApiResponse(responseCode = "404", description = "No Customer was found in the database with the ID provided")
    })
    ResponseEntity<HttpResponse<CustomerDto>> deleteCustomer(@PathVariable("id") Integer id);
}
