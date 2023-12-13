package com.tansu.testcustomer.validation;



import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrongName {
    String message() default "Doit etre compris en 4 et 16 caractères et combiné avec des lettres en majuscule, lettres en minuscule, chiffres, caratères spéciaux.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
