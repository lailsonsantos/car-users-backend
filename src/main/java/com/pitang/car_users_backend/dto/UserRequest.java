package com.pitang.car_users_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para criação do usuário.
 */
@Setter
@Getter
public class UserRequest {
    @NotBlank(message = "O nome não pode ser nulo, vazio ou em branco")
    private String firstName;

    @NotBlank(message = "O sobrenome não pode ser nulo, vazio ou em branco")
    private String lastName;

    @NotBlank(message = "O email não pode ser nulo, vazio ou em branco")
    @Email(message = "O email deve ser válido")
    private String email;

    @NotNull(message = "A data de nascimento não pode ser nula")
    private LocalDate birthday;

    @NotBlank(message = "O login não pode ser nulo, vazio ou em branco")
    private String login;

    @NotBlank(message = "A senha não pode ser nula, vazia ou em branco")
    private String password;

    @NotBlank(message = "O telefone não pode ser nulo, vazio ou em branco")
    private String phone;

    @NotNull(message = "A lista de carros não pode ser nula")
    @NotEmpty(message = "Deve haver ao menos um carro")
    private List<CarRequest> cars;

    private String photoUrl;

    private int totalUsageCount;
}