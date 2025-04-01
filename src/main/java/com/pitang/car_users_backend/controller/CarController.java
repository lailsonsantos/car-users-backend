package com.pitang.car_users_backend.controller;

import com.pitang.car_users_backend.Mapper.CarMapper;
import com.pitang.car_users_backend.dto.CarRequest;
import com.pitang.car_users_backend.dto.CarResponse;
import com.pitang.car_users_backend.exception.CarErrorCode;
import com.pitang.car_users_backend.exception.CarException;
import com.pitang.car_users_backend.model.Car;
import com.pitang.car_users_backend.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller para gerenciamento de carros.
 */
@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    /**
     * Retorna todos os carros do usuário logado.
     * @param userId o id do usuário (simula token)
     * @return a lista de carros do usuário
     */
    @GetMapping
    public ResponseEntity<List<CarResponse>> getAll(@RequestParam(required = false) Long userId) {
        validateUser(userId);
        List<Car> cars = service.getCarsByLoggedUser(userId);
        List<CarResponse> response = cars.stream().map(CarMapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Retorna um carro pelo id.
     * @param id o id do carro
     * @param userId o id do usuário
     * @return o objeto de resposta do carro
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getById(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        validateUser(userId);
        Car car = service.getCarById(id);
        return ResponseEntity.ok(CarMapper.toResponse(car));
    }

    /**
     * Cria um novo carro para o usuário logado.
     * @param userId o id do usuário
     * @param request objeto de requisição do carro
     * @return o objeto de resposta do carro criado
     */
    @PostMapping
    public ResponseEntity<CarResponse> create(@RequestParam(required = false) Long userId, @RequestBody CarRequest request) {
        validateUser(userId);
        Car carEntity = CarMapper.toEntity(request);
        Car created = service.createCar(carEntity);
        return ResponseEntity.status(201).body(CarMapper.toResponse(created));
    }

    /**
     * Atualiza um carro do usuário logado.
     * @param id o id do carro
     * @param userId o id do usuário
     * @param request objeto de requisição do carro
     * @return o objeto de resposta do carro atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<CarResponse> update(@PathVariable Long id, @RequestParam(required = false) Long userId, @RequestBody CarRequest request) {
        validateUser(userId);
        Car carEntity = CarMapper.toEntity(request);
        Car updated = service.updateCar(id, carEntity);
        return ResponseEntity.ok(CarMapper.toResponse(updated));
    }

    /**
     * Remove um carro do usuário logado.
     * @param id o id do carro
     * @param userId o id do usuário
     * @return resposta sem conteúdo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        validateUser(userId);
        service.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Faz o upload da foto de um carro.
     * @param id o id do carro
     * @param file o arquivo da foto
     * @return mensagem de sucesso
     */
    @PostMapping("/{id}/photo")
    public ResponseEntity<String> uploadCarPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String filePath = "uploads/cars/car_" + id + "_" + file.getOriginalFilename();
        try {
            file.transferTo(new java.io.File(filePath));
            return ResponseEntity.ok("Foto enviada com sucesso: " + filePath);
        } catch (Exception e) {
            throw new CarException(CarErrorCode.UPLOAD_FAILED);
        }
    }

    /**
     * Retorna os carros do usuário ordenados por uso.
     * @param userId o id do usuário
     * @return a lista ordenada de carros
     */
    @GetMapping("/ordered")
    public ResponseEntity<List<CarResponse>> getCarsOrderedByUsage(@RequestParam(required = false) Long userId) {
        validateUser(userId);
        List<Car> cars = service.getCarsByLoggedUser(userId);
        List<CarResponse> ordered = cars.stream()
                .sorted(Comparator.comparingInt(Car::getUsageCount)
                        .reversed()
                        .thenComparing(Car::getModel))
                .map(CarMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ordered);
    }

    /**
     * Valida se o usuário (token) está presente e é válido.
     * @param userId id do usuário
     */
    private void validateUser(Long userId) {
        if (userId == null) {
            throw new CarException(CarErrorCode.UNAUTHORIZED);
        }
        if (userId <= 0) {
            throw new CarException(CarErrorCode.UNAUTHORIZED_SESSION);
        }
    }
}
