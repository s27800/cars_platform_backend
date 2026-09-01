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
import com.carsplatform.backend.common.ModerationStatus;
import com.carsplatform.backend.common.ProposalCategory;
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

import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

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
        testUser = TestDataFactory.defaultUser()
                .id(UUID.randomUUID())
                .build();
        Brand brand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .build();
        Model model = TestDataFactory.defaultModel(brand)
                .id(UUID.randomUUID())
                .build();
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(UUID.randomUUID())
                .build();
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .build();
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(UUID.randomUUID())
                .build();
        testProposal = new DataProposal();

        testProposal.setId(UUID.randomUUID());
        testProposal.setUser(testUser);
        testProposal.setCar(testCar);
        testProposal.setCategory(ProposalCategory.ENGINE);
        testProposal.setComment("Update max power");
        testProposal.setProposedValues(Map.of("maxPower", 220));
        testProposal.setStatus(ModerationStatus.PENDING);
    }


    @Nested
    @DisplayName("createDataProposal")
    class CreateDataProposalTests {

        @Test
        @DisplayName("should create data proposal when car and user exist")
        void createDataProposal_ValidData_CreatesProposal() {
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory(ProposalCategory.ENGINE);
            request.setComment("Update max power");
            request.setProposedValues(Map.of("maxPower", 220));

            when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            dataProposalService.createDataProposal(testCar.getId(), "testuser", request);
            ArgumentCaptor<DataProposal> proposalCaptor = ArgumentCaptor.forClass(DataProposal.class);

            verify(dataProposalRepository).save(proposalCaptor.capture());

            DataProposal savedProposal = proposalCaptor.getValue();

            assertThat(savedProposal.getCar()).isEqualTo(testCar);
            assertThat(savedProposal.getUser()).isEqualTo(testUser);
            assertThat(savedProposal.getCategory()).isEqualTo(ProposalCategory.ENGINE);
            assertThat(savedProposal.getStatus()).isEqualTo(ModerationStatus.PENDING);
        }

        @Test
        @DisplayName("should drop fields that do not belong to the category")
        void createDataProposal_UnknownFields_AreFilteredOut() {
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory(ProposalCategory.ENGINE);
            request.setProposedValues(new LinkedHashMap<>(Map.of(
                    "maxPower", 220,
                    "id", UUID.randomUUID().toString(),
                    "gearsNumber", 8
            )));

            when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            dataProposalService.createDataProposal(testCar.getId(), "testuser", request);
            ArgumentCaptor<DataProposal> proposalCaptor = ArgumentCaptor.forClass(DataProposal.class);

            verify(dataProposalRepository).save(proposalCaptor.capture());

            assertThat(proposalCaptor.getValue().getProposedValues())
                    .containsOnlyKeys("maxPower");
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when no editable field is left")
        void createDataProposal_OnlyUnknownFields_ThrowsException() {
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory(ProposalCategory.ENGINE);
            request.setProposedValues(Map.of("id", UUID.randomUUID().toString()));

            when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            assertThatThrownBy(() -> dataProposalService.createDataProposal(testCar.getId(), "testuser", request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No editable fields");

            verify(dataProposalRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when car not found")
        void createDataProposal_CarNotFound_ThrowsException() {
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory(ProposalCategory.ENGINE);
            request.setProposedValues(Map.of("maxPower", 220));

            UUID nonExistentCarId = UUID.randomUUID();

            when(carRepository.findById(nonExistentCarId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> dataProposalService.createDataProposal(nonExistentCarId, "testuser", request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void createDataProposal_UserNotFound_ThrowsException() {
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory(ProposalCategory.ENGINE);
            request.setProposedValues(Map.of("maxPower", 220));

            when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> dataProposalService.createDataProposal(testCar.getId(), "unknown", request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(dataProposalRepository, never()).save(any());
        }

        @Test
        @DisplayName("should create data proposal with TAGS category")
        void createDataProposal_TagsCategory_CreatesProposal() {
            UUID tag1Id = UUID.randomUUID();
            UUID tag2Id = UUID.randomUUID();

            CreateDataProposalRequest request = new CreateDataProposalRequest();
            request.setCategory(ProposalCategory.TAGS);
            request.setComment("Add and remove tags");
            request.setProposedValues(Map.of(
                    "addTagIds", List.of(tag1Id.toString(), tag2Id.toString()),
                    "removeTagIds", List.of(UUID.randomUUID().toString())
            ));

            when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            dataProposalService.createDataProposal(testCar.getId(), "testuser", request);
            ArgumentCaptor<DataProposal> proposalCaptor = ArgumentCaptor.forClass(DataProposal.class);
            verify(dataProposalRepository).save(proposalCaptor.capture());

            DataProposal savedProposal = proposalCaptor.getValue();
            assertThat(savedProposal.getCategory()).isEqualTo(ProposalCategory.TAGS);
            assertThat(savedProposal.getProposedValues()).containsKeys("addTagIds", "removeTagIds");
        }
    }


    @Nested
    @DisplayName("getPendingDataProposals")
    class GetPendingDataProposalsTests {

        @Test
        @DisplayName("should return pending proposals")
        void getPendingDataProposals_ReturnsProposals() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<DataProposal> proposalPage = new PageImpl<>(List.of(testProposal), pageable, 1);

            GetDataProposalsResponse responseDto = GetDataProposalsResponse.builder()
                    .id(testProposal.getId())
                    .status(ModerationStatus.PENDING)
                    .build();

            when(dataProposalRepository.findByStatus(ModerationStatus.PENDING, pageable))
                    .thenReturn(proposalPage);
            when(dataProposalsMapper.toDto(testProposal)).thenReturn(responseDto);

            Page<GetDataProposalsResponse> result = dataProposalService.getPendingDataProposals(pageable);
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getStatus()).isEqualTo(ModerationStatus.PENDING);

            verify(dataProposalRepository).findByStatus(ModerationStatus.PENDING, pageable);
        }
    }


    @Nested
    @DisplayName("resolveDataProposal")
    class ResolveDataProposalTests {

        @Test
        @DisplayName("should reject proposal when approve is false")
        void resolveDataProposal_Reject_SetsRejectedStatus() {
            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            dataProposalService.resolveDataProposal(testProposal.getId(), false, "Not valid data");
            assertThat(testProposal.getStatus()).isEqualTo(ModerationStatus.REJECTED);
            assertThat(testProposal.getAdminComment()).isEqualTo("Not valid data");
            assertThat(testProposal.getResolvedAt()).isNotNull();

            verify(dataProposalRepository).save(testProposal);
        }

        @Test
        @DisplayName("should approve proposal and apply changes when approve is true")
        void resolveDataProposal_Approve_SetsApprovedStatusAndAppliesChanges() throws Exception {
            testProposal.setCategory(ProposalCategory.ENGINE);
            testProposal.setProposedValues(Map.of("maxPower", 250));

            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            JsonNode engineNode = mock(ObjectNode.class);
            JsonNode changesNode = mock(ObjectNode.class);

            when(objectMapper.valueToTree(testCar.getEngine())).thenReturn(engineNode);
            when(objectMapper.valueToTree(testProposal.getProposedValues())).thenReturn(changesNode);
            when(objectMapper.readerForUpdating(testCar.getEngine())).thenReturn(mock(ObjectReader.class));

            dataProposalService.resolveDataProposal(testProposal.getId(), true, "Changes approved");
            assertThat(testProposal.getStatus()).isEqualTo(ModerationStatus.APPROVED);
            assertThat(testProposal.getAdminComment()).isEqualTo("Changes approved");
            assertThat(testProposal.getResolvedAt()).isNotNull();

            verify(dataProposalRepository).save(testProposal);
            verify(carRepository).save(testCar);
        }

        @Test
        @DisplayName("should throw IllegalStateException when approving ENGINE proposal but car has no engine")
        void resolveDataProposal_ApproveEngineNoEngine_ThrowsException() {
            testProposal.setCategory(ProposalCategory.ENGINE);

            testCar.setEngine(null);

            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            assertThatThrownBy(() -> dataProposalService.resolveDataProposal(testProposal.getId(), true, "OK"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Car has no engine defined");
        }

        @Test
        @DisplayName("should ignore non-editable fields of a proposal stored before the whitelist")
        void resolveDataProposal_ApproveLegacyProposalWithForbiddenField_AppliesOnlyAllowedFields() throws Exception {
            testProposal.setCategory(ProposalCategory.ENGINE);
            testProposal.setProposedValues(new LinkedHashMap<>(Map.of(
                    "maxPower", 250,
                    "id", UUID.randomUUID().toString()
            )));

            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            when(objectMapper.valueToTree(testCar.getEngine())).thenReturn(mock(ObjectNode.class));
            when(objectMapper.valueToTree(Map.of("maxPower", 250))).thenReturn(mock(ObjectNode.class));
            when(objectMapper.readerForUpdating(testCar.getEngine())).thenReturn(mock(ObjectReader.class));

            dataProposalService.resolveDataProposal(testProposal.getId(), true, "Approved");
            assertThat(testProposal.getStatus()).isEqualTo(ModerationStatus.APPROVED);

            verify(objectMapper).valueToTree(Map.of("maxPower", 250));
            verify(objectMapper, never()).valueToTree(testProposal.getProposedValues());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when proposal not found")
        void resolveDataProposal_ProposalNotFound_ThrowsException() {
            UUID nonExistentProposalId = UUID.randomUUID();

            when(dataProposalRepository.findById(nonExistentProposalId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> dataProposalService.resolveDataProposal(nonExistentProposalId, true, "OK"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("should throw IllegalStateException when proposal already resolved")
        void resolveDataProposal_AlreadyResolved_ThrowsException() {
            testProposal.setStatus(ModerationStatus.APPROVED);

            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            assertThatThrownBy(() -> dataProposalService.resolveDataProposal(testProposal.getId(), true, "OK"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already resolved");

            verify(dataProposalRepository, never()).save(any());
        }

        @Test
        @DisplayName("should handle CHASSIS category when approving")
        void resolveDataProposal_ApproveChassisCategory_AppliesChanges() throws Exception {
            testProposal.setCategory(ProposalCategory.CHASSIS);
            testProposal.setProposedValues(Map.of("frontBrakesRadius", 340));

            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            JsonNode chassisNode = mock(ObjectNode.class);
            JsonNode changesNode = mock(ObjectNode.class);

            when(objectMapper.valueToTree(testCar.getChassis())).thenReturn(chassisNode);
            when(objectMapper.valueToTree(testProposal.getProposedValues())).thenReturn(changesNode);
            when(objectMapper.readerForUpdating(testCar.getChassis())).thenReturn(mock(ObjectReader.class));

            dataProposalService.resolveDataProposal(testProposal.getId(), true, "Chassis changes approved");
            assertThat(testProposal.getStatus()).isEqualTo(ModerationStatus.APPROVED);
            verify(carRepository).save(testCar);
        }

        @Test
        @DisplayName("should handle TRANSMISSION category when approving")
        void resolveDataProposal_ApproveTransmissionCategory_AppliesChanges() throws Exception {
            testProposal.setCategory(ProposalCategory.TRANSMISSION);
            testProposal.setProposedValues(Map.of("gearsNumber", 8));

            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            JsonNode transmissionNode = mock(ObjectNode.class);
            JsonNode changesNode = mock(ObjectNode.class);

            when(objectMapper.valueToTree(testCar.getTransmission())).thenReturn(transmissionNode);
            when(objectMapper.valueToTree(testProposal.getProposedValues())).thenReturn(changesNode);
            when(objectMapper.readerForUpdating(testCar.getTransmission())).thenReturn(mock(ObjectReader.class));

            dataProposalService.resolveDataProposal(testProposal.getId(), true, "Transmission changes approved");
            assertThat(testProposal.getStatus()).isEqualTo(ModerationStatus.APPROVED);
            verify(carRepository).save(testCar);
        }

        @Test
        @DisplayName("should handle TAGS category when approving - add tags")
        void resolveDataProposal_ApproveTagsCategory_AddsTags() {
            UUID tag1Id = UUID.randomUUID();
            UUID tag2Id = UUID.randomUUID();

            Tag tag1 = Tag.builder().id(tag1Id).name("Sportowy").build();
            Tag tag2 = Tag.builder().id(tag2Id).name("Ekonomiczny").build();

            testCar.setTags(new HashSet<>());

            testProposal.setCategory(ProposalCategory.TAGS);
            testProposal.setProposedValues(Map.of("addTagIds", List.of(tag1Id.toString(), tag2Id.toString())));

            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));
            when(tagRepository.findAllById(anyList())).thenReturn(List.of(tag1, tag2));

            dataProposalService.resolveDataProposal(testProposal.getId(), true, "Tags added");
            assertThat(testProposal.getStatus()).isEqualTo(ModerationStatus.APPROVED);
            assertThat(testCar.getTags()).containsExactlyInAnyOrder(tag1, tag2);
            verify(carRepository).save(testCar);
        }

        @Test
        @DisplayName("should handle TAGS category when approving - remove tags")
        void resolveDataProposal_ApproveTagsCategory_RemovesTags() {
            UUID tag1Id = UUID.randomUUID();
            UUID tag2Id = UUID.randomUUID();
            UUID tag3Id = UUID.randomUUID();

            Tag tag1 = Tag.builder().id(tag1Id).name("Sportowy").build();
            Tag tag2 = Tag.builder().id(tag2Id).name("Ekonomiczny").build();
            Tag tag3 = Tag.builder().id(tag3Id).name("Rodzinny").build();

            Set<Tag> existingTags = new HashSet<>();
            existingTags.add(tag1);
            existingTags.add(tag2);
            existingTags.add(tag3);
            testCar.setTags(existingTags);

            testProposal.setCategory(ProposalCategory.TAGS);
            testProposal.setProposedValues(Map.of("removeTagIds", List.of(tag1Id.toString(), tag2Id.toString())));

            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));

            dataProposalService.resolveDataProposal(testProposal.getId(), true, "Tags removed");
            assertThat(testProposal.getStatus()).isEqualTo(ModerationStatus.APPROVED);
            assertThat(testCar.getTags()).containsExactly(tag3);
            verify(carRepository).save(testCar);
        }

        @Test
        @DisplayName("should handle TAGS category when approving - add and remove tags")
        void resolveDataProposal_ApproveTagsCategory_AddAndRemoveTags() {
            UUID existingTagId = UUID.randomUUID();
            Tag existingTag = Tag.builder().id(existingTagId).name("Sportowy").build();

            UUID newTagId = UUID.randomUUID();
            Tag newTag = Tag.builder().id(newTagId).name("Ekonomiczny").build();

            Set<Tag> existingTags = new HashSet<>();
            existingTags.add(existingTag);
            testCar.setTags(existingTags);

            testProposal.setCategory(ProposalCategory.TAGS);
            testProposal.setProposedValues(Map.of(
                    "addTagIds", List.of(newTagId.toString()),
                    "removeTagIds", List.of(existingTagId.toString())
            ));

            when(dataProposalRepository.findById(testProposal.getId())).thenReturn(Optional.of(testProposal));
            when(tagRepository.findAllById(anyList())).thenReturn(List.of(newTag));

            dataProposalService.resolveDataProposal(testProposal.getId(), true, "Tags modified");
            assertThat(testProposal.getStatus()).isEqualTo(ModerationStatus.APPROVED);
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
            Pageable pageable = PageRequest.of(0, 10);

            Page<DataProposal> proposalPage = new PageImpl<>(List.of(testProposal), pageable, 1);

            GetDataProposalsResponse response = new GetDataProposalsResponse();
            response.setId(UUID.randomUUID());

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(dataProposalRepository.findByUser(testUser, pageable)).thenReturn(proposalPage);
            when(dataProposalsMapper.toDto(testProposal)).thenReturn(response);

            Page<GetDataProposalsResponse> result = dataProposalService.getUserDataProposals("testuser", pageable);
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            verify(userRepository).findByUsername("testuser");
            verify(dataProposalRepository).findByUser(testUser, pageable);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void getUserDataProposals_UserNotFound_ThrowsException() {
            Pageable pageable = PageRequest.of(0, 10);

            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> dataProposalService.getUserDataProposals("unknown", pageable))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(dataProposalRepository, never()).findByUser(any(), any());
        }
    }
}
