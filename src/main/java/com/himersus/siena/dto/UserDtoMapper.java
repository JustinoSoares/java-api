package com.himersus.siena.dto;

import com.himersus.siena.entity.UserEntity;
import com.himersus.siena.entity.UserRole;
import com.himersus.siena.dto.request.UserDtoRequest;
import com.himersus.siena.dto.response.UserDtoResponse;

import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {
    public static UserEntity toEntity(UserDtoRequest request) {
        return new UserEntity(null, request.name(), request.email(), request.password(), UserRole.USER);
    }

    public static UserDtoResponse toResponse(UserEntity entity) {
        return new UserDtoResponse(entity.getId(), entity.getName(), entity.getEmail());
    }
}
