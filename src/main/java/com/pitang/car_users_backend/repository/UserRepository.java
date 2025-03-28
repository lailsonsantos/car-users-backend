package com.pitang.car_users_backend.repository;

import com.pitang.car_users_backend.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByLogin(String login);
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);
}
