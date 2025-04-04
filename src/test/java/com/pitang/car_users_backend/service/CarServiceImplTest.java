package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.exception.CarErrorCode;
import com.pitang.car_users_backend.exception.CarException;
import com.pitang.car_users_backend.model.Car;
import com.pitang.car_users_backend.repository.CarRepository;
import com.pitang.car_users_backend.util.CarValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Classe de teste unitário para a implementação de {@link CarServiceImpl}.
 * Visa cobrir 100% das ramificações de código relacionadas às operações de criação,
 * atualização, deleção e consulta de carros.
 */
class CarServiceImplTest {

    private CarRepository repository;
    private CarServiceImpl carService;

    /**
     * Inicializa os mocks e a instância de serviço antes de cada teste.
     */
    @BeforeEach
    void setUp() {
        repository = Mockito.mock(CarRepository.class);
        carService = new CarServiceImpl(repository);
    }

    /**
     * Testa o cenário de criação de carro com sucesso, quando os campos são válidos
     * e a placa não existe previamente.
     */
    @Test
    void testCreateCar_Success() {
        Car car = new Car();
        car.setLicensePlate("ABC-1234");
        car.setModel("Fiesta");
        car.setYear(2020);
        car.setColor("Red");

        when(repository.existsByLicensePlate("ABC-1234")).thenReturn(false);
        when(repository.save(any(Car.class))).thenReturn(car);

        Car created = carService.createCar(car);

        assertNotNull(created, "O carro criado não deveria ser nulo");
        assertEquals("ABC-1234", created.getLicensePlate(), "A placa deveria ser 'ABC-1234'");
        verify(repository).save(car);
    }

    /**
     * Testa o cenário em que a criação de carro falha devido
     * a campos ausentes (método {@link CarValidationUtil#hasMissingFields(Car)}).
     */
    @Test
    void testCreateCar_MissingFields() {
        Car car = new Car(); // faltando licensePlate, model...

        CarException ex = assertThrows(CarException.class, () -> carService.createCar(car));
        assertEquals(CarErrorCode.MISSING_FIELDS.getMessage(), ex.getMessage(),
                "Deveria lançar erro de campos ausentes");
    }

    /**
     * Testa o cenário em que a criação de carro falha devido
     * a campos inválidos (método {@link CarValidationUtil#isValid(Car)}).
     */
    @Test
    void testCreateCar_InvalidFields() {
        Car car = new Car();
        car.setLicensePlate("AB"); // muito curta
        car.setModel("");
        car.setYear(1800); // muito antigo

        CarException ex = assertThrows(CarException.class, () -> carService.createCar(car));
        assertEquals(CarErrorCode.INVALID_FIELDS.getMessage(), ex.getMessage(),
                "Deveria lançar erro de campos inválidos");
    }

    /**
     * Testa o cenário em que a criação de carro falha devido
     * a placa já existente no sistema.
     */
    @Test
    void testCreateCar_LicensePlateExists() {
        Car car = new Car();
        car.setLicensePlate("ABC-1234");
        car.setModel("Fiesta");
        car.setYear(2021);
        car.setColor("Red");

        when(repository.existsByLicensePlate("ABC-1234")).thenReturn(true);

        CarException ex = assertThrows(CarException.class, () -> carService.createCar(car));
        assertEquals(CarErrorCode.LICENSE_PLATE_EXISTS.getMessage(), ex.getMessage(),
                "Deveria lançar erro de placa duplicada");
    }

    /**
     * Testa o cenário de atualização de carro com sucesso.
     * Verifica se os campos são atualizados corretamente.
     */
    @Test
    void testUpdateCar_Success() {
        Car existing = new Car();
        existing.setId(10L);
        existing.setLicensePlate("OLD-0000");
        existing.setModel("OldCar");
        existing.setYear(2010);

        when(repository.findById(10L)).thenReturn(Optional.of(existing));

        Car updateData = new Car();
        updateData.setLicensePlate("XYZ-9999");
        updateData.setModel("Focus");
        updateData.setYear(2021);
        updateData.setColor("Blue");

        when(repository.save(existing)).thenReturn(existing);

        Car updated = carService.updateCar(10L, updateData);

        assertEquals("XYZ-9999", updated.getLicensePlate(),
                "A placa deveria ter sido atualizada para 'XYZ-9999'");
        assertEquals("Focus", updated.getModel(),
                "O modelo deveria ter sido atualizado para 'Focus'");
        assertEquals(2021, updated.getYear(),
                "O ano de fabricação deveria ser 2021");
        assertEquals("Blue", updated.getColor(),
                "A cor deveria ter sido atualizada para 'Blue'");
        verify(repository).save(existing);
    }

    /**
     * Testa o cenário em que a atualização de carro falha devido
     * ao carro não encontrado na base de dados.
     */
    @Test
    void testUpdateCar_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Car updateData = new Car();
        updateData.setLicensePlate("AAA-1111");
        updateData.setModel("Gol");
        updateData.setYear(2022);

