package com.pitang.car_users_backend.repository;

import com.pitang.car_users_backend.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório JPA para acesso à entidade {@link Car}.
 */
public interface CarRepository extends JpaRepository<Car, Long> {

    /**
     * Retorna todos os carros de um usuário específico.
     * @param id ID do usuário
     * @return lista de carros
     */
    List<Car> findAllByUser_Id(Long id);

    /**
     * Verifica se existe um carro pela placa.
     * @param licensePlate placa do carro
     * @return true se existir, false caso contrário
     */
    boolean existsByLicensePlate(String licensePlate);
}
