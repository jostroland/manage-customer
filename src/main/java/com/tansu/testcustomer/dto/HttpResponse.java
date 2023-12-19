package com.tansu.testcustomer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * @author Jost Roland
 * @version 1.0
 * @since 11/12/2023
 */



@Builder
@JsonInclude(NON_NULL)
public record HttpResponse<T> (String timeStamp,
                               int statusCode,
                               HttpStatus status,
                               String message,
                               String developerMessage,
                               Collection<? extends T> data,
                               Map<String, Object> pageCustomers,
                               Map<String, Object> pageUsers)

       implements Serializable {
}
