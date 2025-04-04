package com.pitang.car_users_backend.exception;

import lombok.Getter;

/**
 * Enum que define os códigos e mensagens de erro específicos para a funcionalidade de carros.
 */
@Getter
public enum CarErrorCode {
    UNAUTHORIZED(1, "Unauthorized"),
    UNAUTHORIZED_SESSION(2, "Unauthorized - invalid session"),
    LICENSE_PLATE_EXISTS(3, "License plate already exists"),
    INVALID_FIELDS(4, "Invalid fields"),
    MISSING_FIELDS(5, "Missing fields"),
    CAR_NOT_FOUND(6, "Car not found"),
    UPLOAD_FAILED(7, "Failed to upload car photo"),
    INVALID_PHOTO(8, "Invalid photo");

    /**
     * -- GETTER --
     *  Retorna o código numérico do erro.
     *
     * @return código do erro
     */
    private final int code;
    /**
     * -- GETTER --
     *  Retorna a mensagem de erro correspondente.
     *
     * @return mensagem de erro
     */
    private final String message;

    CarErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
