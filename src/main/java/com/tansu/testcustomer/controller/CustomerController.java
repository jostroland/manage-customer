package com.tansu.testcustomer.controller;


import com.tansu.testcustomer.controller.api.CustomerApi;
import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.dto.HttpResponse;
import com.tansu.testcustomer.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomerApi {

    private final CustomerService customerService;

    @Override
    public ResponseEntity<HttpResponse<CustomerDto>> saveCustomer(CustomerDto customerDto) {
        return ResponseEntity.created(
                URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/customer/all").toUriString())
        ).body(customerService.save(customerDto));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<HttpResponse<CustomerDto>> updateCustomer(Integer id, CustomerDto customerDto) {
        return ResponseEntity.ok().body(customerService.update(id,customerDto));
    }

    @Override
    public ResponseEntity<HttpResponse<CustomerDto>> findCustomerById(Integer id) {
        return ResponseEntity.ok().body(customerService.findById(id));
    }

    @Override
    public ResponseEntity<HttpResponse<List<CustomerDto>>> findAllCustomers() {
        return ResponseEntity.ok().body(customerService.findAll());
    }

    @Override
    public ResponseEntity<HttpResponse<Map<String, Object>>> findAllCustomersByPage(int page, int size) {
        return ResponseEntity.ok().body(customerService.findAll(page, size));
    }

    @Override
    public ResponseEntity<HttpResponse<CustomerDto>> deleteCustomer(Integer id) {
        return ResponseEntity.ok().body(customerService.delete(id));
    }
}
