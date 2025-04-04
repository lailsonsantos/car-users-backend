package com.pitang.car_users_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

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

    private String password;

    @NotBlank(message = "O telefone não pode ser nulo, vazio ou em branco")
    private String phone;

    @NotBlank(message = "A data de nascimento não pode ser nula")
    private LocalDate birthday;

    @NotBlank(message = "O carro não pode se nulo")
    private List<CarRequest> cars;

    private String photoUrl;
}