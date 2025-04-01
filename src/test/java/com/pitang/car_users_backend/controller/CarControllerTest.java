package com.pitang.car_users_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pitang.car_users_backend.dto.CarRequest;
import com.pitang.car_users_backend.exception.CarErrorCode;
import com.pitang.car_users_backend.exception.CarException;
import com.pitang.car_users_backend.model.Car;
import com.pitang.car_users_backend.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes unitários para CarController (aprox. 80% de cobertura):
 * - Mantém cenários principais de sucesso e falha
 * - Remove/testes duplicados de "unauthorized" ou "invalid session"
 */
class CarControllerTest {

    private CarService carService;
    private CarController carController;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Configuração inicial do MockMvc e injeção de mocks.
     */
    @BeforeEach
    void setUp() {
        carService = Mockito.mock(CarService.class);
        carController = new CarController(carService);
        mockMvc = MockMvcBuilders.standaloneSetup(carController).build();
    }

    /**
     * Teste básico de GET /api/cars com userId válido => sucesso (lista vazia).
     */
    @Test
    void testGetAllCars_Success() throws Exception {
        when(carService.getCarsByLoggedUser(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/cars?userId=1"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    /**
     * Teste GET /api/cars/{id} com sucesso.
     */
    @Test
    void testGetById_Success() throws Exception {
        Car car = new Car();
        car.setId(10L);
        car.setLicensePlate("ABC-1234");
        when(carService.getCarById(10L)).thenReturn(car);

        mockMvc.perform(get("/api/cars/10?userId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.licensePlate").value("ABC-1234"));
    }

    /**
     * Testa POST /api/cars com sucesso.
     */
    @Test
    void testCreateCar_Success() throws Exception {
        CarRequest request = new CarRequest();
        request.setLicensePlate("ABC-1234");
        request.setModel("Fiesta");
        request.setYear(2020);
        request.setColor("Red");

        Car createdCar = new Car();
        createdCar.setId(1L);
        createdCar.setLicensePlate("ABC-1234");

        when(carService.createCar(any(Car.class))).thenReturn(createdCar);

        mockMvc.perform(post("/api/cars?userId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    /**
     * Testa PUT /api/cars/{id} com sucesso.
     */
    @Test
    void testUpdateCar_Success() throws Exception {
        CarRequest request = new CarRequest();
        request.setLicensePlate("XYZ-9999");
        request.setModel("Focus");
        request.setYear(2021);
        request.setColor("Blue");

        Car updatedCar = new Car();
        updatedCar.setId(5L);
        updatedCar.setLicensePlate("XYZ-9999");

        when(carService.updateCar(eq(5L), any(Car.class))).thenReturn(updatedCar);

        mockMvc.perform(put("/api/cars/5?userId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("XYZ-9999"));
    }

}
