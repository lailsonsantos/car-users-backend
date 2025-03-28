package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.model.Car;
import com.pitang.car_users_backend.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CarServiceTest {

    private CarRepository repository;
    private CarServiceImpl service;

    @BeforeEach
    public void setup() {
        repository = Mockito.mock(CarRepository.class);
        service = new CarServiceImpl(repository);
    }

    @Test
    public void shouldCreateCar() {
        Car car = new Car();
        car.setModel("Test");
        when(repository.save(car)).thenReturn(car);
        assertEquals("Test", service.createCar(car).getModel());
    }

    @Test
    public void shouldReturnAllCars() {
        when(repository.findAll()).thenReturn(Arrays.asList(new Car(), new Car()));
        assertEquals(2, service.getAllCars().size());
    }

    @Test
    public void shouldReturnCarById() {
        Car car = new Car();
        when(repository.findById(1L)).thenReturn(Optional.of(car));
        assertNotNull(service.getCarById(1L));
    }

    @Test
    public void shouldUpdateCar() {
        Car car = new Car();
        car.setModel("Updated");
        when(repository.save(car)).thenReturn(car);
        assertEquals("Updated", service.updateCar(1L, car).getModel());
    }

    @Test
    public void shouldDeleteCar() {
        doNothing().when(repository).deleteById(1L);
        service.deleteCar(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}