        CarException ex = assertThrows(CarException.class, () -> carService.updateCar(999L, updateData));
        assertEquals(CarErrorCode.CAR_NOT_FOUND.getMessage(), ex.getMessage(),
                "Deveria lançar erro de carro inexistente");
    }

    /**
     * Testa o cenário em que a atualização de carro falha devido
     * a campos ausentes.
     */
    @Test
    void testUpdateCar_MissingFields() {
        Car existing = new Car();
        existing.setId(1L);
        existing.setLicensePlate("ABC-1234");
        existing.setModel("Fiesta");
        existing.setYear(2020);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        Car updateData = new Car(); // sem licensePlate, model, year...

        CarException ex = assertThrows(CarException.class, () -> carService.updateCar(1L, updateData));
        assertEquals(CarErrorCode.MISSING_FIELDS.getMessage(), ex.getMessage(),
                "Deveria lançar erro de campos ausentes");
    }

    /**
     * Testa o cenário em que a atualização de carro falha devido
     * a campos inválidos (ex. placa curta, ano inválido etc).
     */
    @Test
    void testUpdateCar_InvalidFields() {
        Car existing = new Car();
        existing.setId(1L);
        existing.setLicensePlate("ABC-1234");
        existing.setModel("Fiesta");
        existing.setYear(2020);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        Car updateData = new Car();
        updateData.setLicensePlate("AB"); // muito curta
        updateData.setModel("");
        updateData.setYear(1850); // muito antigo

        CarException ex = assertThrows(CarException.class, () -> carService.updateCar(1L, updateData));
        assertEquals(CarErrorCode.INVALID_FIELDS.getMessage(), ex.getMessage(),
                "Deveria lançar erro de campos inválidos");
    }

    /**
     * Testa o cenário de deleção de carro com sucesso.
     */
    @Test
    void testDeleteCar_Success() {
        when(repository.existsById(5L)).thenReturn(true);

        carService.deleteCar(5L);
        verify(repository).deleteById(5L);
    }

    /**
     * Testa o cenário em que a deleção de carro falha devido
     * ao carro não existir.
     */
    @Test
    void testDeleteCar_NotFound() {
        when(repository.existsById(999L)).thenReturn(false);

        CarException ex = assertThrows(CarException.class, () -> carService.deleteCar(999L));
        assertEquals(CarErrorCode.CAR_NOT_FOUND.getMessage(), ex.getMessage(),
                "Deveria lançar erro de carro inexistente");
    }

    /**
     * Testa a recuperação de todos os carros (GET ALL).
     */
    @Test
    void testGetAllCars() {
        List<Car> mockCars = new ArrayList<>();
        mockCars.add(new Car());
        mockCars.add(new Car());

        when(repository.findAll()).thenReturn(mockCars);

        List<Car> result = carService.getAllCars();
        assertEquals(2, result.size(), "Deveria retornar 2 carros");
        verify(repository).findAll();
    }

    /**
     * Testa a recuperação de um carro por ID com sucesso.
     */
    @Test
    void testGetCarById_Found() {
        Car car = new Car();
        car.setId(123L);
        when(repository.findById(123L)).thenReturn(Optional.of(car));

        Car result = carService.getCarById(123L);
        assertNotNull(result, "O resultado não deveria ser nulo");
        assertEquals(123L, result.getId(), "O id deveria ser 123L");
    }

    /**
     * Testa o cenário em que a recuperação de carro falha devido ao ID não existir.
     */
    @Test
    void testGetCarById_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        CarException ex = assertThrows(CarException.class, () -> carService.getCarById(999L));
        assertEquals(CarErrorCode.CAR_NOT_FOUND.getMessage(), ex.getMessage(),
                "Deveria lançar erro de carro não encontrado");
    }

    /**
     * Testa a recuperação de carros por usuário logado.
     */
    @Test
    void testGetCarsByLoggedUser() {
        Car c1 = new Car();
        Car c2 = new Car();

        when(repository.findAllByUser_Id(10L)).thenReturn(List.of(c1, c2));

        List<Car> result = carService.getCarsByLoggedUser(10L);
        assertEquals(2, result.size(), "Deveria retornar 2 carros");
        verify(repository).findAllByUser_Id(10L);
    }

    /**
     * Testa a verificação de existência de placa.
     */
    @Test
    void testLicensePlateExists() {
        when(repository.existsByLicensePlate("TEST-1234")).thenReturn(true);
        boolean exists = carService.licensePlateExists("TEST-1234");
        assertTrue(exists, "Deveria retornar true para placa existente");
    }

    /**
     * Testa a verificação de placa inexistente.
     */
    @Test
    void testLicensePlateNotExists() {
        when(repository.existsByLicensePlate("DOESNT-9999")).thenReturn(false);
        boolean exists = carService.licensePlateExists("DOESNT-9999");
        assertFalse(exists, "Deveria retornar false para placa inexistente");
    }
}
