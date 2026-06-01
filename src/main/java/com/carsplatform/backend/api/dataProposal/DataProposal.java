package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.users.User;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "data_proposal")
@Data
@NoArgsConstructor
public class DataProposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(nullable = false)
    private String category;

    private String comment;

    private String adminComment;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> proposedValues;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataProposalStatus status = DataProposalStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
}
