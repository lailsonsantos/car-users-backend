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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Controlador responsável pelas operações relacionadas a Usuários.
 * Agora utiliza Request/Response DTOs em vez de expor diretamente a entidade UserEntity.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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

    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> getUserPhoto(@PathVariable Long id) {
        UserEntity user = service.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            // Procurar o arquivo com qualquer extensão
            Path uploadPath = Paths.get("uploads/users").toAbsolutePath().normalize();

            // Listar todos os arquivos que começam com "user_[id]"
            try (Stream<Path> paths = Files.list(uploadPath)) {
                Optional<Path> photoPath = paths
                        .filter(path -> path.getFileName().toString().startsWith("user_" + id + "."))
                        .findFirst();

                if (photoPath.isPresent()) {
                    Resource resource = new UrlResource(photoPath.get().toUri());

                    if (resource.exists() && resource.isReadable()) {
                        // Determinar o content type com base na extensão
                        String contentType = Files.probeContentType(photoPath.get());
                        if (contentType == null) {
                            contentType = "application/octet-stream";
                        }

                        return ResponseEntity.ok()
                                .contentType(MediaType.parseMediaType(contentType))
                                .body(resource);
                    }
                }
            }

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
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
    public ResponseEntity<UserResponse> uploadUserPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty() || (file.getContentType() != null && !file.getContentType().startsWith("image/"))) {
            throw new UserException(UserErrorCode.INVALID_PHOTO);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uploadDir = "uploads/users";
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new UserException(UserErrorCode.UPLOAD_FAILED);
        }

        String fileName = "user_" + id + fileExtension;
        Path filePath = uploadPath.resolve(fileName);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadPath, "user_" + id + ".*")) {
            for (Path existingFile : stream) {
                Files.delete(existingFile);
            }
        } catch (IOException ex) {
            logger.error("Erro de limpeza dos arquivos: {}", ex.getMessage());
        }

        try {
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new UserException(UserErrorCode.UPLOAD_FAILED);
        }

        UserEntity updatedUser = service.updateUserPhoto(id, "/api/users/" + id + "/photo");

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
