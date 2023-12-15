package com.tansu.testcustomer.dto;


import lombok.Builder;

@Builder
public record UserRequest(Integer id,
                          String name,
                          String email,
                          String password,
                          String roles)
{
}
