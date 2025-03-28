package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.model.UserEntity;
import com.pitang.car_users_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository repository;
    private UserServiceImpl service;

    @BeforeEach
    public void setup() {
        repository = Mockito.mock(UserRepository.class);
        PasswordEncoder passwordEncoder = new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
        service = new UserServiceImpl(repository, passwordEncoder);
    }


    @Test
    public void shouldCreateUser() {
        UserEntity user = new UserEntity();
        user.setFirstName("Test");
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setPassword("password");

        // Garante que as verificações de duplicidade retornem false
        when(repository.existsByEmail(user.getEmail())).thenReturn(false);
        when(repository.existsByLogin(user.getLogin())).thenReturn(false);
        when(repository.save(user)).thenReturn(user);

        UserEntity created = service.createUser(user);
        assertEquals("Test", created.getFirstName());
    }

    @Test
    public void shouldReturnAllUsers() {
        when(repository.findAll()).thenReturn(Arrays.asList(new UserEntity(), new UserEntity()));
        assertEquals(2, service.getAllUsers().size());
    }

    @Test
    public void shouldReturnUserById() {
        UserEntity user = new UserEntity();
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        assertNotNull(service.getUserById(1L));
    }

    @Test
    public void shouldUpdateUser() {
        // Cria um usuário existente
        UserEntity existingUser = new UserEntity();
        existingUser.setId(1L);
        existingUser.setFirstName("OldName");

        // Quando buscar por id, retorna o usuário existente
        when(repository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(repository.save(existingUser)).thenReturn(existingUser);

        // Dados para atualização
        UserEntity updateData = new UserEntity();
        updateData.setFirstName("Update");

        UserEntity updated = service.updateUser(1L, updateData);
        assertEquals("Update", updated.getFirstName());
    }

    @Test
    public void shouldDeleteUser() {
        // Garante que o usuário existe para exclusão
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);
        service.deleteUser(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}
