package com.pitang.car_users_backend.exception;

/**
 * Exceção customizada para erros relacionados a usuários.
 * Carrega um {@link UserErrorCode} para indicar o tipo de erro.
 */
public class UserException extends RuntimeException {

    private final UserErrorCode errorCode;

    /**
     * Construtor que recebe o código de erro específico.
     * @param errorCode código e mensagem do erro
     */
    public UserException(UserErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * Retorna o código numérico do erro.
     * @return código do erro
     */
    public int getCode() {
        return errorCode.getCode();
    }

    /**
     * Retorna a mensagem de erro customizada do enum.
     * @return mensagem de erro
     */
    public String getCustomMessage() {
        return errorCode.getMessage();
    }
}
