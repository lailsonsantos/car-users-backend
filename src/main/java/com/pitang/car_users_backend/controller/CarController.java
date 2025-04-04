package com.pitang.car_users_backend.controller;

import com.pitang.car_users_backend.Mapper.CarMapper;
import com.pitang.car_users_backend.dto.CarRequest;
import com.pitang.car_users_backend.dto.CarResponse;
import com.pitang.car_users_backend.exception.CarErrorCode;
import com.pitang.car_users_backend.exception.CarException;
import com.pitang.car_users_backend.model.Car;
import com.pitang.car_users_backend.model.UserEntity;
import com.pitang.car_users_backend.service.CarService;
import com.pitang.car_users_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
 * Controller para gerenciamento de carros.
 */
@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService service;
    private final UserService userService;

    public CarController(CarService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    /**
     * Retorna todos os carros do usuário logado.
     * @param userId o id do usuário (simula token)
     * @return a lista de carros do usuário
     */
    @GetMapping
    public ResponseEntity<List<CarResponse>> getAll(@RequestParam Long userId) {
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
    public ResponseEntity<CarResponse> getById(@PathVariable Long id, @RequestParam Long userId) {
        validateUser(userId);
        Car car = service.getCarUserById(id);
        return ResponseEntity.ok(CarMapper.toResponse(car));
    }

    /**
     * Cria um novo carro para o usuário logado.
     * @param userId o id do usuário
     * @param request objeto de requisição do carro
     * @return o objeto de resposta do carro criado
     */
    @PostMapping
    public ResponseEntity<CarResponse> create(@RequestParam Long userId,
                                              @Valid @RequestBody CarRequest request) {
        validateUser(userId);
        UserEntity user = userService.getUserById(userId);
        Car carEntity = CarMapper.toEntity(request, user);
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
    public ResponseEntity<CarResponse> update(@PathVariable Long id, @RequestParam Long userId,
                                              @Valid @RequestBody CarRequest request) {
        validateUser(userId);
        UserEntity user = userService.getUserById(userId);
        Car carEntity = CarMapper.toEntity(request, user);
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
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long userId) {
        validateUser(userId);
        service.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retorna a foto de um carro.
     * @param id o id do carro
     * @return imagem do carro
     */
    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> getCarPhoto(@PathVariable Long id) {
        Car car = service.getCarById(id);
        if (car == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            // Procurar o arquivo com qualquer extensão
            Path uploadPath = Paths.get("uploads/cars").toAbsolutePath().normalize();

            // Listar todos os arquivos que começam com "car_[id]"
            try (Stream<Path> paths = Files.list(uploadPath)) {
                Optional<Path> photoPath = paths
                        .filter(path -> path.getFileName().toString().startsWith("car_" + id + "."))
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
     * Faz o upload da foto de um carro.
     * @param id o id do carro
     * @param file o arquivo da foto
     * @return mensagem de sucesso
     */
    @PostMapping("/{id}/photo")
    public ResponseEntity<CarResponse> uploadCarPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty() || (file.getContentType() != null && !file.getContentType().startsWith("image/"))) {
            throw new CarException(CarErrorCode.INVALID_PHOTO);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uploadDir = "uploads/cars";
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String fileName = "car_" + id + fileExtension;
        Path filePath = uploadPath.resolve(fileName);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadPath, "user_" + id + ".*")) {
            for (Path existingFile : stream) {
                Files.delete(existingFile);
            }
        }

        file.transferTo(filePath);

        Car updatedCar = service.updateCarPhoto(id, "/api/cars/" + id + "/photo");

        return ResponseEntity.ok(CarMapper.toResponse(updatedCar));
    }

    /**
     * Retorna os carros do usuário ordenados por uso.
     * @param userId o id do usuário
     * @return a lista ordenada de carros
     */
    @GetMapping("/ordered")
    public ResponseEntity<List<CarResponse>> getCarsOrderedByUsage(@RequestParam Long userId) {
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
