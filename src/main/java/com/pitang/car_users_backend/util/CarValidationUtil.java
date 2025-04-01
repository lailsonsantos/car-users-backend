package com.pitang.car_users_backend.util;

import com.pitang.car_users_backend.model.Car;

/**
 * Classe utilitária para validação de campos de um {@link Car}.
 */
public class CarValidationUtil {

    /**
     * Verifica se o carro possui campos válidos (placa, modelo, ano...).
     * @param car carro a ser verificado
     * @return true se for válido, false caso contrário
     */
    public static boolean isValid(Car car) {
        return car.getLicensePlate() != null && car.getLicensePlate().length() >= 5
                && car.getModel() != null && !car.getModel().isBlank()
                && car.getYear() != null && car.getYear() > 1900;
    }

    /**
     * Verifica se campos obrigatórios (placa, modelo, ano) estão ausentes.
     * @param car carro a ser verificado
     * @return true se faltam campos, false caso contrário
     */
    public static boolean hasMissingFields(Car car) {
        return car.getLicensePlate() == null || car.getModel() == null || car.getYear() == null;
    }
}
