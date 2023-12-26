package com.tansu.testcustomer.mapper;

import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.entities.Customer;

public record CustomerMapper() {
    public static CustomerDto toDto(Customer customer){
        return CustomerDto.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .age(customer.getAge())
                .build();

    }

    public static Customer toEntity(CustomerDto customerDto){
        return Customer.builder()
                .id(customerDto.id())
                .firstName(customerDto.firstName())
                .lastName(customerDto.lastName())
                .age(customerDto.age())
                .build();
    }
}
