package com.pitang.car_users_backend.util;

import com.pitang.car_users_backend.model.UserEntity;

/**
 * Classe utilitária para validação de campos de um {@link UserEntity}.
 */
public class UserValidationUtil {

    /**
     * Verifica se o usuário possui campos válidos:
     * e-mail contendo "@", login com no mínimo 3 caracteres e
     * password com no mínimo 6 caracteres.
     *
     * @param user usuário a ser verificado
     * @return true se válido, false caso contrário
     */
    public static boolean isValid(UserEntity user) {
        if (user == null) return false;

        return user.getEmail() != null && user.getEmail().contains("@")
                && user.getLogin() != null && user.getLogin().length() >= 3
                && user.getPassword() != null && user.getPassword().length() >= 6;
    }

    /**
     * Verifica se o usuário possui campos válidos:
     * e-mail contendo "@" e login com no mínimo 3 caracteres.
     *
     * @param user usuário a ser verificado
     * @return true se válido, false caso contrário
     */
    public static boolean isValidUpdate(UserEntity user) {
        if (user == null) return false;

        return user.getEmail() != null && user.getEmail().contains("@")
                && user.getLogin() != null && user.getLogin().length() >= 3;
    }
}
