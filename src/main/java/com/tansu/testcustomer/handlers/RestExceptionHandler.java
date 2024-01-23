package com.tansu.testcustomer.handlers;


import com.tansu.testcustomer.exception.EntityNotFoundException;
import com.tansu.testcustomer.exception.ObjectValidationException;
import jakarta.persistence.NoResultException;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    private ResponseEntity<ProblemDetail> createHttpErrorResponse(HttpStatus httpStatus, Exception exception) {
        log.error(exception.getMessage());
        var problemDetail = ProblemDetail.forStatusAndDetail(httpStatus,exception.getMessage());
        problemDetail.setDetail(exception.getMessage());
        problemDetail.setStatus(httpStatus.value());
        return new ResponseEntity<>(problemDetail, httpStatus);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> onPostNotFoundException(EntityNotFoundException entityNotFoundException) {
        return createHttpErrorResponse(HttpStatus.NOT_FOUND,entityNotFoundException);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> illegalStateException(IllegalStateException exception) {
        return createHttpErrorResponse(BAD_REQUEST, exception);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<ProblemDetail> noResultException(NoResultException exception) {
        return createHttpErrorResponse(BAD_REQUEST, exception);
    }

    @ExceptionHandler({ObjectValidationException.class})
    public ResponseEntity<ProblemDetail> onValidationException1(ObjectValidationException validationException)  {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setDetail("Object not valid exception has occurred");
        problemDetail.setProperty("violationSource",validationException.getViolationSource());
        problemDetail.setProperty("violationsErrors",validationException.getViolations().stream().toString());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(problemDetail);
    }
}
