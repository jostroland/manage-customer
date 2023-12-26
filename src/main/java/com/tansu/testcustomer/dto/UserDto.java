package com.tansu.testcustomer.dto;

import lombok.Builder;

@Builder
public record UserDto (Integer id,
					   String name,
					   String email,
					   String roles)
{ }