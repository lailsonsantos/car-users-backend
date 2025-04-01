package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.exception.UserErrorCode;
import com.pitang.car_users_backend.model.UserEntity;
import com.pitang.car_users_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Serviço customizado para carregar detalhes de usuário (Spring Security)
 * a partir de um login, usando o repositório de usuários.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository usuarioRepository;

    /**
     * Localiza o usuário pelo login e constrói um {@link UserDetails} para autenticação.
     * @param login login do usuário
     * @return objeto de UserDetails
     * @throws UsernameNotFoundException se o usuário não for encontrado
     */
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserEntity user = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(UserErrorCode.USER_NOT_FOUND + " [" + login + "]"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getLogin())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
