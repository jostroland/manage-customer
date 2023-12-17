package com.tansu.testcustomer.dto;


import jakarta.persistence.Column;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;


@Builder
@Validated
public record CustomerDto(
              Integer id,
              @NotBlank(message = "The first name must not be blank")
              @NotEmpty(message = "The first name must not be empty")
              String firstName,
              @NotBlank(message = "The last name must not be blank")
              @NotEmpty(message = "The last name must not be empty")
              String lastName,

              @NotNull(message = "Age must not be empty")
              @Min(value=15, message = "Age must be greater than or equal to 15 years old")
              @Max(value=60, message = "Age must be smaller than or equal to 60 years old")
              Integer age


){ }
