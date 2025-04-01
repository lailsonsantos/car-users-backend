package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.exception.UserErrorCode;
import com.pitang.car_users_backend.exception.UserException;
import com.pitang.car_users_backend.model.UserEntity;
import com.pitang.car_users_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.pitang.car_users_backend.util.UserValidationUtil.isValid;

/**
 * Implementação de {@link UserService}, contendo a lógica de negócio
 * para criação, atualização, deleção e consulta de usuários.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Construtor que injeta o repositório de usuários e o codificador de senhas.
     * @param repository repositório para acesso a {@link UserEntity}
     * @param passwordEncoder codificador de senhas para armazenar com segurança
     */
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
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

        if (updatedUser.getEmail() == null || updatedUser.getLogin() == null) {
            throw new UserException(UserErrorCode.MISSING_FIELDS);
        }
        if (!isValid(updatedUser)) {
            throw new UserException(UserErrorCode.INVALID_FIELDS);
        }

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setBirthday(updatedUser.getBirthday());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setLogin(updatedUser.getLogin());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
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

    /**
     * Valida campos obrigatórios e duplicidades (email/login) para criação de usuário.
     * @param user usuário a ser validado
     */
    private void validateUserForCreation(UserEntity user) {
        if (user.getEmail() == null || user.getLogin() == null || user.getPassword() == null) {
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

    /**
     * Método auxiliar para validação de campos obrigatórios e formato.
     * @param user usuário a ser validado
     */
    private void validateUserForUpdate(UserEntity user) {
        if (user.getEmail() == null || user.getLogin() == null) {
            throw new UserException(UserErrorCode.MISSING_FIELDS);
        }
        if (!isValid(user)) {
            throw new UserException(UserErrorCode.INVALID_FIELDS);
        }
    }
}
