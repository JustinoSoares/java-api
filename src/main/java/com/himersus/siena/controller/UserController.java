package com.himersus.siena.controller;

import com.himersus.siena.service.UserService;
import com.himersus.siena.entity.UserEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController // serve para 
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    @PostMapping()
    public UserEntity create(UserEntity user)
    {
        return userService.createUser(user);
    }

    @GetMapping("/list")
    public List<UserEntity> list()
    {
        return userService.listUser();
    }

    @GetMapping("{id}")
    public UserEntity getEach(@PathVariable UUID id)
    {
        return userService.getEach(id);
    }

    @GetMapping("/hello")
    public String HelloWorld() {
        return "Olla mundo";
    }
}
