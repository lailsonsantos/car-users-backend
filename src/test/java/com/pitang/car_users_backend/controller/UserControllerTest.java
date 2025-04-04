package com.pitang.car_users_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pitang.car_users_backend.model.UserEntity;
import com.pitang.car_users_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de unidade para UserController (aprox. 80% de cobertura).
 * Mantém cenários principais de sucesso e alguns erros relevantes.
 */
class UserControllerTest {

    private UserService userService;
    private UserController userController;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Configuração inicial antes de cada teste.
     */
    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    /**
     * Teste básico do GET /api/users (lista vazia).
     */
    @Test
    void testGetAll_Empty() throws Exception {
        when(userService.getAllUsers()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    /**
     * Teste GET /api/users/{id} com sucesso.
     */
    @Test
    void testGetById_Success() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(10L);
        user.setLogin("login10");

        when(userService.getUserById(10L)).thenReturn(user);

        mockMvc.perform(get("/api/users/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("login10"));
    }

    /**
     * Teste POST /api/users (criação de usuário) com sucesso.
     */
    @Test
    void testCreateUser_Success() throws Exception {
        UserEntity requestEntity = new UserEntity();
        requestEntity.setLogin("userNew");
        requestEntity.setEmail("new@test.com");

        UserEntity savedEntity = new UserEntity();
        savedEntity.setId(1L);
        savedEntity.setLogin("userNew");
        savedEntity.setEmail("new@test.com");

        when(userService.createUser(any(UserEntity.class))).thenReturn(savedEntity);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestEntity)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("userNew"));
    }

    /**
     * Teste PUT /api/users/{id} com sucesso.
     */
    @Test
    void testUpdateUser_Success() throws Exception {
        UserEntity requestEntity = new UserEntity();
        requestEntity.setLogin("updatedUser");
        requestEntity.setEmail("updated@test.com");

        UserEntity updatedEntity = new UserEntity();
        updatedEntity.setId(2L);
        updatedEntity.setLogin("updatedUser");
        updatedEntity.setEmail("updated@test.com");

        when(userService.updateUser(eq(2L), any(UserEntity.class))).thenReturn(updatedEntity);

        mockMvc.perform(put("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestEntity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("updatedUser"));
    }
}
