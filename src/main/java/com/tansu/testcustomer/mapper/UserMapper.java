package com.tansu.testcustomer.mapper;

import com.tansu.testcustomer.dto.UserDto;
import com.tansu.testcustomer.dto.UserRequest;
import com.tansu.testcustomer.entities.User;

public record UserMapper() {
    public static UserDto toDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();

    }

    public static User toEntity(UserDto userDto){
        return User.builder()
                .id(userDto.id())
                .name(userDto.name())
                .email(userDto.email())
                .build();
    }


    public static User fromRequestToEntity(UserRequest userRequest){
        return User.builder()
                .id(userRequest.id())
                .name(userRequest.name())
                .email(userRequest.email())
                .password(userRequest.password())
                .roles(userRequest.roles())
                .build();
    }


    public static UserDto fromRequestToDto(UserRequest userRequest){
        return UserDto.builder()
                .id(userRequest.id())
                .name(userRequest.name())
                .email(userRequest.email())
                .roles(userRequest.roles())
                .build();
    }



}
