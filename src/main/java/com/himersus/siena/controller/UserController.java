package com.himersus.siena.controller;

import com.himersus.siena.service.UserService;
import com.himersus.siena.entity.UserEntity;
import com.himersus.siena.dto.UserDtoMapper;
import com.himersus.siena.dto.request.UserDtoRequest;
import com.himersus.siena.dto.response.UserDtoResponse;
import com.himersus.siena.dto.response.PaginatedResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    public UserController(UserService userService, UserDtoMapper userDtoMapper) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
    }

    @PostMapping("/create")
    public UserDtoResponse create(@RequestBody @Valid UserDtoRequest user) {
        UserEntity userEntity = userService.createUser(UserDtoMapper.toEntity(user));

        return UserDtoMapper.toResponse(userEntity);
    }

@GetMapping("/list")
public PaginatedResponse<UserDtoResponse> list(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int per_page) {

    return userService.listUser(page, per_page); // ✅ passa int, int
}

    @GetMapping("/get/{id}")
    public UserDtoResponse getEach(@PathVariable UUID id) {
        return UserDtoMapper.toResponse(userService.getEach(id));
    }

    @GetMapping("/hello")
    public String HelloWorld() {
        return "Olla mundo";
    }
}

