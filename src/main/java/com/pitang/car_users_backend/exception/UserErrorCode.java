package com.pitang.car_users_backend.exception;

import lombok.Getter;

/**
 * Enum que define os códigos e mensagens de erro para a funcionalidade de usuários.
 */
@Getter
public enum UserErrorCode {
    INVALID_LOGIN_OR_PASSWORD(1, "Invalid login or password"),
    EMAIL_ALREADY_EXISTS(2, "Email already exists"),
    LOGIN_ALREADY_EXISTS(3, "Login already exists"),
    INVALID_FIELDS(4, "Invalid fields"),
    MISSING_FIELDS(5, "Missing fields"),
    USER_NOT_FOUND(6, "User not found"),
    UPLOAD_FAILED(7, "Failed to upload user photo"),
    UNAUTHORIZED(8, "Unauthorized"),
    UNAUTHORIZED_SESSION(9, "Unauthorized - invalid session");

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

    UserErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
