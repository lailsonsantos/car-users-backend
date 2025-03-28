package com.pitang.car_users_backend.controller;

import com.pitang.car_users_backend.model.Car;
import com.pitang.car_users_backend.service.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Car>> getAll() {
        List<Car> cars = service.getAllCars();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Car car = service.getCarById(id);
            return ResponseEntity.ok(car);
        } catch (RuntimeException ex) { // Ex: CarNotFoundException
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Car not found", "errorCode", 404));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Car car) {
        try {
            Car createdCar = service.createCar(car);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCar);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage(), "errorCode", 2));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Car car) {
        try {
            Car updatedCar = service.updateCar(id, car);
            return ResponseEntity.ok(updatedCar);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Car not found", "errorCode", 404));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.deleteCar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Car not found", "errorCode", 404));
        }
    }
}
