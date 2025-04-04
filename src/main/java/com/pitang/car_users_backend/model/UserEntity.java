package com.pitang.car_users_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidade que representa um usuário do sistema.
 * Agora possui um campo persistido para armazenar o total de utilizações.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars = new ArrayList<>();

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "total_usage_count")
    private int totalUsageCount;

    /**
     * Recalcula a soma de uso de todos os carros, persiste em totalUsageCount.
     * Caso prefira sempre manter sincronizado com o somatório real.
     */
    public void recalculateTotalUsage() {
        if (cars == null || cars.isEmpty()) {
            this.totalUsageCount = 0;
        } else {
            this.totalUsageCount = cars.stream()
                    .mapToInt(Car::getUsageCount)
                    .sum();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity user = (UserEntity) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
