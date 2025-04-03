package com.pitang.car_users_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para atualização do usuário.
 */
@Setter
@Getter
public class UserRequestUpdate {

    @NotBlank(message = "O nome não pode ser nulo, vazio ou em branco")
    private String firstName;

    @NotBlank(message = "O sobrenome não pode ser nulo, vazio ou em branco")
    private String lastName;

    @NotBlank(message = "O email não pode ser nulo, vazio ou em branco")
    @Email(message = "O email deve ser válido")
    private String email;

    @NotBlank(message = "O login não pode ser nulo, vazio ou em branco")
    private String login;

    @NotBlank(message = "O telefone não pode ser nulo, vazio ou em branco")
    private String phone;
}