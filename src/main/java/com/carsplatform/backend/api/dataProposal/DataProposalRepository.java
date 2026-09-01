package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.ModerationStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface DataProposalRepository extends JpaRepository<DataProposal, UUID> {

    @EntityGraph(attributePaths = {"car", "car.generation.model.brand", "user"})
    Page<DataProposal> findByStatus(ModerationStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"car", "car.generation.model.brand"})
    Page<DataProposal> findByUser(User user, Pageable pageable);
}
