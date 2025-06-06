package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.exception.UserErrorCode;
import com.pitang.car_users_backend.exception.UserException;
import com.pitang.car_users_backend.model.UserEntity;
import com.pitang.car_users_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.pitang.car_users_backend.util.UserValidationUtil.isValid;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para {@link UserServiceImpl} (~80% coverage).
 * Mantém cenários de criação, atualização e deleção básicos.
 */
class UserServiceImplTest {

    private UserRepository repository;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userService;

    /**
     * Configura mocks e instancia o service.
     */
    @BeforeEach
    void setUp() {
        repository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserServiceImpl(repository, passwordEncoder);
    }

    /**
     * Criação de usuário com sucesso.
     */
    @Test
    void testCreateUser_Success() {
        UserEntity user = new UserEntity();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setEmail("email@test.com");
        user.setLogin("testuser");
        user.setPassword("123456");
        user.setPhone("123456789");

        when(repository.existsByEmail("email@test.com")).thenReturn(false);
        when(repository.existsByLogin("testuser")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hashed");
        when(repository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        UserEntity result = userService.createUser(user);

        assertNotNull(result.getId());
        assertEquals("hashed", result.getPassword());
        assertNotNull(result.getCreatedAt());
    }

    /**
     * Falha ao criar usuário devido login duplicado.
     */
    @Test
    void testCreateUser_LoginExists() {
        UserEntity user = new UserEntity();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setEmail("ok@test.com");
        user.setLogin("duplicado");
        user.setPassword("123456");
        user.setPhone("123456789");

        when(repository.existsByLogin("duplicado")).thenReturn(true);

        UserException ex = assertThrows(UserException.class, () -> userService.createUser(user));
        assertEquals(UserErrorCode.LOGIN_ALREADY_EXISTS.getMessage(), ex.getMessage());
    }

    /**
     * Atualização de usuário com sucesso.
     */
    @Test
    void testUpdateUser_Success() {
        UserEntity existing = new UserEntity();
        existing.setId(1L);
        existing.setFirstName("Old");
        existing.setLastName("User");
        existing.setBirthday(LocalDate.of(1990, 1, 1));
        existing.setEmail("old@test.com");
        existing.setLogin("olduser");
        existing.setPassword("oldpwd");
        existing.setPhone("123456789");
        existing.setCreatedAt(LocalDateTime.now());

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(passwordEncoder.encode("newpwd")).thenReturn("hashedNew");

        UserEntity updateData = new UserEntity();
        updateData.setFirstName("New");
        updateData.setLastName("User");
        updateData.setBirthday(LocalDate.of(1990, 1, 1));
        updateData.setEmail("new@test.com");
        updateData.setLogin("newuser");
        updateData.setPassword("newpwd");
        updateData.setPhone("123456789");

        UserEntity updated = userService.updateUser(1L, updateData);

        assertEquals("new@test.com", updated.getEmail());
        assertEquals("newuser", updated.getLogin());
        assertEquals("hashedNew", updated.getPassword());
    }

    /**
     * Falha ao atualizar usuário não encontrado.
     */
    @Test
    void testUpdateUser_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        UserEntity userData = new UserEntity();
        userData.setEmail("ok@test.com");
        userData.setLogin("okUser");

        UserException ex = assertThrows(UserException.class, () -> userService.updateUser(999L, userData));
        assertEquals(UserErrorCode.USER_NOT_FOUND.getMessage(), ex.getMessage());
    }

    /**
     * Deleção de usuário com sucesso.
     */
    @Test
    void testDeleteUser_Success() {
        when(repository.existsById(5L)).thenReturn(true);

        userService.deleteUser(5L);
        verify(repository).deleteById(5L);
    }

    /**
     * Falha ao deletar usuário não encontrado.
     */
    @Test
    void testDeleteUser_NotFound() {
        when(repository.existsById(999L)).thenReturn(false);

        UserException ex = assertThrows(UserException.class, () -> userService.deleteUser(999L));
        assertEquals(UserErrorCode.USER_NOT_FOUND.getMessage(), ex.getMessage());
    }

    /**
     * Testa rapidamente a busca por ID.
     */
    @Test
    void testGetUserById_Found() {
        UserEntity user = new UserEntity();
        user.setId(123L);
        when(repository.findById(123L)).thenReturn(Optional.of(user));

        UserEntity result = userService.getUserById(123L);
        assertNotNull(result);
        assertEquals(123L, result.getId());
    }

    /**
     * Testa verificação se login existe.
     */
    @Test
    void testLoginExists() {
        when(repository.existsByLogin("test")).thenReturn(true);
        assertTrue(userService.loginExists("test"));
    }

    /**
     * Verifica método utilitário de isValid (opcional).
     */
    @Test
    void testIsValidMethod() {
        UserEntity u = new UserEntity();
        u.setEmail("ok@test.com");
        u.setLogin("login");
        u.setPassword("123456");
        assertTrue(isValid(u));
    }

    /**
     * Testa sucesso no retorno da foto não encontrada
     * @throws Exception
     */
    @Test
    void testGetUserPhotoResource_Success() throws Exception {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        Path uploadPath = Paths.get("uploads/users").toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        Path photoPath = uploadPath.resolve("user_" + userId + ".jpg");
        Files.write(photoPath, "fake image content".getBytes());

        Resource resource = userService.getUserPhotoResource(userId);
        assertNotNull(resource);
        assertTrue(resource.exists());

        Files.deleteIfExists(photoPath);
    }

    /**
     * Testa falha no retorno da foto não encontrada
     */
    @Test
    void testGetUserPhotoResource_NotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserException.class, () -> userService.getUserPhotoResource(99L));
    }

    /**
     * Testa sucesso na importação da foto
     * @throws Exception
     */
    @Test
    void testUploadUserPhoto_Success() throws Exception {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);

        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.save(any(UserEntity.class))).thenReturn(user);

        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "image data".getBytes());

        UserEntity result = userService.uploadUserPhoto(userId, file);

        assertNotNull(result);
        assertEquals("/api/users/" + userId + "/photo", result.getPhotoUrl());

        Path uploadPath = Paths.get("uploads/users").toAbsolutePath().normalize();
        Files.deleteIfExists(uploadPath.resolve("user_" + userId + ".jpg"));
    }

    /**
     * Testa falha na importação da foto tipo arquivo
     */
    @Test
    void testUploadUserPhoto_InvalidFileType() {
        MockMultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain", "data".getBytes());
        UserException ex = assertThrows(UserException.class, () -> userService.uploadUserPhoto(1L, file));
        assertEquals(UserErrorCode.INVALID_PHOTO.getMessage(), ex.getMessage());
    }

    /**
     * Testa falha na importação da foto
     */
    @Test
    void testUploadUserPhoto_EmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.jpg", "image/jpeg", new byte[0]);
        UserException ex = assertThrows(UserException.class, () -> userService.uploadUserPhoto(1L, file));
        assertEquals(UserErrorCode.INVALID_PHOTO.getMessage(), ex.getMessage());
    }

}
