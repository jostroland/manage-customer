package com.tansu.testcustomer.services;

import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.dto.HttpResponse;
import com.tansu.testcustomer.entities.Customer;
import com.tansu.testcustomer.exception.EntityNotFoundException;
import com.tansu.testcustomer.mapper.CustomerMapper;
import com.tansu.testcustomer.repository.CustomerRepository;
import com.tansu.testcustomer.validation.ObjectsValidator;
import com.tansu.testcustomer.services.CustomerService;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.tansu.testcustomer.util.DateUtil.dateTimeFormatter;
import static java.util.Collections.singleton;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.OK;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final ObservationRegistry registry;
    private final CustomerRepository customerRepository;
    private final ObjectsValidator<CustomerDto> validator;
    @Override
    public HttpResponse<CustomerDto> save(CustomerDto dto) {
        log.info("Saving Customer to the database");
        validator.validate(dto);

        Customer customer = CustomerMapper.toEntity(dto);
        Customer customerSave = customerRepository.save(customer);

        return Observation.createNotStarted("save-Customer",registry)
                .observe(
                        HttpResponse.<CustomerDto>builder()
                                .data(Collections.singleton(
                                        CustomerMapper.toDto(customerSave)
                                ))
                                .message("Customer created successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                                ::build
                        );

    }


    @Override
    public HttpResponse<CustomerDto> update(Integer id, CustomerDto customerDto) {
        log.info("Updating Customer to the database");
        validator.validate(customerDto);

        Optional<Customer> optionalCustomer = ofNullable(customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("This Customer was not found on the server")));

        Customer customer         = CustomerMapper.toEntity(customerDto);

        Customer updateCustomer = optionalCustomer.orElseGet(Customer::new);
        updateCustomer.setFirstName(customer.getFirstName());
        updateCustomer.setLastName(customer.getLastName());
        updateCustomer.setAge(customer.getAge());


        customerRepository.save(updateCustomer);

        return Observation.createNotStarted("update-Customer",registry)
                .observe(
                        HttpResponse.<CustomerDto>builder()
                                .data(Collections.singleton(
                                        CustomerMapper.toDto(updateCustomer)
                                ))
                                .message("Customer updated successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                                ::build
                );

    }

    @Override
    public HttpResponse<CustomerDto> findById(Integer id) {
        log.info("Updating Customer to the database");
        if(isNull(id)){
            log.error("The ID must not be null");
            return null;
        }

        log.info("FindById Customer from the database by id {}", id);
        Optional<Customer> optionalCustomer = ofNullable(customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("This Customer was not found on the server")));


        return Observation.createNotStarted("find-by-id-Customer",registry)
                .observe(
                         HttpResponse.<CustomerDto>builder()
                                .data(Collections.singleton(
                                        CustomerMapper.toDto(optionalCustomer.orElseThrow(()-> new NoSuchElementException("This Customer was not found on the server")))
                                ))
                                .message("Customer founded successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                                ::build
                );

    }

    @Override
    public HttpResponse<List<CustomerDto>> findAll() {
        log.info("Find all customers to the database");

        List<CustomerDto> customerDtos = customerRepository.findAll().stream()
                .map(CustomerMapper::toDto)
                .toList();


        return Observation.createNotStarted("find-all-Customer",registry)
                .observe(
                        HttpResponse.<List<CustomerDto>>builder()
                                .data(singleton(customerDtos))
                                .message(customerRepository.count() > 0 ? customerRepository.count() + " notes retrieved" : "No notes to display")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                                ::build
                );
    }

    @Override
    public HttpResponse<Map<String, Object>>  findAll(int page, int size) {
        log.info("Find all by page customers to the database");

        Pageable pageable = PageRequest.of(page, size);
        var customerPage = customerRepository.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        var content = customerPage.getContent().stream().map(CustomerMapper::toDto).collect(Collectors.toList());

        response.put("CustomerDto", content);
        response.put("currentPage", customerPage.getNumber());
        response.put("totalItems", customerPage.getTotalElements());
        response.put("totalPages", customerPage.getTotalPages());

        return  Observation.createNotStarted("find-all-customer-page",registry)
                .observe(
                        HttpResponse.<Map<String, Object>>builder()
                                .pageCustomers(response)
                                .message("Customers found successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))::build
                );
    }

    @Override
    public HttpResponse<CustomerDto> delete(Integer id) throws  EntityNotFoundException{
        log.info("Deleting Customer from the database by id {}", id);
        if(isNull(id)){
            log.error("The ID must not be null");
            return null;
        }

        log.info("Deleting note from the database by id {}", id);
        Optional<Customer> optionalCustomer = ofNullable(customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("This Customer was not found on the server")));

        optionalCustomer.ifPresent(customerRepository::delete);

        return Observation.createNotStarted("delete-Customer",registry)
                .observe(
                        () -> HttpResponse.<CustomerDto>builder()
                                .data(Collections.singleton(
                                        CustomerMapper.toDto(optionalCustomer.orElseThrow(()-> new NoSuchElementException("This Customer was not found on the server")))
                                ))
                                .message("Customer deleted successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                                .build()
                );

    }


}
