package com.pitang.car_users_backend.Mapper;

import com.pitang.car_users_backend.dto.UserRequest;
import com.pitang.car_users_backend.dto.UserRequestUpdate;
import com.pitang.car_users_backend.dto.UserResponse;
import com.pitang.car_users_backend.model.UserEntity;

import java.util.stream.Collectors;

/**
 * Classe utilitária para conversão entre UserEntity e seus DTOs.
 */
public class UserMapper {

    /**
     * Converte UserRequest para UserEntity.
     * @param request o objeto de requisição
     * @return a entidade de usuário
     */
    public static UserEntity toEntity(UserRequest request) {
        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setBirthday(request.getBirthday());
        user.setLogin(request.getLogin());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setCars(CarMapper.toEntity(request.getCars()));
        return user;
    }

    /**
     * Converte UserRequestUpdate para UserEntity.
     * @param request o objeto de requisição
     * @return a entidade de usuário
     */
    public static UserEntity toEntity(UserRequestUpdate request) {
        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setLogin(request.getLogin());
        user.setPhone(request.getPhone());
        return user;
    }

    /**
     * Converte UserEntity para UserResponse.
     * @param user a entidade de usuário
     * @return o objeto de resposta
     */
    public static UserResponse toResponse(UserEntity user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setBirthday(user.getBirthday());
        response.setLogin(user.getLogin());
        response.setPhone(user.getPhone());
        response.setCreatedAt(user.getCreatedAt());
        response.setLastLogin(user.getLastLogin());
        if (user.getCars() != null) {
            response.setCars(user.getCars().stream().map(CarMapper::toResponse).collect(Collectors.toList()));
        }
        return response;
    }
}
