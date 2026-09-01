package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.dtos.CarInfoResponse;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CarInfoMapper Tests")
class CarInfoMapperTest {

    @Autowired
    private CarInfoMapper carInfoMapper;

    private Car testCar;

    @BeforeEach
    void setUp() {
        Brand brand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .name("BMW")
                .build();
        Model model = TestDataFactory.defaultModel(brand)
                .id(UUID.randomUUID())
                .name("3 Series")
                .build();
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(UUID.randomUUID())
                .name("E90")
                .build();
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .build();
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(UUID.randomUUID())
                .name("320i")
                .build();
    }

    @Test
    @DisplayName("should map car to CarInfoResponse")
    void toCarInfo_ValidCar_ReturnsCarInfoResponse() {
        CarInfoResponse result = carInfoMapper.toCarInfo(testCar);

        assertThat(result).isNotNull();
        assertThat(result.getCarId()).isEqualTo(testCar.getId());
        assertThat(result.getCarName()).isEqualTo("320i");
        assertThat(result.getBrandName()).isEqualTo("BMW");
        assertThat(result.getModelName()).isEqualTo("3 Series");
        assertThat(result.getGenerationName()).isEqualTo("E90");
    }

    @Test
    @DisplayName("should return null when car is null")
    void toCarInfo_NullCar_ReturnsNull() {
        assertThat(carInfoMapper.toCarInfo(null)).isNull();
    }
}
