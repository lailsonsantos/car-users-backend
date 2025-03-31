package com.pitang.car_users_backend.controller;

import com.pitang.car_users_backend.model.UserEntity;
import com.pitang.car_users_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAll() {
        List<UserEntity> users = service.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            UserEntity user = service.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage(), "errorCode", 404));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserEntity user) {
        try {
            UserEntity newUser = service.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage(), "errorCode", 2));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UserEntity updatedUser) {
        try {
            UserEntity updated = service.updateUser(id, updatedUser);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage(), "errorCode", 404));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage(), "errorCode", 404));
        }
    }
}


import org.springframework.web.multipart.MultipartFile;
import java.util.Comparator;
import java.util.stream.Collectors;

@PostMapping("/users/{id}/photo")
public ResponseEntity<String> uploadUserPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
    // Simula salvamento local (poderia ser no S3, por exemplo)
    String filePath = "uploads/users/user_" + id + "_" + file.getOriginalFilename();
    try {
        file.transferTo(new java.io.File(filePath));
        // Atualizar entidade com o caminho (omitido)
        return ResponseEntity.ok("Foto enviada com sucesso: " + filePath);
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Erro ao salvar foto");
    }
}

@GetMapping("/users")
public ResponseEntity<List<UserEntity>> getUsersOrdered() {
    List<UserEntity> users = userService.getAllUsers();
    List<UserEntity> ordered = users.stream()
        .sorted(Comparator.comparingInt(UserEntity::getTotalUsageCount)
            .reversed()
            .thenComparing(UserEntity::getLogin))
        .collect(Collectors.toList());
    return ResponseEntity.ok(ordered);
}
