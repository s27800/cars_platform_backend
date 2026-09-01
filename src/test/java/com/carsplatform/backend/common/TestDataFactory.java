package com.carsplatform.backend.common;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.carImages.CarImage;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.chassis.Chassis;
import com.carsplatform.backend.api.engines.Engine;
import com.carsplatform.backend.api.fuelReportLikes.FuelReportLike;
import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.insideDimensions.InsideDimensions;
import com.carsplatform.backend.api.reviewLikes.ReviewLike;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.outsideDimensions.OutsideDimensions;
import com.carsplatform.backend.api.performances.Performance;
import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.tags.Tag;
import com.carsplatform.backend.api.transmissions.Transmission;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.userSettings.UserSettings;
import com.carsplatform.backend.common.ModerationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * Factory class for creating test entities.
 */
public final class TestDataFactory {

    private TestDataFactory() {
        // Utility class
    }


    // ===== USER =====

    public static User.UserBuilder defaultUser() {
        return User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqA.rQ3W3FHtm.QxhJ2RWqcx9Z5Y6") // "password123" encoded
                .firstName("Test")
                .lastName("User")
                .isAdmin(false)
                .registrationDate(LocalDateTime.now());
    }

    public static User.UserBuilder adminUser() {
        return defaultUser()
                .username("admin")
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .isAdmin(true);
    }

    public static User createUser(String suffix) {
        return defaultUser()
                .username("testuser" + suffix)
                .email("testuser" + suffix + "@example.com")
                .build();
    }


    // ===== BRAND =====

    public static Brand.BrandBuilder defaultBrand() {
        return Brand.builder()
                .name("Test Brand")
                .country("Germany")
                .foundedYear(1900)
                .description("A test brand for unit testing.");
    }

    public static Brand createBrand(String name) {
        return defaultBrand()
                .name(name)
                .build();
    }


    // ===== MODEL =====

    public static Model.ModelBuilder defaultModel(Brand brand) {
        return Model.builder()
                .brand(brand)
                .name("Test Model")
                .description("A test model for unit testing.");
    }

    public static Model createModel(Brand brand, String name) {
        return defaultModel(brand)
                .name(name)
                .build();
    }


    // ===== GENERATION =====

    public static Generation.GenerationBuilder defaultGeneration(Model model) {
        return Generation.builder()
                .model(model)
                .name("Gen I");
    }

    public static Generation createGeneration(Model model, String name) {
        return defaultGeneration(model)
                .name(name)
                .build();
    }


    // ===== BODY TYPE =====

    public static BodyType.BodyTypeBuilder defaultBodyType() {
        return BodyType.builder()
                .name("Sedan");
    }

    public static BodyType createBodyType(String name) {
        return defaultBodyType()
                .name(name)
                .build();
    }


    // ===== ENGINE =====

    public static Engine.EngineBuilder defaultEngine() {
        return Engine.builder()
                .engineCode("2.0 TSI")
                .productionYears("2015-2020")
                .displacement(1984)
                .engineType("Petrol")
                .maxPower(190)
                .maxPowerRotationSpeed(4200)
                .turbo("Turbocharged")
                .cylindersNumber(4)
                .cylindersLayout("Inline")
                .valvesNumber(16)
                .ignition("Direct Injection")
                .injectionType("Direct")
                .maxTorque(320)
                .maxTorqueRotationSpeed(2500);
    }


    // ===== TRANSMISSION =====

    public static Transmission.TransmissionBuilder defaultTransmission() {
        return Transmission.builder()
                .transmissionType("Automatic")
                .transmissionName("DSG")
                .gearsNumber(7)
                .clutchType("Dual-clutch");
    }


    // ===== CHASSIS =====

    public static Chassis.ChassisBuilder defaultChassis() {
        return Chassis.builder()
                .basicRims("17\"")
                .optionalRims("18\", 19\"")
                .basicTires("225/45 R17")
                .optionalTires("235/40 R18")
                .frontBrakes("Ventilated disc")
                .backBrakes("Disc")
                .frontBrakesRadius(312)
                .backBrakesRadius(286)
                .frontBrakesThickness(25)
                .backBrakesThickness(12);
    }


    // ===== PERFORMANCE =====

