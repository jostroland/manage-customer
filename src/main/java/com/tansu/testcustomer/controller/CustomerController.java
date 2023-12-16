package com.tansu.testcustomer.controller;



import com.tansu.testcustomer.controller.api.CustomerApi;
import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.dto.HttpResponse;
import com.tansu.testcustomer.services.CustomerService;
import com.tansu.testcustomer.utils.Constants;
import com.tansu.testcustomer.utils.DateUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

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

    @Override
    public ResponseEntity<HttpResponse<?>> handleError(HttpServletRequest request) {
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .developerMessage("There is no mapping for a " + request.getMethod() + " request for this path on the server")
                        .status(NOT_FOUND)
                        .statusCode(NOT_FOUND.value())
                        .timeStamp(LocalDateTime.now().format(DateUtil.dateTimeFormatter()))
                        .build(), NOT_FOUND
        );
    }


}
