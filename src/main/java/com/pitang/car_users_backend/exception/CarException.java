package com.pitang.car_users_backend.exception;

/**
 * Exceção customizada para erros relacionados a carros.
 * Carrega um {@link CarErrorCode} para indicar o tipo de erro.
 */
public class CarException extends RuntimeException {

    private final CarErrorCode errorCode;

    /**
     * Construtor que recebe um código de erro específico.
     * @param errorCode código e mensagem do erro
     */
    public CarException(CarErrorCode errorCode) {
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
