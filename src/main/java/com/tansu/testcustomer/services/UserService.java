package com.tansu.testcustomer.services;

import com.tansu.testcustomer.dto.HttpResponse;
import com.tansu.testcustomer.dto.UserDto;
import com.tansu.testcustomer.exception.EntityNotFoundException;

import java.util.List;
import java.util.Map;

public interface UserService<T,U> {
    HttpResponse<T> save(U dto);
    HttpResponse<T> update(Integer id,U dto);
    HttpResponse<T> findById(Integer id) throws EntityNotFoundException;
    HttpResponse<List<T>> findAll();
    HttpResponse<Map<String, Object>> findAll(int page , int size);
    HttpResponse<T> delete(Integer id) throws EntityNotFoundException;
}
