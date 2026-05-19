package com.himersus.siena.service;

import com.himersus.siena.entity.UserEntity;
import com.himersus.siena.exception.UserNotFoundException;
import com.himersus.siena.repository.UserRepository;
import com.himersus.siena.dto.UserDtoMapper;
import com.himersus.siena.dto.response.UserDtoResponse;
import com.himersus.siena.dto.response.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }   

    public UserEntity createUser(UserEntity user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new com.himersus.siena.exception.EmailAlreadyExistsException("O email informado já está em uso.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public PaginatedResponse<UserDtoResponse> listUser(int page, int perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        Page<UserEntity> userPage = userRepository.findAll(pageable);

        List<UserDtoResponse> dtos = userPage.getContent().stream()
                .map(UserDtoMapper::toResponse)
                .toList();

        PaginatedResponse.Meta meta = new PaginatedResponse.Meta(
                userPage.getTotalElements(),
                page,
                perPage
        );

        return new PaginatedResponse<>(dtos, meta);
    }

    public UserEntity getEach(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
    }
}