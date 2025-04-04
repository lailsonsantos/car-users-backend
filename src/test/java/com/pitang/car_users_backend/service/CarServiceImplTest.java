package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.exception.CarErrorCode;
import com.pitang.car_users_backend.exception.CarException;
import com.pitang.car_users_backend.model.Car;
import com.pitang.car_users_backend.repository.CarRepository;
import com.pitang.car_users_backend.util.CarValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    /**
     * Testa sucesso no retorno da foto não encontrada
     * @throws Exception
     */
    @Test
    void testGetCarPhotoResource_Success() throws Exception {
        Long carId = 1L;
        Car car = new Car();
        car.setId(carId);

        when(repository.findById(carId)).thenReturn(Optional.of(car));

        Path uploadPath = Paths.get("uploads/cars").toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        Path photoPath = uploadPath.resolve("car_" + carId + ".jpg");
        Files.write(photoPath, "imagem fake".getBytes());

        Resource resource = carService.getCarPhotoResource(carId);
        assertNotNull(resource, "O recurso da imagem não deveria ser nulo");
        assertTrue(resource.exists(), "O recurso deveria existir");
        assertTrue(resource.isReadable(), "O recurso deveria ser legível");

        Files.deleteIfExists(photoPath);
    }

    /**
     * Testa falha no retorno da foto não encontrada
     */
    @Test
    void testGetCarPhotoResource_NotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(CarException.class, () -> carService.getCarPhotoResource(99L));
    }

    /**
     * Testa sucesso na importação da foto
     * @throws Exception
     */
    @Test
    void testUploadCarPhoto_Success() throws Exception {
        Long carId = 2L;
        Car car = new Car();
        car.setId(carId);

        when(repository.findById(carId)).thenReturn(Optional.of(car));
        when(repository.save(any(Car.class))).thenReturn(car);

        MockMultipartFile file = new MockMultipartFile(
                "file", "carro.jpg", "image/jpeg", "imagem de carro".getBytes()
        );

        Car result = carService.uploadCarPhoto(carId, file);

        assertNotNull(result, "O carro retornado não deveria ser nulo");
        assertEquals("/api/cars/" + carId + "/photo", result.getPhotoUrl(),
                "A URL da foto deveria ser atualizada corretamente");

        Path uploadPath = Paths.get("uploads/cars").toAbsolutePath().normalize();
        Files.deleteIfExists(uploadPath.resolve("car_" + carId + ".jpg"));
    }

    /**
     * Testa falha na importação da foto tipo arquivo
     */
    @Test
    void testUploadCarPhoto_InvalidFileType() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "documento.txt", "text/plain", "texto inválido".getBytes()
        );

        CarException ex = assertThrows(CarException.class, () -> carService.uploadCarPhoto(3L, file));
        assertEquals(CarErrorCode.INVALID_PHOTO.getMessage(), ex.getMessage(),
                "Deveria lançar erro para tipo de arquivo inválido");
    }

    /**
     * Testa falha na importação da foto
     */
    @Test
    void testUploadCarPhoto_EmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "carro.jpg", "image/jpeg", new byte[0]
        );

        CarException ex = assertThrows(CarException.class, () -> carService.uploadCarPhoto(4L, file));
        assertEquals(CarErrorCode.INVALID_PHOTO.getMessage(), ex.getMessage(),
                "Deveria lançar erro para arquivo vazio");
    }

}
