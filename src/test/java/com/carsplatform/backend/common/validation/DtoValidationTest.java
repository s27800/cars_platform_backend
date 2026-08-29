package com.carsplatform.backend.common.validation;

import com.carsplatform.backend.api.authentication.dtos.LoginRequest;
import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.dataProposal.dtos.CreateDataProposalRequest;
import com.carsplatform.backend.api.fuelReports.dtos.CreateFuelReportRequest;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.reviews.dtos.CreateReviewRequest;
import com.carsplatform.backend.api.userSettings.dtos.UpdateUserSettingsRequest;
import com.carsplatform.backend.api.users.dtos.UserChangePasswordRequest;
import com.carsplatform.backend.api.users.dtos.UserModifyRequest;
import com.carsplatform.backend.common.MockMvcTestBase;
import com.carsplatform.backend.common.TestDataFactory;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("DTO Validation Integration Tests")
class DtoValidationTest extends MockMvcTestBase {

    @Autowired
    private EntityManager entityManager;

    private String userToken;
    private Car testCar;

    @BeforeEach
    void setUpUser() throws Exception {

        // Create test brand
        Brand brand = TestDataFactory.defaultBrand()
                .name("ValidationBrand")
                .build();

        entityManager.persist(brand);

        // Create test model
        Model model = TestDataFactory.defaultModel(brand)
                .name("ValidationModel")
                .build();

        entityManager.persist(model);

        // Create test generation
        Generation generation = TestDataFactory.defaultGeneration(model)
                .name("ValidationGen")
                .build();

        entityManager.persist(generation);

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .name("ValidationBody")
                .build();

        entityManager.persist(bodyType);

        // Create test car
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .name("ValidationCar")
                .build();

        entityManager.persist(testCar.getEngine());
        entityManager.persist(testCar.getTransmission());
        entityManager.persist(testCar.getChassis());
        entityManager.persist(testCar.getPerformance());
        entityManager.persist(testCar.getInsideDimensions());
        entityManager.persist(testCar.getOutsideDimensions());
        entityManager.persist(testCar);

        entityManager.flush();

        // Register test user
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("validationuser" + System.currentTimeMillis())
                .email("validationuser" + System.currentTimeMillis() + "@example.com")
                .password("Password123!")
                .firstName("Test")
                .lastName("User")
                .build();

        // Perform registration and extract token
        String response = performPostNoAuth("/api/auth/register", registerRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        userToken = objectMapper.readTree(response).get("accessToken").asText();
    }


    @Nested
    @DisplayName("RegisterRequest Validation")
    class RegisterRequestValidationTests {

        @Test
        @DisplayName("returns 400 when username is blank")
        void register_BlankUsername_Returns400WithFieldError() throws Exception {

            // Create request with blank username
            RegisterRequest request = RegisterRequest.builder()
                    .username("")
                    .email("valid@example.com")
                    .password("Password123!")
                    .firstName("Test")
                    .lastName("User")
                    .build();

            // Perform request and assert validation error for username
            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.username").exists());
        }

        @Test
        @DisplayName("returns 400 when email is invalid format")
        void register_InvalidEmail_Returns400WithFieldError() throws Exception {

            // Create request with invalid email
            RegisterRequest request = RegisterRequest.builder()
                    .username("validuser")
                    .email("not-an-email")
                    .password("Password123!")
                    .firstName("Test")
                    .lastName("User")
                    .build();

            // Perform request and assert validation error for email
            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email").exists());
        }

        @Test
        @DisplayName("returns 400 when password is too short")
        void register_ShortPassword_Returns400WithFieldError() throws Exception {

            // Create request with short password
            RegisterRequest request = RegisterRequest.builder()
                    .username("validuser")
                    .email("valid@example.com")
                    .password("123")
                    .firstName("Test")
                    .lastName("User")
                    .build();

            // Perform request and assert validation error for password
            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.password").exists());
        }

        @Test
        @DisplayName("returns 400 with multiple field errors")
        void register_MultipleInvalidFields_Returns400WithMultipleErrors() throws Exception {

            // Create request with multiple invalid fields
            RegisterRequest request = RegisterRequest.builder()
                    .username("")
                    .email("invalid")
                    .password("")
                    .build();

            // Perform request and assert multiple validation errors
            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.errors", aMapWithSize(greaterThanOrEqualTo(3))));
        }

        @Test
        @DisplayName("returns 400 when all fields are null")
        void register_NullFields_Returns400() throws Exception {

            // Create request with all fields null
            RegisterRequest request = RegisterRequest.builder().build();

            // Perform request and assert validation error
            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").exists());
        }
    }


    @Nested
    @DisplayName("LoginRequest Validation")
    class LoginRequestValidationTests {

        @Test
        @DisplayName("returns 400 when username is blank")
        void login_BlankUsername_Returns400() throws Exception {

            // Create request with blank username
            LoginRequest request = LoginRequest.builder()
                    .username("")
                    .password("Password123!")
                    .build();

            // Perform request and assert validation error for username
            performPostNoAuth("/api/auth/login", request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when password is blank")
        void login_BlankPassword_Returns400() throws Exception {

            // Create request with blank password
            LoginRequest request = LoginRequest.builder()
                    .username("someuser")
                    .password("")
                    .build();

            // Perform request and assert validation error for password
            performPostNoAuth("/api/auth/login", request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when both fields are blank")
        void login_EmptyRequest_Returns400() throws Exception {

            // Create request with both fields blank
            LoginRequest request = LoginRequest.builder()
                    .username("")
                    .password("")
                    .build();

            // Perform request and assert validation error
            performPostNoAuth("/api/auth/login", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").exists());
        }
    }


    @Nested
    @DisplayName("UserModifyRequest Validation")
    class UserModifyRequestValidationTests {

        @Test
        @DisplayName("returns 400 when email is invalid format")
        void updateProfile_InvalidEmail_Returns400() throws Exception {

            // Create request with invalid email
            UserModifyRequest request = UserModifyRequest.builder()
                    .email("not-valid-email")
                    .build();

            // Perform request and assert validation error for email
            performPutWithAuth("/api/users/me", request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email").exists());
        }
    }


    @Nested
    @DisplayName("UserChangePasswordRequest Validation")
    class UserChangePasswordRequestValidationTests {

        @Test
        @DisplayName("returns 400 when current password is blank")
        void changePassword_BlankCurrentPassword_Returns400() throws Exception {

            // Create request with blank current password
            UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                    .currentPassword("")
                    .newPassword("NewPassword123!")
                    .build();

            // Perform request and assert validation error for current password
            performPostWithAuth("/api/users/me/change-password", request, userToken)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when new password is too short")
        void changePassword_ShortNewPassword_Returns400() throws Exception {

            // Create request with short new password
            UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                    .currentPassword("Password123!")
                    .newPassword("123")
                    .build();

            // Perform request and assert validation error for new password
            performPostWithAuth("/api/users/me/change-password", request, userToken)
                    .andExpect(status().isBadRequest());
        }
    }


    @Nested
    @DisplayName("Error Response Format")
    class ErrorResponseFormatTests {

        @Test
        @DisplayName("error response contains required fields")
        void validationError_ContainsRequiredFields() throws Exception {

            // Create request with invalid fields
            RegisterRequest request = RegisterRequest.builder()
                    .username("")
                    .email("")
                    .password("")
                    .build();

            // Perform request and assert validation error
            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.timestamp").isNumber())
                    .andExpect(jsonPath("$.errors").isMap());
        }

        @Test
        @DisplayName("field errors have descriptive messages")
        void validationError_HasDescriptiveMessages() throws Exception {

            // Create request with invalid fields
            RegisterRequest request = RegisterRequest.builder()
                    .username("")
                    .email("invalid")
                    .password("123")
                    .build();

            // Perform request and assert validation error messages
            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email", containsString("valid")))
                    .andExpect(jsonPath("$.errors.username").isString());
        }
    }


    @Nested
    @DisplayName("CreateReviewRequest Validation")
    class CreateReviewRequestValidationTests {

        private CreateReviewRequest validReviewRequest() {
            return CreateReviewRequest.builder()
                    .comment("This is a valid comment with enough characters")
                    .engineRating(4.0)
                    .transmissionRating(4.0)
                    .steeringRating(4.0)
                    .suspensionRating(4.0)
                    .visibilityRating(4.0)
                    .ergonomicsRating(4.0)
                    .soundProofingRating(4.0)
                    .interiorSpaceRating(4.0)
                    .maintenanceRating(4.0)
                    .priceQualityRating(4.0)
                    .failureFreeRating(4.0)
                    .build();
        }

        @Test
        @DisplayName("returns 400 when comment is blank")
        void createReview_BlankComment_Returns400() throws Exception {

            // Create request with blank comment
            CreateReviewRequest request = validReviewRequest();
            request.setComment("");

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/reviews/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.comment").exists());
        }

        @Test
        @DisplayName("returns 400 when comment is too short")
        void createReview_ShortComment_Returns400() throws Exception {

            // Create request with comment shorter than 10 characters
            CreateReviewRequest request = validReviewRequest();
            request.setComment("Short");

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/reviews/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.comment").exists());
        }

        @Test
        @DisplayName("returns 400 when engine rating is null")
        void createReview_NullEngineRating_Returns400() throws Exception {

            // Create request with null engine rating
            CreateReviewRequest request = validReviewRequest();
            request.setEngineRating(null);

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/reviews/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.engineRating").exists());
        }

        @Test
        @DisplayName("returns 400 when engine rating is below minimum")
        void createReview_EngineRatingBelowMin_Returns400() throws Exception {

            // Create request with engine rating below 1
            CreateReviewRequest request = validReviewRequest();
            request.setEngineRating(0.0);

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/reviews/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.engineRating").exists());
        }

        @Test
        @DisplayName("returns 400 when engine rating exceeds maximum")
        void createReview_EngineRatingAboveMax_Returns400() throws Exception {

            // Create request with engine rating above 5
            CreateReviewRequest request = validReviewRequest();
            request.setEngineRating(6.0);

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/reviews/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.engineRating").exists());
        }

        @Test
        @DisplayName("returns 400 when transmission rating is null")
        void createReview_NullTransmissionRating_Returns400() throws Exception {

            // Create request with null transmission rating
            CreateReviewRequest request = validReviewRequest();
            request.setTransmissionRating(null);

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/reviews/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.transmissionRating").exists());
        }

        @Test
        @DisplayName("returns 400 when multiple ratings are invalid")
        void createReview_MultipleInvalidRatings_Returns400() throws Exception {

            // Create request with multiple invalid ratings
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Valid comment with enough characters")
                    .engineRating(null)
                    .transmissionRating(0.0)
                    .steeringRating(6.0)
                    .suspensionRating(4.0)
                    .visibilityRating(4.0)
                    .ergonomicsRating(4.0)
                    .soundProofingRating(4.0)
                    .interiorSpaceRating(4.0)
                    .maintenanceRating(4.0)
                    .priceQualityRating(4.0)
                    .failureFreeRating(4.0)
                    .build();

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/reviews/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", aMapWithSize(greaterThanOrEqualTo(3))));
        }

        @Test
        @DisplayName("returns 400 when all ratings are null")
        void createReview_AllRatingsNull_Returns400() throws Exception {

            // Create request with all ratings null
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Valid comment with enough characters")
                    .build();

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/reviews/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", aMapWithSize(greaterThanOrEqualTo(11))));
        }
    }


    @Nested
    @DisplayName("CreateFuelReportRequest Validation")
    class CreateFuelReportRequestValidationTests {

        @Test
        @DisplayName("returns 400 when fuel consumption is null")
        void createFuelReport_NullFuelConsumption_Returns400() throws Exception {

            // Create request with null fuel consumption
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(null)
                    .comment("Valid comment")
                    .build();

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/fuel-reports/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.fuelConsumption").exists());
        }

        @Test
        @DisplayName("returns 400 when fuel consumption is negative")
        void createFuelReport_NegativeFuelConsumption_Returns400() throws Exception {

            // Create request with negative fuel consumption
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("-5.5"))
                    .comment("Valid comment")
                    .build();

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/fuel-reports/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.fuelConsumption").exists());
        }

        @Test
        @DisplayName("returns 400 when fuel consumption is zero")
        void createFuelReport_ZeroFuelConsumption_Returns400() throws Exception {

            // Create request with zero fuel consumption
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(BigDecimal.ZERO)
                    .comment("Valid comment")
                    .build();

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/fuel-reports/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.fuelConsumption").exists());
        }

        @Test
        @DisplayName("returns 400 when fuel consumption has invalid format")
        void createFuelReport_InvalidDigitsFormat_Returns400() throws Exception {

            // Create request with invalid digits format (more than 2 integer digits)
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("123.5"))
                    .comment("Valid comment")
                    .build();

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/fuel-reports/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.fuelConsumption").exists());
        }

        @Test
        @DisplayName("returns 400 when comment exceeds maximum length")
        void createFuelReport_CommentTooLong_Returns400() throws Exception {

            // Create request with comment exceeding 1000 characters
            String longComment = "x".repeat(1001);
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("7.5"))
                    .comment(longComment)
                    .build();

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/fuel-reports/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.comment").exists());
        }

        @Test
        @DisplayName("accepts valid fuel report request")
        void createFuelReport_ValidRequest_Returns201() throws Exception {

            // Create valid request
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("7.5"))
                    .comment("Normal driving conditions")
                    .build();

            // Perform request and verify result -> 201 Created is returned
            performPostWithAuth("/api/fuel-reports/" + testCar.getId(), request, userToken)
                    .andExpect(status().isCreated());
        }
    }


    @Nested
    @DisplayName("CreateDataProposalRequest Validation")
    class CreateDataProposalRequestValidationTests {

        @Test
        @DisplayName("returns 400 when category is null")
        void createDataProposal_NullCategory_Returns400() throws Exception {

            // Create request with null category
            CreateDataProposalRequest request = new CreateDataProposalRequest();
            request.setCategory(null);
            request.setProposedValues(Map.of("maxPower", 220));

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/data-proposals/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.category").exists());
        }

        @Test
        @DisplayName("returns 400 when proposed values are empty")
        void createDataProposal_EmptyProposedValues_Returns400() throws Exception {

            // Create request with empty proposed values
            CreateDataProposalRequest request = new CreateDataProposalRequest();
            request.setCategory("ENGINE");
            request.setProposedValues(Map.of());

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/data-proposals/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.proposedValues").exists());
        }

        @Test
        @DisplayName("returns 400 when proposed values are null")
        void createDataProposal_NullProposedValues_Returns400() throws Exception {

            // Create request with null proposed values
            CreateDataProposalRequest request = new CreateDataProposalRequest();
            request.setCategory("ENGINE");
            request.setProposedValues(null);

            // Perform request and verify result -> 400 Bad Request is returned
            performPostWithAuth("/api/data-proposals/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.proposedValues").exists());
        }

        @Test
        @DisplayName("accepts valid data proposal request")
        void createDataProposal_ValidRequest_Returns201() throws Exception {

            // Create valid request
            CreateDataProposalRequest request = new CreateDataProposalRequest();
            request.setCategory("ENGINE");
            request.setComment("Correction of engine power");
            request.setProposedValues(Map.of("maxPower", 220));

            // Perform request and verify result -> 201 Created is returned
            performPostWithAuth("/api/data-proposals/" + testCar.getId(), request, userToken)
                    .andExpect(status().isCreated());
        }
    }


    @Nested
    @DisplayName("UpdateUserSettingsRequest Validation")
    class UpdateUserSettingsRequestValidationTests {

        @Test
        @DisplayName("returns 400 when theme is blank")
        void updateSettings_BlankTheme_Returns400() throws Exception {

            // Create request with blank theme
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("")
                    .build();

            // Perform request and verify result -> 400 Bad Request is returned
            performPutWithAuth("/api/user-settings", request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.theme").exists());
        }

        @Test
        @DisplayName("omitting a field leaves its current value untouched")
        void updateSettings_NullTheme_LeavesThemeUnchanged() throws Exception {

            // Perform PUT request to update the theme
            performPutWithAuth("/api/user-settings",
                    UpdateUserSettingsRequest.builder().theme("dark").build(), userToken)
                    .andExpect(status().isOk());

            // Perform request and verify result -> the previously stored theme stays unchanged
            performPutWithAuth("/api/user-settings",
                    UpdateUserSettingsRequest.builder().language("pl").build(), userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.language").value("pl"))
                    .andExpect(jsonPath("$.theme").value("dark"));
        }

        @Test
        @DisplayName("returns 400 when theme exceeds maximum length")
        void updateSettings_ThemeTooLong_Returns400() throws Exception {

            // Create request with theme exceeding 20 characters
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("thisthemeiswaytoolongtobevalid")
                    .build();

            // Perform request and verify result -> 400 Bad Request is returned
            performPutWithAuth("/api/user-settings", request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.theme").exists());
        }

        @Test
        @DisplayName("returns 400 when theme is not light or dark")
        void updateSettings_InvalidThemeValue_Returns400() throws Exception {

            // Create request with invalid theme value
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("blue")
                    .build();

            // Perform request and verify result -> 400 Bad Request is returned
            performPutWithAuth("/api/user-settings", request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.theme").exists());
        }

        @Test
        @DisplayName("accepts light theme")
        void updateSettings_LightTheme_Returns200() throws Exception {

            // Create request with valid light theme
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("light")
                    .build();

            // Perform request and verify result -> 200 OK is returned
            performPutWithAuth("/api/user-settings", request, userToken)
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("accepts dark theme")
        void updateSettings_DarkTheme_Returns200() throws Exception {

            // Create request with valid dark theme
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("dark")
                    .build();

            // Perform request and verify result -> 200 OK is returned
            performPutWithAuth("/api/user-settings", request, userToken)
                    .andExpect(status().isOk());
        }
    }
}
