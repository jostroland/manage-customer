package com.tansu.testcustomer.mapper;

import com.tansu.testcustomer.dto.UserDto;
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

}
