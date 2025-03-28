package com.pitang.car_users_backend.repository;

import com.pitang.car_users_backend.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
