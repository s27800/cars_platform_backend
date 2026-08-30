package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.cars.CarRepository;
import com.carsplatform.backend.api.dataProposal.dtos.CreateDataProposalRequest;
import com.carsplatform.backend.api.dataProposal.dtos.GetDataProposalsResponse;
import com.carsplatform.backend.api.tags.Tag;
import com.carsplatform.backend.api.tags.TagRepository;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataProposalService {
    private final DataProposalRepository dataProposalRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final GetDataProposalsMapper dataProposalsMapper;

    @Transactional
    public void createDataProposal(UUID carId, String username, CreateDataProposalRequest dto) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Car not found."));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        // Keep only the fields that belong to the category
        String category = DataProposalFields.normalizeCategory(dto.getCategory());
        Map<String, Object> proposedValues = DataProposalFields.filter(category, dto.getProposedValues());

        if (proposedValues.isEmpty())
            throw new IllegalArgumentException(
                    "No editable fields for category " + category + ". Allowed fields: "
                            + DataProposalFields.allowedFieldsFor(category) + ".");

        DataProposal proposal = new DataProposal();

        proposal.setCar(car);
        proposal.setUser(user);
        proposal.setCategory(category);
        proposal.setComment(dto.getComment());
        proposal.setProposedValues(proposedValues);
        proposal.setStatus(DataProposalStatus.PENDING);

        dataProposalRepository.save(proposal);
    }

    @Transactional(readOnly = true)
    public Page<GetDataProposalsResponse> getPendingDataProposals(Pageable pageable) {
        Page<DataProposal> proposals = dataProposalRepository.findByStatus(DataProposalStatus.PENDING, pageable);
        return proposals.map(dataProposalsMapper::toDto);
    }

    @Transactional
    public void resolveDataProposal(UUID proposalId, boolean approve, String adminComment) {
        DataProposal proposal = dataProposalRepository.findById(proposalId)
                .orElseThrow(() -> new EntityNotFoundException("Proposal not found."));

        if (proposal.getStatus() != DataProposalStatus.PENDING)
            throw new IllegalStateException("Proposal is already resolved.");

        if (approve) {
            applyChanges(proposal);
            proposal.setStatus(DataProposalStatus.APPROVED);
        } else {
            proposal.setStatus(DataProposalStatus.REJECTED);
        }

        proposal.setAdminComment(adminComment);
        proposal.setResolvedAt(LocalDateTime.now());
        dataProposalRepository.save(proposal);
    }

    @Transactional(readOnly = true)
    public Page<GetDataProposalsResponse> getUserDataProposals(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        Page<DataProposal> proposals = dataProposalRepository.findByUser(user, pageable);

        return proposals.map(dataProposalsMapper::toDto);
    }

    private void applyChanges(DataProposal proposal) {
        Car car = proposal.getCar();

        // Filtered again on approval
        Map<String, Object> changes = DataProposalFields.filter(proposal.getCategory(), proposal.getProposedValues());

        if (changes.isEmpty())
            throw new IllegalStateException("Proposal contains no fields that can be applied.");

        switch (proposal.getCategory().toUpperCase()) {
            case "BASIC_INFO" -> updateEntity(car, changes);
            case "ENGINE" -> {
                if (car.getEngine() == null) throw new IllegalStateException("Car has no engine defined.");
                updateEntity(car.getEngine(), changes);
            }
            case "CHASSIS" -> {
                if (car.getChassis() == null) throw new IllegalStateException("Car has no chassis defined.");
                updateEntity(car.getChassis(), changes);
            }
            case "TRANSMISSION" -> {
                if (car.getTransmission() == null) throw new IllegalStateException("Car has no transmission defined.");
                updateEntity(car.getTransmission(), changes);
            }
            case "PERFORMANCE" -> {
                if (car.getPerformance() == null) throw new IllegalStateException("Car has no performance defined.");
                updateEntity(car.getPerformance(), changes);
            }
            case "INSIDE_DIMENSIONS" -> {
                if (car.getInsideDimensions() == null)
                    throw new IllegalStateException("Car has no inside dimensions defined.");
                updateEntity(car.getInsideDimensions(), changes);
            }
            case "OUTSIDE_DIMENSIONS" -> {
                if (car.getOutsideDimensions() == null)
                    throw new IllegalStateException("Car has no outside dimensions defined.");
                updateEntity(car.getOutsideDimensions(), changes);
            }
            case "TAGS" -> applyTagChanges(car, changes);
            default -> throw new IllegalArgumentException("Unknown category: " + proposal.getCategory() + ".");
        }

        carRepository.save(car);
    }

    @SuppressWarnings("unchecked")
    private void applyTagChanges(Car car, Map<String, Object> changes) {
        
        // Handle tags to add
        Object addTagIdsObj = changes.get("addTagIds");
        if (addTagIdsObj instanceof List<?> addTagIdsList) {
            List<UUID> addTagIds = addTagIdsList.stream()
                    .map(id -> id instanceof String ? UUID.fromString((String) id) : (UUID) id)
                    .collect(Collectors.toList());

            if (!addTagIds.isEmpty()) {
                List<Tag> tagsToAdd = tagRepository.findAllById(addTagIds);
                car.getTags().addAll(tagsToAdd);
            }
        }

        // Handle tags to remove
        Object removeTagIdsObj = changes.get("removeTagIds");
        if (removeTagIdsObj instanceof List<?> removeTagIdsList) {
            Set<UUID> removeTagIds = removeTagIdsList.stream()
                    .map(id -> id instanceof String ? UUID.fromString((String) id) : (UUID) id)
                    .collect(Collectors.toSet());

            if (!removeTagIds.isEmpty()) {
                car.getTags().removeIf(tag -> removeTagIds.contains(tag.getId()));
            }
        }
    }

    private void updateEntity(Object entityToUpdate, Map<String, Object> changes) {
        try {
            JsonNode entityNode = objectMapper.valueToTree(entityToUpdate);

            JsonNode changesNode = objectMapper.valueToTree(changes);

            if (entityNode instanceof ObjectNode && changesNode instanceof ObjectNode) {
                ((ObjectNode) entityNode).setAll((ObjectNode) changesNode);

                objectMapper.readerForUpdating(entityToUpdate).readValue(entityNode);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to apply data changes automatically", e);
        }
    }
}
