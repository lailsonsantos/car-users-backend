package com.pitang.car_users_backend.Mapper;

import com.pitang.car_users_backend.dto.CarRequest;
import com.pitang.car_users_backend.dto.UserRequest;
import com.pitang.car_users_backend.dto.UserRequestUpdate;
import com.pitang.car_users_backend.dto.UserResponse;
import com.pitang.car_users_backend.model.UserEntity;

import java.time.LocalDate;
import java.util.List;
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
        return getUserEntity(request.getFirstName(), request.getLastName(), request.getEmail(), request.getBirthday(),
                request.getLogin(), request.getPassword(), request.getPhone(), request.getCars());
    }

    /**
     * Converte UserRequestUpdate para UserEntity.
     * @param request o objeto de requisição
     * @return a entidade de usuário
     */
    public static UserEntity toEntity(UserRequestUpdate request) {
        return getUserEntity(request.getFirstName(), request.getLastName(), request.getEmail(), request.getBirthday(),
                request.getLogin(), request.getPassword(), request.getPhone(), request.getCars());
    }

    /**
     * Cria um UserEntity.
     * @param firstName nome do usuário
     * @param lastName sobrenome do usuário
     * @param email email do usuário
     * @param birthday data de aniversário do usuário
     * @param login login do usuário
     * @param password senha do usuário
     * @param phone telefone do usuário
     * @param cars carros do usuário
     * @return a entidade de usuário
     */
    private static UserEntity getUserEntity(String firstName, String lastName, String email, LocalDate birthday,
                                            String login, String password, String phone, List<CarRequest> cars) {
        UserEntity user = new UserEntity();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setBirthday(birthday);
        user.setLogin(login);
        user.setPassword(password);
        user.setPhone(phone);
        user.setCars(CarMapper.toEntity(cars));
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
        response.setPhotoUrl(user.getPhotoUrl());
        if (user.getCars() != null) {
            response.setCars(user.getCars().stream().map(CarMapper::toResponse).collect(Collectors.toList()));
        }
        return response;
    }
}
