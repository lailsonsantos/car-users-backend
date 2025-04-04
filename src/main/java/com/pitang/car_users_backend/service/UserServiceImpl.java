package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.exception.UserErrorCode;
import com.pitang.car_users_backend.exception.UserException;
import com.pitang.car_users_backend.model.Car;
import com.pitang.car_users_backend.model.UserEntity;
import com.pitang.car_users_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.pitang.car_users_backend.util.UserValidationUtil.isValid;
import static com.pitang.car_users_backend.util.UserValidationUtil.isValidUpdate;

/**
 * Implementação de {@link UserService}, contendo a lógica de negócio
 * para criação, atualização, deleção e consulta de usuários.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private CarService carService;

    /**
     * Construtor que injeta o repositório de usuários e o codificador de senhas.
     * @param repository repositório para acesso a {@link UserEntity}
     * @param passwordEncoder codificador de senhas para armazenar com segurança
     */
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setCarService(CarService carService) {
        this.carService = carService;
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        validateUserForCreation(user);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(null);

        if (user.getCars() != null) {
            user.getCars().forEach(car -> car.setUser(user));
        }

        return repository.save(user);
    }

    @Override
    public UserEntity updateUser(Long id, UserEntity updatedUser) {
        UserEntity existingUser = repository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (updatedUser.getEmail() == null
                || updatedUser.getPhone() == null
                || updatedUser.getLastName() == null
                || updatedUser.getFirstName() == null
                || updatedUser.getBirthday() == null) {
            throw new UserException(UserErrorCode.MISSING_FIELDS);
        }
        
        if (!isValidUpdate(updatedUser)) {
            throw new UserException(UserErrorCode.INVALID_FIELDS);
        }

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setBirthday(updatedUser.getBirthday());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setLogin(updatedUser.getLogin());

        if(updatedUser.getPhotoUrl() != null && !updatedUser.getPhotoUrl().isEmpty()){
            existingUser.setPhotoUrl(updatedUser.getPhotoUrl());
        }

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        if(updatedUser.getCars() != null && !updatedUser.getCars().isEmpty()){
            List<Car> updatedCars = new ArrayList<>();

            for (Car carUpdate : updatedUser.getCars()) {
                if (carUpdate.getId() != null) {
                    Car existingCar = carService.getCarById(carUpdate.getId());

                    if (!existingCar.getUser().getId().equals(id)) {
                        throw new UserException(UserErrorCode.INVALID_FIELDS);
                    }

                    existingCar.setYear(carUpdate.getYear());
                    existingCar.setLicensePlate(carUpdate.getLicensePlate());
                    existingCar.setModel(carUpdate.getModel());
                    existingCar.setColor(carUpdate.getColor());

                    if (carUpdate.getPhotoUrl() != null && !carUpdate.getPhotoUrl().isEmpty()) {
                        existingCar.setPhotoUrl(carUpdate.getPhotoUrl());
                    }

                    updatedCars.add(carService.updateCar(existingCar.getId(), existingCar));
                } else {
                    carUpdate.setUser(existingUser);
                    updatedCars.add(carService.createCar(carUpdate));
                }
            }

            existingUser.getCars().clear();
            existingUser.getCars().addAll(updatedCars);
        }

        return repository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }
        repository.deleteById(id);
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public UserEntity getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    @Override
    public boolean emailExists(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean loginExists(String login) {
        return repository.existsByLogin(login);
    }

    @Override
    public UserEntity updateUserPhoto(Long id, String photoUrl) {
        UserEntity existingUser = repository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        existingUser.setPhotoUrl(photoUrl);
        return repository.save(existingUser);
    }

    /**
     * Valida campos obrigatórios e duplicidades (email/login) para criação de usuário.
     * @param user usuário a ser validado
     */
    private void validateUserForCreation(UserEntity user) {
        if (user.getEmail() == null
                || user.getPhone() == null
                || user.getLastName() == null
                || user.getFirstName() == null
                || user.getBirthday() == null
                || user.getPassword() == null
                || user.getLogin() == null) {
            throw new UserException(UserErrorCode.MISSING_FIELDS);
        }

        if (!isValid(user)) {
            throw new UserException(UserErrorCode.INVALID_FIELDS);
        }

        if (emailExists(user.getEmail())) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (loginExists(user.getLogin())) {
            throw new UserException(UserErrorCode.LOGIN_ALREADY_EXISTS);
        }
    }

    @Override
    public Resource getUserPhotoResource(Long id) {
        UserEntity user = getUserById(id);
        if (user == null) {
            return null;
        }
        try {
            Path uploadPath = Paths.get("uploads/users").toAbsolutePath().normalize();
            try (Stream<Path> paths = Files.list(uploadPath)) {
                Optional<Path> photoPath = paths
                        .filter(path -> path.getFileName().toString().startsWith("user_" + id + "."))
                        .findFirst();
                if (photoPath.isPresent()) {
                    Resource resource = new UrlResource(photoPath.get().toUri());
                    if (resource.exists() && resource.isReadable()) {
                        return resource;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Erro ao retornar a foto do usuário: {}", ex.getMessage());
        }
        return null;
    }

    @Override
    public UserEntity uploadUserPhoto(Long id, MultipartFile file) {
        // Validação de imagem
        if (file.isEmpty() || (file.getContentType() != null && !file.getContentType().startsWith("image/"))) {
            throw new UserException(UserErrorCode.INVALID_PHOTO);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uploadDir = "uploads/users";
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new UserException(UserErrorCode.UPLOAD_FAILED);
        }

        String fileName = "user_" + id + fileExtension;
        Path filePath = uploadPath.resolve(fileName);

        // Limpar arquivos existentes que correspondam a "user_{id}.*"
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadPath, "user_" + id + ".*")) {
            for (Path existingFile : stream) {
                Files.delete(existingFile);
            }
        } catch (IOException ex) {
            logger.error("Erro de limpeza dos arquivos: {}", ex.getMessage());
        }

        try {
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new UserException(UserErrorCode.UPLOAD_FAILED);
        }

        // Atualiza apenas a photoUrl do usuário
        return updateUserPhoto(id, "/api/users/" + id + "/photo");
    }

}
