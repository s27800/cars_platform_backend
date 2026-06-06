package com.carsplatform.backend.api.fuelReportLikes;

import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fuel_report_likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "fuel_report_id"}, name = "uk_user_fuel_report_like")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelReportLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fuel_report_id", nullable = false)
    @JsonIgnore
    private FuelReport fuelReport;
}
