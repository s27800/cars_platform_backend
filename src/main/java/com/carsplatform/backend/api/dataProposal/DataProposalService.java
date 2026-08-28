package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.cars.CarRepository;
import com.carsplatform.backend.api.dataProposal.dtos.CreateDataProposalRequest;
import com.carsplatform.backend.api.dataProposal.dtos.GetDataProposalsResponse;
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
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DataProposalService {
    private final DataProposalRepository dataProposalRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final GetDataProposalsMapper dataProposalsMapper;

    @Transactional
    public void createDataProposal(UUID carId, String username, CreateDataProposalRequest dto) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Car not found."));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        DataProposal proposal = new DataProposal();

        proposal.setCar(car);
        proposal.setUser(user);
        proposal.setCategory(dto.getCategory());
        proposal.setComment(dto.getComment());
        proposal.setProposedValues(dto.getProposedValues());
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
        Map<String, Object> changes = proposal.getProposedValues();

        switch (proposal.getCategory().toUpperCase()) {
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
            default -> throw new IllegalArgumentException("Unknown category: " + proposal.getCategory() + ".");
        }

        carRepository.save(car);
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
