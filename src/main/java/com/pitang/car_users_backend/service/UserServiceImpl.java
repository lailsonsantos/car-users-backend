package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.model.UserEntity;
import com.pitang.car_users_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        // Verifica se já existe um usuário com o mesmo email
        if (repository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        // Verifica se já existe um usuário com o mesmo login
        if (repository.existsByLogin(user.getLogin())) {
            throw new IllegalArgumentException("Login already exists");
        }
        // Criptografa a senha e define as datas
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(null);
        return repository.save(user);
    }

    @Override
    public UserEntity updateUser(Long id, UserEntity updatedUser) {
        UserEntity existingUser = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Atualiza os campos permitidos; não altera login nem data de criação
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setBirthday(updatedUser.getBirthday());
        existingUser.setPhone(updatedUser.getPhone());

        // Atualiza a senha se for informada (não vazia)
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        return repository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
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
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
