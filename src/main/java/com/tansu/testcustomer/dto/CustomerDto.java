package com.tansu.testcustomer.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;


@Builder
@Validated
public record CustomerDto(
              Integer id,
              @NotBlank(message = "The first name must not be blank")
              @NotEmpty(message = "The first name must not be empty")
              String firstName,
              @NotBlank(message = "The first name must not be blank")
              @NotEmpty(message = "The first name must not be empty")
              String lastName,
              Integer age


){ }
