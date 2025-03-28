package com.pitang.car_users_backend.controller;

import com.pitang.car_users_backend.dto.LoginRequest;
import com.pitang.car_users_backend.model.UserEntity;
import com.pitang.car_users_backend.repository.UserRepository;
import com.pitang.car_users_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Tenta autenticar com base no login e password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getLogin(),
                            loginRequest.getPassword()
                    )
            );

            // Define a autenticação no contexto de segurança para ser utilizada em outras requisições, se necessário.
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Gera o token JWT
            String token = jwtUtil.generateToken(loginRequest.getLogin());

            // Atualiza o lastLogin do usuário e salva a alteração
            UserEntity user = usuarioRepository.findByLogin(loginRequest.getLogin())
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid login or password"));
            user.setLastLogin(LocalDateTime.now());
            usuarioRepository.save(user);

            // Retorna o token no corpo da resposta
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("token", token);
            return ResponseEntity.ok(responseBody);

        } catch (BadCredentialsException ex) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("message", "Invalid login or password");
            errorBody.put("errorCode", 1);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody);
        }
    }
}
