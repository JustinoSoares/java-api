package com.himersus.siena.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public record UserDtoRequest(

        @NotBlank(message = "O campo nome não pode ser vazio") @Size(min = 3, max = 100, message = "O campo nome deve ter entre 3 e 100 caracteres") String name,

        @NotBlank(message = "O campo email não pode ser vazio") @Email(message = "O campo email deve ser válido") String email,

        @NotBlank(message = "O campo senha não pode ser vazio") @Size(min = 8, max = 100, message = "O campo senha deve ter entre 8 e 100 caracteres") String password) {

}