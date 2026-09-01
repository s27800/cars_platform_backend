package com.carsplatform.backend.api.fuelReportLikes;

import com.carsplatform.backend.api.fuelReports.FuelReportRepository;
import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.LikeResponse;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


/**
 * Likes on fuel reports. Behaves exactly like
 * {@link com.carsplatform.backend.api.reviewLikes.ReviewLikeService}, only for fuel reports.
 */
@Service
@RequiredArgsConstructor
public class FuelReportLikeService {

    private final FuelReportLikeRepository fuelReportLikeRepository;
    private final UserRepository userRepository;
    private final FuelReportRepository fuelReportRepository;


    @Transactional
    public LikeResponse toggleLike(UUID fuelReportId, String username) {
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
            try {
                FuelReportLike newLike = FuelReportLike.builder()
                        .user(user)
                        .fuelReport(fuelReport)
                        .build();

                fuelReportLikeRepository.save(newLike);

                isLikedNow = true;
            } catch (DataIntegrityViolationException e) {
                isLikedNow = true;
            }
        }

        long count = fuelReportLikeRepository.countByFuelReportId(fuelReportId);

        return LikeResponse.builder()
                .isLiked(isLikedNow)
                .likesCount(count)
                .build();
    }

    @Transactional(readOnly = true)
    public LikeResponse getLikeStatus(UUID fuelReportId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        boolean isLiked = fuelReportLikeRepository.existsByUserIdAndFuelReportId(user.getId(), fuelReportId);
        long count = fuelReportLikeRepository.countByFuelReportId(fuelReportId);

        return LikeResponse.builder()
                .isLiked(isLiked)
                .likesCount(count)
                .build();
    }
}
