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
import com.carsplatform.backend.common.ProposalCategory;
import com.carsplatform.backend.common.TestDataFactory;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

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
        Brand brand = TestDataFactory.defaultBrand()
                .name("ValidationBrand")
                .build();

        entityManager.persist(brand);
        Model model = TestDataFactory.defaultModel(brand)
                .name("ValidationModel")
                .build();

        entityManager.persist(model);
        Generation generation = TestDataFactory.defaultGeneration(model)
                .name("ValidationGen")
                .build();

        entityManager.persist(generation);
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .name("ValidationBody")
                .build();

        entityManager.persist(bodyType);
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

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("validationuser" + System.currentTimeMillis())
                .email("validationuser" + System.currentTimeMillis() + "@example.com")
                .password("Password123!")
                .firstName("Test")
                .lastName("User")
                .build();

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
            RegisterRequest request = RegisterRequest.builder()
                    .username("")
                    .email("valid@example.com")
                    .password("Password123!")
                    .firstName("Test")
                    .lastName("User")
                    .build();

            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.username").exists());
        }

        @Test
        @DisplayName("returns 400 when email is invalid format")
        void register_InvalidEmail_Returns400WithFieldError() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("validuser")
                    .email("not-an-email")
                    .password("Password123!")
                    .firstName("Test")
                    .lastName("User")
                    .build();

            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email").exists());
        }

        @Test
        @DisplayName("returns 400 when password is too short")
        void register_ShortPassword_Returns400WithFieldError() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("validuser")
                    .email("valid@example.com")
                    .password("123")
                    .firstName("Test")
                    .lastName("User")
                    .build();

            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.password").exists());
        }

        @Test
        @DisplayName("returns 400 when password has no uppercase letter or digit")
        void register_PasswordWithoutComplexity_Returns400WithFieldError() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("validuser")
                    .email("valid@example.com")
                    .password("onlylowercase")
                    .firstName("Test")
                    .lastName("User")
                    .build();

            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.password").exists());
        }

        @Test
        @DisplayName("returns 400 when password exceeds the 72 bytes used by BCrypt")
        void register_TooLongPassword_Returns400WithFieldError() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("validuser")
                    .email("valid@example.com")
                    .password("Aa1" + "x".repeat(80))
                    .firstName("Test")
                    .lastName("User")
                    .build();

            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.password").exists());
        }

        @Test
        @DisplayName("returns 400, not 500, when first name is longer than the column")
        void register_TooLongFirstName_Returns400WithFieldError() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("validuser")
                    .email("valid@example.com")
                    .password("Password123!")
                    .firstName("A".repeat(150))
                    .lastName("User")
                    .build();

            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.firstName").exists());
        }

        @Test
        @DisplayName("returns 400, not 500, when username is longer than the column")
        void register_TooLongUsername_Returns400WithFieldError() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("u".repeat(80))
                    .email("valid@example.com")
                    .password("Password123!")
                    .firstName("Test")
                    .lastName("User")
                    .build();

            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.username").exists());
        }

        @Test
        @DisplayName("returns 400 with multiple field errors")
        void register_MultipleInvalidFields_Returns400WithMultipleErrors() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("")
                    .email("invalid")
                    .password("")
                    .build();

            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.errors", aMapWithSize(greaterThanOrEqualTo(3))));
        }

        @Test
        @DisplayName("returns 400 when all fields are null")
        void register_NullFields_Returns400() throws Exception {
            RegisterRequest request = RegisterRequest.builder().build();

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
            LoginRequest request = LoginRequest.builder()
                    .username("")
                    .password("Password123!")
                    .build();

            performPostNoAuth("/api/auth/login", request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when password is blank")
        void login_BlankPassword_Returns400() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .username("someuser")
                    .password("")
                    .build();

            performPostNoAuth("/api/auth/login", request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when both fields are blank")
        void login_EmptyRequest_Returns400() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .username("")
                    .password("")
                    .build();

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
            UserModifyRequest request = UserModifyRequest.builder()
                    .email("not-valid-email")
                    .build();

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
            UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                    .currentPassword("")
                    .newPassword("NewPassword123!")
                    .build();

            performPostWithAuth("/api/users/me/change-password", request, userToken)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when new password is too short")
        void changePassword_ShortNewPassword_Returns400() throws Exception {
            UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                    .currentPassword("Password123!")
                    .newPassword("123")
                    .build();

            performPostWithAuth("/api/users/me/change-password", request, userToken)
                    .andExpect(status().isBadRequest());
        }
    }


    @Nested
    @DisplayName("Pageable limits")
    class PageableLimitTests {

        @Test
        @DisplayName("caps the page size instead of materializing the whole table")
        void search_HugePageSize_IsCappedAtMaximum() throws Exception {
            performGetNoAuth("/api/cars/search?page=0&size=1000000")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageable.pageSize").value(100));
        }
    }


    @Nested
    @DisplayName("Error Response Format")
    class ErrorResponseFormatTests {

        @Test
        @DisplayName("error response contains required fields")
        void validationError_ContainsRequiredFields() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("")
                    .email("")
                    .password("")
                    .build();

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
            RegisterRequest request = RegisterRequest.builder()
                    .username("")
                    .email("invalid")
                    .password("123")
                    .build();

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
                    .engineRating(4)
                    .transmissionRating(4)
                    .steeringRating(4)
                    .suspensionRating(4)
                    .visibilityRating(4)
                    .ergonomicsRating(4)
                    .soundProofingRating(4)
                    .interiorSpaceRating(4)
                    .maintenanceRating(4)
                    .priceQualityRating(4)
                    .failureFreeRating(4)
                    .build();
        }

        @Test
        @DisplayName("returns 400 when comment is blank")
        void createReview_BlankComment_Returns400() throws Exception {
            CreateReviewRequest request = validReviewRequest();
            request.setComment("");

            performPostWithAuth("/api/reviews/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.comment").exists());
        }

        @Test
        @DisplayName("returns 400 when comment is too short")
        void createReview_ShortComment_Returns400() throws Exception {
            CreateReviewRequest request = validReviewRequest();
            request.setComment("Short");

            performPostWithAuth("/api/reviews/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.comment").exists());
        }

        @Test
        @DisplayName("returns 400 when engine rating is null")
        void createReview_NullEngineRating_Returns400() throws Exception {
            CreateReviewRequest request = validReviewRequest();
            request.setEngineRating(null);

            performPostWithAuth("/api/reviews/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.engineRating").exists());
        }

        @Test
        @DisplayName("returns 400 when engine rating is below minimum")
        void createReview_EngineRatingBelowMin_Returns400() throws Exception {
            CreateReviewRequest request = validReviewRequest();
            request.setEngineRating(0);

            performPostWithAuth("/api/reviews/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.engineRating").exists());
        }

        @Test
        @DisplayName("returns 400 when engine rating exceeds maximum")
        void createReview_EngineRatingAboveMax_Returns400() throws Exception {
            CreateReviewRequest request = validReviewRequest();
            request.setEngineRating(6);

            performPostWithAuth("/api/reviews/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.engineRating").exists());
        }

        @Test
        @DisplayName("returns 400 when transmission rating is null")
        void createReview_NullTransmissionRating_Returns400() throws Exception {
            CreateReviewRequest request = validReviewRequest();
            request.setTransmissionRating(null);

            performPostWithAuth("/api/reviews/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.transmissionRating").exists());
        }

        @Test
        @DisplayName("returns 400 when multiple ratings are invalid")
        void createReview_MultipleInvalidRatings_Returns400() throws Exception {
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Valid comment with enough characters")
                    .engineRating(null)
                    .transmissionRating(0)
                    .steeringRating(6)
                    .suspensionRating(4)
                    .visibilityRating(4)
                    .ergonomicsRating(4)
                    .soundProofingRating(4)
                    .interiorSpaceRating(4)
                    .maintenanceRating(4)
                    .priceQualityRating(4)
                    .failureFreeRating(4)
                    .build();

            performPostWithAuth("/api/reviews/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", aMapWithSize(greaterThanOrEqualTo(3))));
        }

        @Test
        @DisplayName("returns 400 when all ratings are null")
        void createReview_AllRatingsNull_Returns400() throws Exception {
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Valid comment with enough characters")
                    .build();

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
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(null)
                    .comment("Valid comment")
                    .build();

            performPostWithAuth("/api/fuel-reports/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.fuelConsumption").exists());
        }

        @Test
        @DisplayName("returns 400 when fuel consumption is negative")
        void createFuelReport_NegativeFuelConsumption_Returns400() throws Exception {
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("-5.5"))
                    .comment("Valid comment")
                    .build();

            performPostWithAuth("/api/fuel-reports/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.fuelConsumption").exists());
        }

        @Test
        @DisplayName("returns 400 when fuel consumption is zero")
        void createFuelReport_ZeroFuelConsumption_Returns400() throws Exception {
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(BigDecimal.ZERO)
                    .comment("Valid comment")
                    .build();

            performPostWithAuth("/api/fuel-reports/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.fuelConsumption").exists());
        }

        @Test
        @DisplayName("returns 400 when fuel consumption has invalid format")
        void createFuelReport_InvalidDigitsFormat_Returns400() throws Exception {
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("123.5"))
                    .comment("Valid comment")
                    .build();

            performPostWithAuth("/api/fuel-reports/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.fuelConsumption").exists());
        }

        @Test
        @DisplayName("returns 400 when comment exceeds maximum length")
        void createFuelReport_CommentTooLong_Returns400() throws Exception {
            String longComment = "x".repeat(1001);
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("7.5"))
                    .comment(longComment)
                    .build();

            performPostWithAuth("/api/fuel-reports/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.comment").exists());
        }

        @Test
        @DisplayName("accepts valid fuel report request")
        void createFuelReport_ValidRequest_Returns201() throws Exception {
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("7.5"))
                    .comment("Normal driving conditions")
                    .build();

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
            CreateDataProposalRequest request = new CreateDataProposalRequest();
            request.setCategory(null);
            request.setProposedValues(Map.of("maxPower", 220));

            performPostWithAuth("/api/data-proposals/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.category").exists());
        }

        @Test
        @DisplayName("returns 400 when proposed values are empty")
        void createDataProposal_EmptyProposedValues_Returns400() throws Exception {
            CreateDataProposalRequest request = new CreateDataProposalRequest();
            request.setCategory(ProposalCategory.ENGINE);
            request.setProposedValues(Map.of());

            performPostWithAuth("/api/data-proposals/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.proposedValues").exists());
        }

        @Test
        @DisplayName("returns 400 when proposed values are null")
        void createDataProposal_NullProposedValues_Returns400() throws Exception {
            CreateDataProposalRequest request = new CreateDataProposalRequest();
            request.setCategory(ProposalCategory.ENGINE);
            request.setProposedValues(null);

            performPostWithAuth("/api/data-proposals/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.proposedValues").exists());
        }

        @Test
        @DisplayName("returns 400 when the proposal touches a field outside its category")
        void createDataProposal_NonEditableFieldOnly_Returns400() throws Exception {
            CreateDataProposalRequest request = new CreateDataProposalRequest();
            request.setCategory(ProposalCategory.ENGINE);
            request.setProposedValues(Map.of("id", UUID.randomUUID().toString()));

            performPostWithAuth("/api/data-proposals/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("accepts valid data proposal request")
        void createDataProposal_ValidRequest_Returns201() throws Exception {
            CreateDataProposalRequest request = new CreateDataProposalRequest();
            request.setCategory(ProposalCategory.ENGINE);
            request.setComment("Correction of engine power");
            request.setProposedValues(Map.of("maxPower", 220));

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
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("")
                    .build();

            performPutWithAuth("/api/user-settings", request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.theme").exists());
        }

        @Test
        @DisplayName("omitting a field leaves its current value untouched")
        void updateSettings_NullTheme_LeavesThemeUnchanged() throws Exception {
            performPutWithAuth("/api/user-settings",
                    UpdateUserSettingsRequest.builder().theme("dark").build(), userToken)
                    .andExpect(status().isOk());

            performPutWithAuth("/api/user-settings",
                    UpdateUserSettingsRequest.builder().language("pl").build(), userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.language").value("pl"))
                    .andExpect(jsonPath("$.theme").value("dark"));
        }

        @Test
        @DisplayName("returns 400 when theme exceeds maximum length")
        void updateSettings_ThemeTooLong_Returns400() throws Exception {
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("thisthemeiswaytoolongtobevalid")
                    .build();

            performPutWithAuth("/api/user-settings", request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.theme").exists());
        }

        @Test
        @DisplayName("returns 400 when theme is not light or dark")
        void updateSettings_InvalidThemeValue_Returns400() throws Exception {
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("blue")
                    .build();

            performPutWithAuth("/api/user-settings", request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.theme").exists());
        }

        @Test
        @DisplayName("accepts light theme")
        void updateSettings_LightTheme_Returns200() throws Exception {
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("light")
                    .build();

            performPutWithAuth("/api/user-settings", request, userToken)
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("accepts dark theme")
        void updateSettings_DarkTheme_Returns200() throws Exception {
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("dark")
                    .build();

            performPutWithAuth("/api/user-settings", request, userToken)
                    .andExpect(status().isOk());
        }
    }
}
