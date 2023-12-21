package com.tansu.testcustomer.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class ObjectValidationException extends RuntimeException {
    private final Set<String> violations;
    private final String violationSource;
}
