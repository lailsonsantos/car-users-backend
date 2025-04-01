package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.model.Car;

import java.util.List;

/**
 * Interface que define métodos de negócio relacionados a carros.
 */
public interface CarService {

    /**
     * Cria um novo carro.
     * @param car carro a ser criado
     * @return carro criado
     */
    Car createCar(Car car);

    /**
     * Atualiza os dados de um carro existente.
     * @param id ID do carro
     * @param car dados para atualização
     * @return carro atualizado
     */
    Car updateCar(Long id, Car car);

    /**
     * Deleta um carro pelo ID.
     * @param id ID do carro a ser removido
     */
    void deleteCar(Long id);

    /**
     * Retorna todos os carros existentes no banco (todos os usuários).
     * @return lista de todos os carros
     */
    List<Car> getAllCars();

    /**
     * Retorna um carro específico pelo ID.
     * @param id ID do carro
     * @return carro encontrado
     */
    Car getCarById(Long id);

    /**
     * Retorna todos os carros pertencentes a um usuário específico.
     * @param id ID do usuário
     * @return lista de carros do usuário
     */
    List<Car> getCarsByLoggedUser(Long id);

    /**
     * Verifica se uma placa de carro já está cadastrada.
     * @param licensePlate placa do carro
     * @return true se existir, false caso contrário
     */
    boolean licensePlateExists(String licensePlate);

    /**
     * Retorna um carro específico pelo ID e incrementa o uso do carro.
     * @param id ID do carro
     * @return carro encontrado
     */
    Car getCarUserById(Long id);
}
