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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador responsável pelas operações relacionadas a Usuários.
 * Agora utiliza Request/Response DTOs em vez de expor diretamente a entidade UserEntity.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    /**
     * Construtor que injeta o UserService.
     * @param service instância de UserService
     */
    public UserController(UserService service) {
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
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@RequestParam(required = false) Long userId) {
        validateUser(userId);
        UserEntity user = service.getUserById(userId);
        return ResponseEntity.ok(UserMapper.toResponse(user));
    }

    /**
     * Retorna todos os usuários do sistema (apenas como exemplo).
     * @return lista de UserResponse
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        List<UserResponse> responses = service.getAllUsers().stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Retorna um usuário específico pelo ID.
     * @param id id do usuário
     * @return UserResponse com os dados do usuário
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        UserEntity user = service.getUserById(id);
        return ResponseEntity.ok(UserMapper.toResponse(user));
    }

    /**
     * Cria um novo usuário.
     * @param request objeto de requisição com os campos necessários para criar um usuário
     * @return UserResponse com o usuário criado
     */
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserEntity userEntity = UserMapper.toEntity(request);
        UserEntity created = service.createUser(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toResponse(created));
    }

    /**
     * Atualiza os dados de um usuário existente.
     * @param id id do usuário a ser atualizado
     * @param updatedUser objeto de requisição com dados para atualizar
     * @return UserResponse com os dados do usuário atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequestUpdate updatedUser) {
        UserEntity userEntity = UserMapper.toEntity(updatedUser);
        UserEntity updated = service.updateUser(id, userEntity);
        return ResponseEntity.ok(UserMapper.toResponse(updated));
    }

    /**
     * Remove um usuário do sistema pelo ID.
     * @param id id do usuário a ser removido
     * @return 204 No Content em caso de sucesso
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retorna a foto do usuário
     * @param id id do usuário
     * @return foto do usuário
     */
    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> getUserPhoto(@PathVariable Long id) {
        Resource resource = service.getUserPhotoResource(id);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            String contentType = Files.probeContentType(Paths.get(resource.getFile().getAbsolutePath()));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Faz upload de uma foto para o usuário.
     * @param id id do usuário
     * @param file arquivo da foto
     * @return mensagem de sucesso caso a foto seja salva
     */
    @PostMapping("/{id}/photo")
    public ResponseEntity<UserResponse> uploadUserPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        UserEntity updatedUser = service.uploadUserPhoto(id, file);
        return ResponseEntity.ok(UserMapper.toResponse(updatedUser));
    }

    /**
     * Retorna a lista de usuários ordenados pelo total de uso (desc),
     * desempate pelo login (asc).
     * @return lista de UserResponse ordenada
     */
    @GetMapping("/ordered")
    public ResponseEntity<List<UserResponse>> getUsersOrdered() {
        List<UserResponse> ordered = service.getAllUsers().stream()
                .sorted(Comparator.comparingInt(UserEntity::getTotalUsageCount)
                        .reversed()
                        .thenComparing(UserEntity::getLogin))
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ordered);
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
