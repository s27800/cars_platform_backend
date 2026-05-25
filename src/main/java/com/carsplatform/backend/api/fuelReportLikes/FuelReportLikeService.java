package com.carsplatform.backend.api.fuelReportLikes;

import com.carsplatform.backend.api.fuelReportLikes.dtos.FuelReportLikeResponse;
import com.carsplatform.backend.api.fuelReports.FuelReportRepository;
import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FuelReportLikeService {
    private final FuelReportLikeRepository fuelReportLikeRepository;
    private final UserRepository userRepository;
    private final FuelReportRepository fuelReportRepository;

    @Transactional
    public FuelReportLikeResponse toggleLike(Long fuelReportId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        FuelReport fuelReport = fuelReportRepository.findById(fuelReportId)
                .orElseThrow(() -> new ResourceNotFoundException("FuelReport", "id", fuelReportId));

        var existingLike = fuelReportLikeRepository.findByUserIdAndFuelReportId(user.getId(), fuelReportId);

        boolean isLikedNow;

        if (existingLike.isPresent()) {
            fuelReportLikeRepository.delete(existingLike.get());
            isLikedNow = false;
        } else {
            FuelReportLike newLike = FuelReportLike.builder()
                    .user(user)
                    .fuelReport(fuelReport)
                    .build();

            fuelReportLikeRepository.save(newLike);
            isLikedNow = true;
        }

        long count = fuelReportLikeRepository.countByFuelReportId(fuelReportId);

        return FuelReportLikeResponse.builder()
                .isLiked(isLikedNow)
                .likesCount(count)
                .build();
    }

    @Transactional(readOnly = true)
    public FuelReportLikeResponse getLikeStatus(Long fuelReportId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        boolean isLiked = fuelReportLikeRepository.existsByUserIdAndFuelReportId(user.getId(), fuelReportId);
        long count = fuelReportLikeRepository.countByFuelReportId(fuelReportId);

        return FuelReportLikeResponse.builder()
                .isLiked(isLiked)
                .likesCount(count)
                .build();
    }
}
