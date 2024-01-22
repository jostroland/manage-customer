package com.tansu.testcustomer.dto;

import com.tansu.testcustomer.validation.StrongRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record UserRequest(
                          Integer id,
                          @NotBlank(message = "The name must not be blank")
                          @NotEmpty(message = "The name must not be empty")
                          String name,
                          @NotBlank(message = "The email must not be blank")
                          @NotEmpty(message = "The email must not be empty")
                          @Email(message = "The email is not valid")
                          String email,
                          @NotBlank(message = "The password must not be blank")
                          @NotEmpty(message = "The password must not be empty")
                          String password,
                          @NotBlank(message = "The roles name must not be blank")
                          @NotEmpty(message = "The roles name must not be empty")
                          @StrongRole(message = "The role must start with the term: ROLE_")
                          String roles)
{ }
