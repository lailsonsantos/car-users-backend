package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.model.UserEntity;

import java.util.List;

public interface UserService {
    UserEntity createUser(UserEntity user);
    UserEntity updateUser(Long id, UserEntity user);
    void deleteUser(Long id);
    List<UserEntity> getAllUsers();
    UserEntity getUserById(Long id);
}
