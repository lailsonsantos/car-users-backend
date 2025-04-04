package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.exception.CarErrorCode;
import com.pitang.car_users_backend.exception.CarException;
import com.pitang.car_users_backend.model.Car;
import com.pitang.car_users_backend.repository.CarRepository;
import com.pitang.car_users_backend.util.CarValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Implementação de {@link CarService}, contendo a lógica de negócio
 * para criação, atualização, deleção e consulta de carros.
 */
@Service
public class CarServiceImpl implements CarService {

    private static final Logger logger = LoggerFactory.getLogger(CarServiceImpl.class);

    private final CarRepository repository;

    public CarServiceImpl(CarRepository repository) {
        this.repository = repository;
    }

    @Override
    public Car createCar(Car car) {
        if (CarValidationUtil.hasMissingFields(car)) {
            throw new CarException(CarErrorCode.MISSING_FIELDS);
        }

        if (!CarValidationUtil.isValid(car)) {
            throw new CarException(CarErrorCode.INVALID_FIELDS);
        }

        if (licensePlateExists(car.getLicensePlate())) {
            throw new CarException(CarErrorCode.LICENSE_PLATE_EXISTS);
        }

        return repository.save(car);
    }

    @Override
    public Car updateCar(Long id, Car car) {
        Car existing = repository.findById(id)
                .orElseThrow(() -> new CarException(CarErrorCode.CAR_NOT_FOUND));

        if (CarValidationUtil.hasMissingFields(car)) {
            throw new CarException(CarErrorCode.MISSING_FIELDS);
        }

        if (!CarValidationUtil.isValid(car)) {
            throw new CarException(CarErrorCode.INVALID_FIELDS);
        }

        if (car.getPhotoUrl() != null && !car.getPhotoUrl().isEmpty()) {
            existing.setPhotoUrl(car.getPhotoUrl());
        }

        existing.setLicensePlate(car.getLicensePlate());
        existing.setColor(car.getColor());
        existing.setModel(car.getModel());
        existing.setYear(car.getYear());

        return repository.save(existing);
    }

    @Override
    public Car updateCarPhoto(Long id, String photoUrl) {
        Car car = repository.findById(id)
                .orElseThrow(() -> new CarException(CarErrorCode.CAR_NOT_FOUND));

        car.setPhotoUrl(photoUrl);
        return repository.save(car);
    }

    @Override
    public void deleteCar(Long id) {
        if (!repository.existsById(id)) {
            throw new CarException(CarErrorCode.CAR_NOT_FOUND);
        }
        repository.deleteById(id);
    }

    @Override
    public List<Car> getAllCars() {
        return repository.findAll();
    }

    @Override
    public Car getCarById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CarException(CarErrorCode.CAR_NOT_FOUND));
    }

    @Override
    public Car getCarUserById(Long id) {
        Car car = repository.findById(id)
                .orElseThrow(() -> new CarException(CarErrorCode.CAR_NOT_FOUND));

        car.setUsageCount(car.getUsageCount() + 1);
        this.updateCar(id, car);

        if (car.getUser() != null) {
            car.getUser().recalculateTotalUsage();
        }

        return car;
    }

    @Override
    public List<Car> getCarsByLoggedUser(Long id) {
        return repository.findAllByUser_Id(id);
    }

    @Override
    public boolean licensePlateExists(String licensePlate) {
        return repository.existsByLicensePlate(licensePlate);
    }

    @Override
    public Resource getCarPhotoResource(Long id) {
        Car car = getCarById(id);
        if (car == null) {
            return null;
        }
        try {
            Path uploadPath = Paths.get("uploads/cars").toAbsolutePath().normalize();
            try (Stream<Path> paths = Files.list(uploadPath)) {
                Optional<Path> photoPath = paths
                        .filter(path -> path.getFileName().toString().startsWith("car_" + id + "."))
                        .findFirst();
                if (photoPath.isPresent()) {
                    Resource resource = new UrlResource(photoPath.get().toUri());
                    if (resource.exists() && resource.isReadable()) {
                        return resource;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Erro ao retornar a foto: {}", ex.getMessage());
        }
        return null;
    }

    @Override
    public Car uploadCarPhoto(Long id, MultipartFile file) {

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
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new CarException(CarErrorCode.UPLOAD_FAILED); // lailson
        }

        String fileName = "car_" + id + fileExtension;
        Path filePath = uploadPath.resolve(fileName);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadPath, "car_" + id + ".*")) {
            for (Path existingFile : stream) {
                Files.delete(existingFile);
            }
        } catch (IOException ex) {
            logger.error("Erro de limpeza dos arquivos: {}", ex.getMessage());
        }

        try {
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new CarException(CarErrorCode.UPLOAD_FAILED);
        }

        return updateCarPhoto(id, "/api/cars/" + id + "/photo");
    }

}
