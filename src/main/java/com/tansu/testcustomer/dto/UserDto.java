package com.tansu.testcustomer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;


@Builder
public record UserDto (Integer id,
					   String name,
					   String email,
					   String roles)
{
}