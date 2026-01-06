package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.users.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataProposalRepository extends JpaRepository<DataProposal, Long> {
    @EntityGraph(attributePaths = {"car", "car.generation.model.brand", "user"})
    Page<DataProposal> findByStatus(DataProposalStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"car", "car.generation.model.brand"})
    Page<DataProposal> findByUser(User user, Pageable pageable);
}
