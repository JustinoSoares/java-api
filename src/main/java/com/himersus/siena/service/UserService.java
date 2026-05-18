package com.himersus.siena.service;

import com.himersus.siena.entity.UserEntity;
import com.himersus.siena.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public UserEntity createUser(UserEntity user) {
        return userRepository.save(user);
    } 

    public List<UserEntity> listUser() {
        return userRepository.findAll();
    }

    public UserEntity getEach(UUID id)
    {
        return userRepository.findById(id).orElseThrow();
    }
}