package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.cars.CarRepository;
import com.carsplatform.backend.api.dataProposal.dtos.CreateDataProposalRequest;
import com.carsplatform.backend.api.dataProposal.dtos.GetDataProposalsResponse;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.common.TestDataFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("DataProposalService Tests")
class DataProposalServiceTest {

    @Mock
    private DataProposalRepository dataProposalRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private GetDataProposalsMapper dataProposalsMapper;

    @InjectMocks
    private DataProposalService dataProposalService;

    private User testUser;
    private Car testCar;
    private DataProposal testProposal;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = TestDataFactory.defaultUser()
                .id(1L)
                .build();

        // Create test brand
        Brand brand = TestDataFactory.defaultBrand()
                .id(1)
                .build();

        // Create test model
        Model model = TestDataFactory.defaultModel(brand)
                .id(1)
                .build();

        // Create test generation
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(1)
                .build();

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(1)
                .build();

        // Create test car
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(1)
                .build();

        // Create test proposal
        testProposal = new DataProposal();

        testProposal.setId(1L);
        testProposal.setUser(testUser);
        testProposal.setCar(testCar);
        testProposal.setCategory("ENGINE");
        testProposal.setComment("Update max power");
        testProposal.setProposedValues(Map.of("maxPower", 220));
        testProposal.setStatus(DataProposalStatus.PENDING);
    }


    @Nested
    @DisplayName("createDataProposal")
    class CreateDataProposalTests {

        @Test
        @DisplayName("should create data proposal when car and user exist")
        void createDataProposal_ValidData_CreatesProposal() {

            // Create request with valid proposal data
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory("ENGINE");
            request.setComment("Update max power");
            request.setProposedValues(Map.of("maxPower", 220));

            // Mock repositories to return existing car and user
            when(carRepository.findById(1)).thenReturn(Optional.of(testCar));
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // Send create proposal request
            dataProposalService.createDataProposal(1, "testuser", request);

            // Verify results -> proposal is saved with correct data
            ArgumentCaptor<DataProposal> proposalCaptor = ArgumentCaptor.forClass(DataProposal.class);

            verify(dataProposalRepository).save(proposalCaptor.capture());

            DataProposal savedProposal = proposalCaptor.getValue();

            assertThat(savedProposal.getCar()).isEqualTo(testCar);
            assertThat(savedProposal.getUser()).isEqualTo(testUser);
            assertThat(savedProposal.getCategory()).isEqualTo("ENGINE");
            assertThat(savedProposal.getStatus()).isEqualTo(DataProposalStatus.PENDING);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when car not found")
        void createDataProposal_CarNotFound_ThrowsException() {

            // Create request with valid proposal data
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory("ENGINE");
            request.setProposedValues(Map.of("maxPower", 220));

            // Mock repositories to return non-existent car
            when(carRepository.findById(999)).thenReturn(Optional.empty());

            // Send create proposal request and verify results -> EntityNotFoundException is thrown
            assertThatThrownBy(() -> dataProposalService.createDataProposal(999, "testuser", request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Car not found");
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when user not found")
        void createDataProposal_UserNotFound_ThrowsException() {

            // Create request with valid proposal data
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory("ENGINE");
            request.setProposedValues(Map.of("maxPower", 220));

            // Mock repositories to return existing car and non-existent user
            when(carRepository.findById(1)).thenReturn(Optional.of(testCar));
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            // Send create proposal request and verify results -> EntityNotFoundException is thrown
            assertThatThrownBy(() -> dataProposalService.createDataProposal(1, "unknown", request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User not found");

            verify(dataProposalRepository, never()).save(any());
        }
    }


    @Nested
    @DisplayName("getPendingDataProposals")
    class GetPendingDataProposalsTests {

        @Test
        @DisplayName("should return pending proposals")
        void getPendingDataProposals_ReturnsProposals() {

            // Create pageable request
            Pageable pageable = PageRequest.of(0, 10);

            Page<DataProposal> proposalPage = new PageImpl<>(List.of(testProposal), pageable, 1);

            // Mock repositories to return pending proposals
            when(dataProposalRepository.findByStatus(DataProposalStatus.PENDING, pageable))
                    .thenReturn(proposalPage);

            // Send get pending proposals request
            Page<DataProposal> result = dataProposalService.getPendingDataProposals(pageable);

            // Verify results -> pending proposals are returned
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getStatus()).isEqualTo(DataProposalStatus.PENDING);

            verify(dataProposalRepository).findByStatus(DataProposalStatus.PENDING, pageable);
        }
    }


    @Nested
    @DisplayName("resolveDataProposal")
    class ResolveDataProposalTests {

        @Test
        @DisplayName("should reject proposal when approve is false")
        void resolveDataProposal_Reject_SetsRejectedStatus() {

            // Mock repositories to return existing proposal
            when(dataProposalRepository.findById(1L)).thenReturn(Optional.of(testProposal));

            // Send resolve proposal request
            dataProposalService.resolveDataProposal(1L, false, "Not valid data");

            // Verify results -> proposal is updated with rejected status and admin comment
            assertThat(testProposal.getStatus()).isEqualTo(DataProposalStatus.REJECTED);
            assertThat(testProposal.getAdminComment()).isEqualTo("Not valid data");
            assertThat(testProposal.getResolvedAt()).isNotNull();

            verify(dataProposalRepository).save(testProposal);
        }

        @Test
        @DisplayName("should approve proposal and apply changes when approve is true")
        void resolveDataProposal_Approve_SetsApprovedStatusAndAppliesChanges() throws Exception {

            // Prepare test data with ENGINE category and proposed changes
            testProposal.setCategory("ENGINE");
            testProposal.setProposedValues(Map.of("maxPower", 250));

            // Mock repositories to return existing proposal
            when(dataProposalRepository.findById(1L)).thenReturn(Optional.of(testProposal));

            // Mock ObjectMapper behavior
            JsonNode engineNode = mock(ObjectNode.class);
            JsonNode changesNode = mock(ObjectNode.class);

            when(objectMapper.valueToTree(testCar.getEngine())).thenReturn(engineNode);
            when(objectMapper.valueToTree(testProposal.getProposedValues())).thenReturn(changesNode);
            when(objectMapper.readerForUpdating(testCar.getEngine())).thenReturn(mock(ObjectReader.class));

            // Send resolve proposal request with approve=true
            dataProposalService.resolveDataProposal(1L, true, "Changes approved");

            // Verify results -> proposal is approved and changes applied
            assertThat(testProposal.getStatus()).isEqualTo(DataProposalStatus.APPROVED);
            assertThat(testProposal.getAdminComment()).isEqualTo("Changes approved");
            assertThat(testProposal.getResolvedAt()).isNotNull();

            verify(dataProposalRepository).save(testProposal);
            verify(carRepository).save(testCar);
        }

        @Test
        @DisplayName("should throw IllegalStateException when approving ENGINE proposal but car has no engine")
        void resolveDataProposal_ApproveEngineNoEngine_ThrowsException() {

            // Set up proposal with ENGINE category
            testProposal.setCategory("ENGINE");

            // Remove engine from car
            testCar.setEngine(null);

            // Mock repositories to return existing proposal
            when(dataProposalRepository.findById(1L)).thenReturn(Optional.of(testProposal));

            // Verify IllegalStateException is thrown
            assertThatThrownBy(() -> dataProposalService.resolveDataProposal(1L, true, "OK"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Car has no engine defined");
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for unknown category")
        void resolveDataProposal_UnknownCategory_ThrowsException() {

            // Set up proposal with unknown category
            testProposal.setCategory("UNKNOWN_CATEGORY");

            // Mock repositories to return existing proposal
            when(dataProposalRepository.findById(1L)).thenReturn(Optional.of(testProposal));

            // Verify IllegalArgumentException is thrown
            assertThatThrownBy(() -> dataProposalService.resolveDataProposal(1L, true, "OK"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unknown category");
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when proposal not found")
        void resolveDataProposal_ProposalNotFound_ThrowsException() {

            // Mock repositories to return non-existent proposal
            when(dataProposalRepository.findById(999L)).thenReturn(Optional.empty());

            // Send resolve proposal request and verify results -> EntityNotFoundException is thrown
            assertThatThrownBy(() -> dataProposalService.resolveDataProposal(999L, true, "OK"))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("should throw IllegalStateException when proposal already resolved")
        void resolveDataProposal_AlreadyResolved_ThrowsException() {

            // Mock repositories to return already resolved proposal
            testProposal.setStatus(DataProposalStatus.APPROVED);

            when(dataProposalRepository.findById(1L)).thenReturn(Optional.of(testProposal));

            // Send resolve proposal request and verify results -> IllegalStateException is thrown
            assertThatThrownBy(() -> dataProposalService.resolveDataProposal(1L, true, "OK"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already resolved");

            verify(dataProposalRepository, never()).save(any());
        }

        @Test
        @DisplayName("should handle CHASSIS category when approving")
        void resolveDataProposal_ApproveChassisCategory_AppliesChanges() throws Exception {

            // Prepare test data with CHASSIS category
            testProposal.setCategory("CHASSIS");
            testProposal.setProposedValues(Map.of("frontBrakesRadius", 340));

            // Mock repositories to return existing proposal
            when(dataProposalRepository.findById(1L)).thenReturn(Optional.of(testProposal));

            // Mock ObjectMapper behavior
            JsonNode chassisNode = mock(ObjectNode.class);
            JsonNode changesNode = mock(ObjectNode.class);

            when(objectMapper.valueToTree(testCar.getChassis())).thenReturn(chassisNode);
            when(objectMapper.valueToTree(testProposal.getProposedValues())).thenReturn(changesNode);
            when(objectMapper.readerForUpdating(testCar.getChassis())).thenReturn(mock(ObjectReader.class));

            // Send resolve proposal request
            dataProposalService.resolveDataProposal(1L, true, "Chassis changes approved");

            // Verify results
            assertThat(testProposal.getStatus()).isEqualTo(DataProposalStatus.APPROVED);
            verify(carRepository).save(testCar);
        }

        @Test
        @DisplayName("should handle TRANSMISSION category when approving")
        void resolveDataProposal_ApproveTransmissionCategory_AppliesChanges() throws Exception {

            // Prepare test data with TRANSMISSION category
            testProposal.setCategory("TRANSMISSION");
            testProposal.setProposedValues(Map.of("gearsNumber", 8));

            // Mock repositories to return existing proposal
            when(dataProposalRepository.findById(1L)).thenReturn(Optional.of(testProposal));

            // Mock ObjectMapper behavior
            JsonNode transmissionNode = mock(ObjectNode.class);
            JsonNode changesNode = mock(ObjectNode.class);

            when(objectMapper.valueToTree(testCar.getTransmission())).thenReturn(transmissionNode);
            when(objectMapper.valueToTree(testProposal.getProposedValues())).thenReturn(changesNode);
            when(objectMapper.readerForUpdating(testCar.getTransmission())).thenReturn(mock(ObjectReader.class));

            // Send resolve proposal request
            dataProposalService.resolveDataProposal(1L, true, "Transmission changes approved");

            // Verify results
            assertThat(testProposal.getStatus()).isEqualTo(DataProposalStatus.APPROVED);
            verify(carRepository).save(testCar);
        }
    }


    @Nested
    @DisplayName("getUserDataProposals")
    class GetUserDataProposalsTests {

        @Test
        @DisplayName("should return user proposals")
        void getUserDataProposals_ReturnsProposals() {

            // Create pageable request
            Pageable pageable = PageRequest.of(0, 10);

            Page<DataProposal> proposalPage = new PageImpl<>(List.of(testProposal), pageable, 1);

            GetDataProposalsResponse response = new GetDataProposalsResponse();
            response.setId(1L);

            // Mock repositories to return existing user and proposals
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(dataProposalRepository.findByUser(testUser, pageable)).thenReturn(proposalPage);
            when(dataProposalsMapper.toDto(testProposal)).thenReturn(response);

            // Send get user data proposals request
            Page<GetDataProposalsResponse> result = dataProposalService.getUserDataProposals("testuser", pageable);

            // Verify results -> proposals are returned
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            verify(userRepository).findByUsername("testuser");
            verify(dataProposalRepository).findByUser(testUser, pageable);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when user not found")
        void getUserDataProposals_UserNotFound_ThrowsException() {

            // Mock repositories to return non-existent user
            Pageable pageable = PageRequest.of(0, 10);

            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            // Send get user data proposals request and verify results -> EntityNotFoundException is thrown
            assertThatThrownBy(() -> dataProposalService.getUserDataProposals("unknown", pageable))
                    .isInstanceOf(EntityNotFoundException.class);

            verify(dataProposalRepository, never()).findByUser(any(), any());
        }
    }
}
