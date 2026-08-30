package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.cars.CarRepository;
import com.carsplatform.backend.api.dataProposal.dtos.CreateDataProposalRequest;
import com.carsplatform.backend.api.dataProposal.dtos.GetDataProposalsResponse;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.tags.Tag;
import com.carsplatform.backend.api.tags.TagRepository;
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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
    private TagRepository tagRepository;

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
                .id(UUID.randomUUID())
                .build();

        // Create test brand
        Brand brand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .build();

        // Create test model
        Model model = TestDataFactory.defaultModel(brand)
                .id(UUID.randomUUID())
                .build();

        // Create test generation
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(UUID.randomUUID())
                .build();

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .build();

        // Create test car
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(UUID.randomUUID())
                .build();

        // Create test proposal
        testProposal = new DataProposal();

        testProposal.setId(UUID.randomUUID());
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
            when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // Send create proposal request
            dataProposalService.createDataProposal(testCar.getId(), "testuser", request);

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
        @DisplayName("should drop fields that do not belong to the category")
        void createDataProposal_UnknownFields_AreFilteredOut() {

            // Create request with the entity id and a field of another category
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory("ENGINE");
            request.setProposedValues(new LinkedHashMap<>(Map.of(
                    "maxPower", 220,
                    "id", UUID.randomUUID().toString(),
                    "gearsNumber", 8
            )));

            // Mock repositories to return existing car and user
            when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // Send create proposal request
            dataProposalService.createDataProposal(testCar.getId(), "testuser", request);

            // Verify results -> only the editable engine field is stored
            ArgumentCaptor<DataProposal> proposalCaptor = ArgumentCaptor.forClass(DataProposal.class);

            verify(dataProposalRepository).save(proposalCaptor.capture());

            assertThat(proposalCaptor.getValue().getProposedValues())
                    .containsOnlyKeys("maxPower");
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when no editable field is left")
        void createDataProposal_OnlyUnknownFields_ThrowsException() {

            // Create request containing nothing but non-editable fields
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory("ENGINE");
            request.setProposedValues(Map.of("id", UUID.randomUUID().toString()));

            // Mock repositories to return existing car and user
            when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // Send create proposal request and verify results -> IllegalArgumentException is thrown
            assertThatThrownBy(() -> dataProposalService.createDataProposal(testCar.getId(), "testuser", request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No editable fields");

            verify(dataProposalRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for an unknown category")
        void createDataProposal_UnknownCategory_ThrowsException() {

            // Create request with a category that does not exist
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory("USERS");
            request.setProposedValues(Map.of("isAdmin", true));

            // Mock repositories to return existing car and user
            when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // Send create proposal request and verify results -> IllegalArgumentException is thrown
            assertThatThrownBy(() -> dataProposalService.createDataProposal(testCar.getId(), "testuser", request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unknown category");

            verify(dataProposalRepository, never()).save(any());
        }

        @Test
        @DisplayName("should store the category in upper case")
        void createDataProposal_LowerCaseCategory_IsNormalized() {

            // Create request with a lower case category
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory("engine");
            request.setProposedValues(Map.of("maxPower", 220));

            // Mock repositories to return existing car and user
            when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // Send create proposal request
            dataProposalService.createDataProposal(testCar.getId(), "testuser", request);

            // Verify results -> category is stored normalized
            ArgumentCaptor<DataProposal> proposalCaptor = ArgumentCaptor.forClass(DataProposal.class);

            verify(dataProposalRepository).save(proposalCaptor.capture());

            assertThat(proposalCaptor.getValue().getCategory()).isEqualTo("ENGINE");
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when car not found")
        void createDataProposal_CarNotFound_ThrowsException() {

            // Create request with valid proposal data
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory("ENGINE");
            request.setProposedValues(Map.of("maxPower", 220));

            // Mock repositories to return non-existent car
            UUID nonExistentCarId = UUID.randomUUID();

            when(carRepository.findById(nonExistentCarId)).thenReturn(Optional.empty());

            // Send create proposal request and verify results -> EntityNotFoundException is thrown
            assertThatThrownBy(() -> dataProposalService.createDataProposal(nonExistentCarId, "testuser", request))
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
            when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            // Send create proposal request and verify results -> EntityNotFoundException is thrown
            assertThatThrownBy(() -> dataProposalService.createDataProposal(testCar.getId(), "unknown", request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User not found");

            verify(dataProposalRepository, never()).save(any());
        }

        @Test
        @DisplayName("should create data proposal with TAGS category")
        void createDataProposal_TagsCategory_CreatesProposal() {

            // Create request with TAGS category
            UUID tag1Id = UUID.randomUUID();
            UUID tag2Id = UUID.randomUUID();

            CreateDataProposalRequest request = new CreateDataProposalRequest();
            request.setCategory("TAGS");
            request.setComment("Add and remove tags");
            request.setProposedValues(Map.of(
                    "addTagIds", List.of(tag1Id.toString(), tag2Id.toString()),
                    "removeTagIds", List.of(UUID.randomUUID().toString())
            ));

            // Mock repositories to return existing car and user
            when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // Send create proposal request
            dataProposalService.createDataProposal(testCar.getId(), "testuser", request);

            // Verify results -> proposal is saved with correct data
            ArgumentCaptor<DataProposal> proposalCaptor = ArgumentCaptor.forClass(DataProposal.class);
            verify(dataProposalRepository).save(proposalCaptor.capture());

            DataProposal savedProposal = proposalCaptor.getValue();
            assertThat(savedProposal.getCategory()).isEqualTo("TAGS");
            assertThat(savedProposal.getProposedValues()).containsKeys("addTagIds", "removeTagIds");
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

            // Create expected response DTO
            GetDataProposalsResponse responseDto = GetDataProposalsResponse.builder()
                    .id(testProposal.getId())
                    .status(DataProposalStatus.PENDING)
                    .build();

            // Mock repositories to return pending proposals
            when(dataProposalRepository.findByStatus(DataProposalStatus.PENDING, pageable))
                    .thenReturn(proposalPage);
            when(dataProposalsMapper.toDto(testProposal)).thenReturn(responseDto);

            // Send get pending proposals request
            Page<GetDataProposalsResponse> result = dataProposalService.getPendingDataProposals(pageable);

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
            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            // Send resolve proposal request
            dataProposalService.resolveDataProposal(testProposal.getId(), false, "Not valid data");

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
            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            // Mock ObjectMapper behavior
            JsonNode engineNode = mock(ObjectNode.class);
            JsonNode changesNode = mock(ObjectNode.class);

            when(objectMapper.valueToTree(testCar.getEngine())).thenReturn(engineNode);
            when(objectMapper.valueToTree(testProposal.getProposedValues())).thenReturn(changesNode);
            when(objectMapper.readerForUpdating(testCar.getEngine())).thenReturn(mock(ObjectReader.class));

            // Send resolve proposal request with approve=true
            dataProposalService.resolveDataProposal(testProposal.getId(), true, "Changes approved");

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
            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            // Verify IllegalStateException is thrown
            assertThatThrownBy(() -> dataProposalService.resolveDataProposal(testProposal.getId(), true, "OK"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Car has no engine defined");
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for unknown category")
        void resolveDataProposal_UnknownCategory_ThrowsException() {

            // Set up proposal with unknown category
            testProposal.setCategory("UNKNOWN_CATEGORY");

            // Mock repositories to return existing proposal
            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            // Verify IllegalArgumentException is thrown
            assertThatThrownBy(() -> dataProposalService.resolveDataProposal(testProposal.getId(), true, "OK"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unknown category");
        }

        @Test
        @DisplayName("should ignore non-editable fields of a proposal stored before the whitelist")
        void resolveDataProposal_ApproveLegacyProposalWithForbiddenField_AppliesOnlyAllowedFields() throws Exception {

            // Prepare a proposal that also carries the entity id
            testProposal.setCategory("ENGINE");
            testProposal.setProposedValues(new LinkedHashMap<>(Map.of(
                    "maxPower", 250,
                    "id", UUID.randomUUID().toString()
            )));

            // Mock repositories to return existing proposal
            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            // Mock ObjectMapper behavior
            when(objectMapper.valueToTree(testCar.getEngine())).thenReturn(mock(ObjectNode.class));
            when(objectMapper.valueToTree(Map.of("maxPower", 250))).thenReturn(mock(ObjectNode.class));
            when(objectMapper.readerForUpdating(testCar.getEngine())).thenReturn(mock(ObjectReader.class));

            // Send approval request
            dataProposalService.resolveDataProposal(testProposal.getId(), true, "Approved");

            // Verify results -> only the whitelisted field was handed over to the mapper
            assertThat(testProposal.getStatus()).isEqualTo(DataProposalStatus.APPROVED);

            verify(objectMapper).valueToTree(Map.of("maxPower", 250));
            verify(objectMapper, never()).valueToTree(testProposal.getProposedValues());
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when proposal not found")
        void resolveDataProposal_ProposalNotFound_ThrowsException() {

            // Mock repositories to return non-existent proposal
            UUID nonExistentProposalId = UUID.randomUUID();

            when(dataProposalRepository.findById(nonExistentProposalId)).thenReturn(Optional.empty());

            // Send resolve proposal request and verify results -> EntityNotFoundException is thrown
            assertThatThrownBy(() -> dataProposalService.resolveDataProposal(nonExistentProposalId, true, "OK"))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("should throw IllegalStateException when proposal already resolved")
        void resolveDataProposal_AlreadyResolved_ThrowsException() {

            // Mock repositories to return already resolved proposal
            testProposal.setStatus(DataProposalStatus.APPROVED);

            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            // Send resolve proposal request and verify results -> IllegalStateException is thrown
            assertThatThrownBy(() -> dataProposalService.resolveDataProposal(testProposal.getId(), true, "OK"))
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
            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            // Mock ObjectMapper behavior
            JsonNode chassisNode = mock(ObjectNode.class);
            JsonNode changesNode = mock(ObjectNode.class);

            when(objectMapper.valueToTree(testCar.getChassis())).thenReturn(chassisNode);
            when(objectMapper.valueToTree(testProposal.getProposedValues())).thenReturn(changesNode);
            when(objectMapper.readerForUpdating(testCar.getChassis())).thenReturn(mock(ObjectReader.class));

            // Send resolve proposal request
            dataProposalService.resolveDataProposal(testProposal.getId(), true, "Chassis changes approved");

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
            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            // Mock ObjectMapper behavior
            JsonNode transmissionNode = mock(ObjectNode.class);
            JsonNode changesNode = mock(ObjectNode.class);

            when(objectMapper.valueToTree(testCar.getTransmission())).thenReturn(transmissionNode);
            when(objectMapper.valueToTree(testProposal.getProposedValues())).thenReturn(changesNode);
            when(objectMapper.readerForUpdating(testCar.getTransmission())).thenReturn(mock(ObjectReader.class));

            // Send resolve proposal request
            dataProposalService.resolveDataProposal(testProposal.getId(), true, "Transmission changes approved");

            // Verify results
            assertThat(testProposal.getStatus()).isEqualTo(DataProposalStatus.APPROVED);
            verify(carRepository).save(testCar);
        }

        @Test
        @DisplayName("should handle TAGS category when approving - add tags")
        void resolveDataProposal_ApproveTagsCategory_AddsTags() {

            // Create tags to add
            UUID tag1Id = UUID.randomUUID();
            UUID tag2Id = UUID.randomUUID();

            Tag tag1 = Tag.builder().id(tag1Id).name("Sportowy").build();
            Tag tag2 = Tag.builder().id(tag2Id).name("Ekonomiczny").build();

            // Initialize car tags as mutable set
            testCar.setTags(new HashSet<>());

            // Prepare test data with TAGS category
            testProposal.setCategory("TAGS");
            testProposal.setProposedValues(Map.of("addTagIds", List.of(tag1Id.toString(), tag2Id.toString())));

            // Mock repositories
            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));
            when(tagRepository.findAllById(anyList())).thenReturn(List.of(tag1, tag2));

            // Send resolve proposal request
            dataProposalService.resolveDataProposal(testProposal.getId(), true, "Tags added");

            // Verify results
            assertThat(testProposal.getStatus()).isEqualTo(DataProposalStatus.APPROVED);
            assertThat(testCar.getTags()).containsExactlyInAnyOrder(tag1, tag2);
            verify(carRepository).save(testCar);
        }

        @Test
        @DisplayName("should handle TAGS category when approving - remove tags")
        void resolveDataProposal_ApproveTagsCategory_RemovesTags() {

            // Create existing tags
            UUID tag1Id = UUID.randomUUID();
            UUID tag2Id = UUID.randomUUID();
            UUID tag3Id = UUID.randomUUID();

            Tag tag1 = Tag.builder().id(tag1Id).name("Sportowy").build();
            Tag tag2 = Tag.builder().id(tag2Id).name("Ekonomiczny").build();
            Tag tag3 = Tag.builder().id(tag3Id).name("Rodzinny").build();

            // Initialize car with existing tags
            Set<Tag> existingTags = new HashSet<>();
            existingTags.add(tag1);
            existingTags.add(tag2);
            existingTags.add(tag3);
            testCar.setTags(existingTags);

            // Prepare test data with TAGS category - remove tag1 and tag2
            testProposal.setCategory("TAGS");
            testProposal.setProposedValues(Map.of("removeTagIds", List.of(tag1Id.toString(), tag2Id.toString())));

            // Mock repositories
            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            // Send resolve proposal request
            dataProposalService.resolveDataProposal(testProposal.getId(), true, "Tags removed");

            // Verify results
            assertThat(testProposal.getStatus()).isEqualTo(DataProposalStatus.APPROVED);
            assertThat(testCar.getTags()).containsExactly(tag3);
            verify(carRepository).save(testCar);
        }

        @Test
        @DisplayName("should handle TAGS category when approving - add and remove tags")
        void resolveDataProposal_ApproveTagsCategory_AddAndRemoveTags() {

            // Create existing tag
            UUID existingTagId = UUID.randomUUID();
            Tag existingTag = Tag.builder().id(existingTagId).name("Sportowy").build();

            // Create new tag to add
            UUID newTagId = UUID.randomUUID();
            Tag newTag = Tag.builder().id(newTagId).name("Ekonomiczny").build();

            // Initialize car with existing tag
            Set<Tag> existingTags = new HashSet<>();
            existingTags.add(existingTag);
            testCar.setTags(existingTags);

            // Prepare test data - add newTag, remove existingTag
            testProposal.setCategory("TAGS");
            testProposal.setProposedValues(Map.of(
                    "addTagIds", List.of(newTagId.toString()),
                    "removeTagIds", List.of(existingTagId.toString())
            ));

            // Mock repositories
            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));
            when(tagRepository.findAllById(anyList())).thenReturn(List.of(newTag));

            // Send resolve proposal request
            dataProposalService.resolveDataProposal(testProposal.getId(), true, "Tags modified");

            // Verify results
            assertThat(testProposal.getStatus()).isEqualTo(DataProposalStatus.APPROVED);
            assertThat(testCar.getTags()).containsExactly(newTag);
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
            response.setId(UUID.randomUUID());

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
