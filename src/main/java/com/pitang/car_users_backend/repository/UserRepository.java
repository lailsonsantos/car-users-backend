package com.pitang.car_users_backend.repository;

import com.pitang.car_users_backend.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório JPA para acesso à entidade {@link UserEntity}.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Busca um usuário pelo login.
     * @param login login do usuário
     * @return usuário encontrado, se existir
     */
    Optional<UserEntity> findByLogin(String login);

    /**
     * Verifica se existe algum usuário com o e-mail informado.
     * @param email e-mail a ser verificado
     * @return true se existir, false caso contrário
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se existe algum usuário com o login informado.
     * @param login login a ser verificado
     * @return true se existir, false caso contrário
     */
    boolean existsByLogin(String login);
}
