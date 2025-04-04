package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.model.UserEntity;

import java.util.List;

/**
 * Interface que define métodos de negócio relacionados a usuários.
 */
public interface UserService {

    /**
     * Cria um novo usuário.
     * @param user objeto contendo dados do novo usuário
     * @return usuário criado
     */
    UserEntity createUser(UserEntity user);

    /**
     * Atualiza dados de um usuário existente.
     * @param id ID do usuário
     * @param user dados para atualização
     * @return usuário atualizado
     */
    UserEntity updateUser(Long id, UserEntity user);

    /**
     * Remove um usuário pelo ID.
     * @param id ID do usuário a ser removido
     */
    void deleteUser(Long id);

    /**
     * Retorna todos os usuários cadastrados.
     * @return lista de usuários
     */
    List<UserEntity> getAllUsers();

    /**
     * Retorna um usuário específico pelo ID.
     * @param id ID do usuário
     * @return usuário encontrado
     */
    UserEntity getUserById(Long id);

    /**
     * Verifica se um e-mail já está em uso.
     * @param email e-mail a ser verificado
     * @return true se existir, false caso contrário
     */
    boolean emailExists(String email);

    /**
     * Verifica se um login já está em uso.
     * @param login login a ser verificado
     * @return true se existir, false caso contrário
     */
    boolean loginExists(String login);

    /**
     *
     * @param id ID do usuário
     * @param photoUrl photoUrl do usuário
     * @return usuário atualizado com a imagem
     */
    UserEntity updateUserPhoto(Long id, String photoUrl);
}
