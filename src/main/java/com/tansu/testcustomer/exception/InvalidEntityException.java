package com.tansu.testcustomer.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
public class InvalidEntityException extends RuntimeException {
    private Integer id;
}

