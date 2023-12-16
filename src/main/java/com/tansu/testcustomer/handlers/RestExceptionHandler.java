package com.tansu.testcustomer.handlers;


import com.tansu.testcustomer.exception.EntityNotFoundException;
import com.tansu.testcustomer.exception.ObjectValidationException;
import com.tansu.testcustomer.exception.OperationNonPermittedException;
import jakarta.persistence.NoResultException;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {



    private ResponseEntity<ProblemDetail> createHttpErrorResponse(HttpStatus httpStatus, Exception exception) {
        log.error(exception.getMessage());
        var problemDetail = ProblemDetail.forStatusAndDetail(httpStatus,exception.getMessage());
        problemDetail.setDetail(exception.getMessage());
        problemDetail.setStatus(httpStatus.value());
        return new ResponseEntity<>(problemDetail, httpStatus);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> onPostNotFoundException(EntityNotFoundException e) {
        return createHttpErrorResponse(HttpStatus.NOT_FOUND,e);
    }

    @ExceptionHandler(OperationNonPermittedException.class)
    public ResponseEntity<ProblemDetail> onOperationNonPermittedException(OperationNonPermittedException e){
        return createHttpErrorResponse(HttpStatus.NOT_ACCEPTABLE,e);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> illegalStateException(IllegalStateException exception) {
        return createHttpErrorResponse(BAD_REQUEST, exception);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<ProblemDetail> noResultException(NoResultException exception) {
        return createHttpErrorResponse(INTERNAL_SERVER_ERROR, exception);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ProblemDetail> servletException(ServletException exception) {
        return createHttpErrorResponse(INTERNAL_SERVER_ERROR, exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> exception(Exception exception) {
        return createHttpErrorResponse(INTERNAL_SERVER_ERROR, exception);
    }
    

    @ExceptionHandler(ObjectValidationException.class)
    public ResponseEntity<ProblemDetail> onValidationException(ObjectValidationException exception)  {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setProperty("source", exception.getViolationSource());
        String stringBuilderStream = exception.getViolations().stream().map(s -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(s);
            return stringBuilder;
        }).toString();

        problemDetail.setDetail(stringBuilderStream);
        //problemDetail.setDetail(String.join(", ", exception.getViolations()));
        problemDetail.setStatus(HttpStatus.BAD_REQUEST);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(problemDetail);
    }




}
