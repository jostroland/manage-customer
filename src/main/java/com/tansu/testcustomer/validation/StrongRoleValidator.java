package com.tansu.testcustomer.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongRoleValidator implements ConstraintValidator<StrongRole, String> {
    @Override
    public void initialize(StrongRole constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.startsWith("ROLE_");
    }
}
