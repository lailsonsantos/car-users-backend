package com.pitang.car_users_backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * Handler global de exceções, retorna resposta padronizada (JSON)
 * para diferentes tipos de exceções customizadas.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata exceções de {@link UserException}, retornando status 400 e um JSON com mensagem e código.
     * @param ex exceção do tipo UserException
     * @return ResponseEntity com corpo JSON
     */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<Map<String, Object>> handleUserException(UserException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "message", ex.getCustomMessage(),
                        "errorCode", ex.getCode()
                ));
    }

    /**
     * Trata exceções de {@link IllegalArgumentException}, retornando status 400 e corpo com mensagem.
     * @param ex exceção IllegalArgumentException
     * @return ResponseEntity com corpo JSON
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "message", ex.getMessage(),
                        "errorCode", 400
                ));
    }

    /**
     * Trata exceções de {@link CarException}, retornando status 400 e corpo JSON padronizado.
     * @param ex exceção do tipo CarException
     * @return ResponseEntity com corpo JSON
     */
    @ExceptionHandler(CarException.class)
    public ResponseEntity<Map<String, Object>> handleCarException(CarException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "message", ex.getCustomMessage(),
                        "errorCode", ex.getCode()
                ));
    }
}
