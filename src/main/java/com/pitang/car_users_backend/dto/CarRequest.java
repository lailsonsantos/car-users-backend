package com.pitang.car_users_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para criação/atualização de carro.
 */
@Setter
@Getter
public class CarRequest {

    @NotNull(message = "O ano de fabricação não pode ser nulo")
    private Integer year;

    @NotBlank(message = "A placa do carro não pode ser vazia ou em branco")
    private String licensePlate;

    @NotBlank(message = "O modelo do carro não pode ser vazio ou em branco")
    private String model;

    @NotBlank(message = "A cor do carro não pode ser vazia ou em branco")
    private String color;

    private int usageCount;

    private String photoUrl;


}