    public static Performance.PerformanceBuilder defaultPerformance() {
        return Performance.builder()
                .maxSpeed(250)
                .acceleration0100(new BigDecimal("7.2"))
                .acceleration100200(new BigDecimal("22.5"))
                .fuelTankCapacity(60)
                .fuelConsumptionCity(new BigDecimal("9.5"))
                .fuelConsumptionRoute(new BigDecimal("6.2"))
                .fuelConsumptionMixed(new BigDecimal("7.4"))
                .rangeCity(630)
                .rangeRoute(968)
                .rangeMixed(810);
    }


    // ===== INSIDE DIMENSIONS =====

    public static InsideDimensions.InsideDimensionsBuilder defaultInsideDimensions() {
        return InsideDimensions.builder()
                .heightFromSeatToRoofFront(1020)
                .heightFromSeatToRoofBack(960)
                .maxTrunkSpace(1680)
                .minTrunkSpace(590)
                .minTrunkLength(1020)
                .maxTrunkLength(1920)
                .trunkWidth(1050)
                .trunkHeight(500);
    }


    // ===== OUTSIDE DIMENSIONS =====

    public static OutsideDimensions.OutsideDimensionsBuilder defaultOutsideDimensions() {
        return OutsideDimensions.builder()
                .length(4584)
                .height(1456)
                .width(1832)
                .widthWithMirrors(2018)
                .heightWithOpenTrunk(1986)
                .wheelBase(2731)
                .wheelBaseFront(1563)
                .wheelBaseBack(1537)
                .overhangFront(889)
                .overhangBack(964)
                .clearance(115)
                .maxRoofLoad(75);
    }


    // ===== CAR =====

    public static Car.CarBuilder defaultCar(Generation generation, BodyType bodyType) {
        return Car.builder()
                .name("Test Car 2.0 TSI")
                .description("A test car for unit testing.")
                .doorsNumber(5)
                .seatsNumber(5)
                .productionYears("2015-2020")
                .bodyType(bodyType)
                .generation(generation)
                .engine(defaultEngine().build())
                .transmission(defaultTransmission().build())
                .chassis(defaultChassis().build())
                .performance(defaultPerformance().build())
                .insideDimensions(defaultInsideDimensions().build())
                .outsideDimensions(defaultOutsideDimensions().build());
    }

    public static Car createCompleteCar(Brand brand, String carName) {
        Model model = createModel(brand, brand.getName() + " Model");
        Generation generation = createGeneration(model, "Gen I");
        BodyType bodyType = createBodyType("Sedan");
        return defaultCar(generation, bodyType)
                .name(carName)
                .build();
    }


    // ===== TAG =====

    public static Tag.TagBuilder defaultTag() {
        return Tag.builder()
                .name("Test Tag");
    }

    public static Tag createTag(String name) {
        return defaultTag()
                .name(name)
                .build();
    }


    // ===== REVIEW =====

    public static Review.ReviewBuilder defaultReview(User user, Car car) {
        return Review.builder()
                .user(user)
                .car(car)
                .comment("This is a test review comment for testing.")
                .engineRating(4)
                .transmissionRating(5)
                .steeringRating(4)
                .suspensionRating(3)
                .visibilityRating(4)
                .ergonomicsRating(4)
                .soundProofingRating(3)
                .interiorSpaceRating(4)
                .maintenanceRating(3)
                .priceQualityRating(4)
                .failureFreeRating(4)
                .status(ModerationStatus.PENDING);
    }


    // ===== LIKE =====

    public static ReviewLike.ReviewLikeBuilder defaultReviewLike(User user, Review review) {
        return ReviewLike.builder()
                .user(user)
                .review(review);
    }


    // ===== FUEL REPORT LIKE =====

    public static FuelReportLike.FuelReportLikeBuilder defaultFuelReportLike(User user, FuelReport fuelReport) {
        return FuelReportLike.builder()
                .user(user)
                .fuelReport(fuelReport);
    }


    // ===== FUEL REPORT =====

    public static FuelReport.FuelReportBuilder defaultFuelReport(User user, Car car) {
        return FuelReport.builder()
                .user(user)
                .car(car)
                .fuelConsumption(new BigDecimal("7.8"))
                .comment("Test fuel consumption report.")
                .status(ModerationStatus.PENDING);
    }


    // ===== CAR IMAGE =====

    public static CarImage.CarImageBuilder defaultCarImage(Car car) {
        return CarImage.builder()
                .car(car)
                .imageUrl("https://example.com/images/car-test.jpg")
                .isMain(false);
    }


    // ===== USER SETTINGS =====

    public static UserSettings.UserSettingsBuilder defaultUserSettings(User user) {
        return UserSettings.builder()
                .user(user)
                .theme("light");
    }
}
