package com.tansu.testcustomer.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OperationNonPermittedException extends RuntimeException {
    public OperationNonPermittedException(String message) {
        super(message);
    }
    public OperationNonPermittedException(String message, Throwable cause) {
        super(message, cause);
    }

}
