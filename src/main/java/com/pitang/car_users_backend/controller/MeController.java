package com.pitang.car_users_backend.controller;

import com.pitang.car_users_backend.Mapper.UserMapper;
import com.pitang.car_users_backend.dto.UserRequest;
import com.pitang.car_users_backend.dto.UserRequestUpdate;
import com.pitang.car_users_backend.dto.UserResponse;
import com.pitang.car_users_backend.exception.UserErrorCode;
import com.pitang.car_users_backend.exception.UserException;
import com.pitang.car_users_backend.model.UserEntity;
import com.pitang.car_users_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controlador responsável pelas operações relacionadas ao Usuário logado.
 * Agora utiliza Request/Response DTOs em vez de expor diretamente a entidade UserEntity.
 */
@RestController
@RequestMapping("/api/me")
public class MeController {

    private final UserService service;

    /**
     * Construtor que injeta o UserService.
     * @param service instância de UserService
     */
    public MeController(UserService service) {
        this.service = service;
    }

    /**
     * Retorna as informações do usuário logado.
     * Simula validação de token via userId, lançando exceções específicas se estiver ausente (UNAUTHORIZED)
     * ou inválido (UNAUTHORIZED_SESSION).
     *
     * @param userId ID do usuário logado, representando o "token"
     * @return UserResponse com as informações do usuário
     */
    @GetMapping()
    public ResponseEntity<UserResponse> getMe(@RequestParam(required = false) Long userId) {
        validateUser(userId);
        UserEntity user = service.getUserById(userId);
        return ResponseEntity.ok(UserMapper.toResponse(user));
    }

    /**
     * Valida o "token" recebido como userId.
     * @param userId identificador do usuário
     * @throws UserException caso não exista userId ou seja menor/igual a zero
     */
    private void validateUser(Long userId) {
        if (userId == null) {
            throw new UserException(UserErrorCode.UNAUTHORIZED);
        }
        if (userId <= 0) {
            throw new UserException(UserErrorCode.UNAUTHORIZED_SESSION);
        }
    }

}
