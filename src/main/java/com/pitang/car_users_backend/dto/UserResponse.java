package com.pitang.car_users_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para retorno dos dados do usuário.
 */
@Setter
@Getter
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthday;
    private String login;
    private String phone;
    private List<CarResponse> cars;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}