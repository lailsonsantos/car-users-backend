package com.pitang.car_users_backend.controller;

import com.pitang.car_users_backend.dto.LoginRequest;
import com.pitang.car_users_backend.exception.UserErrorCode;
import com.pitang.car_users_backend.exception.UserException;
import com.pitang.car_users_backend.model.UserEntity;
import com.pitang.car_users_backend.repository.UserRepository;
import com.pitang.car_users_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador responsável pela autenticação de usuários (login).
 * Permite autenticar via login e senha, gerando um token JWT em caso de sucesso.
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Realiza a autenticação de um usuário com base em seu login e senha.
     * Gera um token JWT se as credenciais forem válidas.
     *
     * @param loginRequest objeto contendo login e password
     * @return token JWT no corpo da resposta em caso de sucesso
     * @throws UserException com código {@link UserErrorCode#INVALID_LOGIN_OR_PASSWORD} se credenciais falharem
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Autentica o usuário
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getLogin(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserEntity user = usuarioRepository.findByLogin(loginRequest.getLogin())
                    .orElseThrow(() -> new UserException(UserErrorCode.INVALID_LOGIN_OR_PASSWORD));

            // Gera o token passando o username e o id do usuário
            String token = jwtUtil.generateToken(user.getLogin(), user.getId());

            user.setLastLogin(LocalDateTime.now());
            usuarioRepository.save(user);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("token", token);
            return ResponseEntity.ok(responseBody);

        } catch (BadCredentialsException | UsernameNotFoundException ex) {
            throw new UserException(UserErrorCode.INVALID_LOGIN_OR_PASSWORD);
        }
    }
}
