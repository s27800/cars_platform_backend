package com.carsplatform.backend.api.fuelReportLikes;

import com.carsplatform.backend.api.fuelReportLikes.dtos.FuelReportLikeResponse;
import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.fuelReports.FuelReportRepository;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.common.TestDataFactory;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("FuelReportLikeService Tests")
class FuelReportLikeServiceTest {

    @Mock
    private FuelReportLikeRepository fuelReportLikeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FuelReportRepository fuelReportRepository;

    @InjectMocks
    private FuelReportLikeService fuelReportLikeService;

    private User testUser;
    private FuelReport testFuelReport;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = TestDataFactory.defaultUser()
                .id(UUID.randomUUID())
                .build();

        // Create test fuel report
        testFuelReport = FuelReport.builder()
                .id(UUID.randomUUID())
                .fuelConsumption(new BigDecimal("7.5"))
                .comment("Test fuel report")
                .isApproved(true)
                .build();
    }


    @Nested
    @DisplayName("toggleLike")
    class ToggleLikeTests {

        @Test
        @DisplayName("should add like when not already liked")
        void toggleLike_NotLiked_AddsLike() {

            // Mock dependencies
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(fuelReportRepository.findById(testFuelReport.getId())).thenReturn(Optional.of(testFuelReport));
            when(fuelReportLikeRepository.findByUserIdAndFuelReportId(testUser.getId(), testFuelReport.getId())).thenReturn(Optional.empty());
            when(fuelReportLikeRepository.countByFuelReportId(testFuelReport.getId())).thenReturn(1L);

            // Toggle like
            FuelReportLikeResponse result = fuelReportLikeService.toggleLike(testFuelReport.getId(), "testuser");

            // Verify like added
            assertThat(result.isLiked()).isTrue();
            assertThat(result.getLikesCount()).isEqualTo(1);

            // Check like saved
            ArgumentCaptor<FuelReportLike> likeCaptor = ArgumentCaptor.forClass(FuelReportLike.class);

            verify(fuelReportLikeRepository).save(likeCaptor.capture());

            FuelReportLike savedLike = likeCaptor.getValue();

            assertThat(savedLike.getUser()).isEqualTo(testUser);
            assertThat(savedLike.getFuelReport()).isEqualTo(testFuelReport);
        }

        @Test
        @DisplayName("should remove like when already liked")
        void toggleLike_AlreadyLiked_RemovesLike() {

            // Create like
            FuelReportLike existingLike = FuelReportLike.builder()
                    .id(UUID.randomUUID())
                    .user(testUser)
                    .fuelReport(testFuelReport)
                    .build();

            // Mock dependencies
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(fuelReportRepository.findById(testFuelReport.getId())).thenReturn(Optional.of(testFuelReport));
            when(fuelReportLikeRepository.findByUserIdAndFuelReportId(testUser.getId(), testFuelReport.getId())).thenReturn(Optional.of(existingLike));
            when(fuelReportLikeRepository.countByFuelReportId(testFuelReport.getId())).thenReturn(0L);

            // Toggle like
            FuelReportLikeResponse result = fuelReportLikeService.toggleLike(testFuelReport.getId(), "testuser");

            // Verify like removed
            assertThat(result.isLiked()).isFalse();
            assertThat(result.getLikesCount()).isZero();

            verify(fuelReportLikeRepository).delete(existingLike);
            verify(fuelReportLikeRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void toggleLike_UserNotFound_ThrowsException() {

            // Mock dependencies
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            // Toggle like for non-existent user
            assertThatThrownBy(() -> fuelReportLikeService.toggleLike(testFuelReport.getId(), "unknown"))
                    .isInstanceOf(ResourceNotFoundException.class);

            // Verify like not saved or deleted
            verify(fuelReportLikeRepository, never()).save(any());
            verify(fuelReportLikeRepository, never()).delete(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when fuel report not found")
        void toggleLike_FuelReportNotFound_ThrowsException() {

            // Mock dependencies
            UUID nonExistentReportId = UUID.randomUUID();
            
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(fuelReportRepository.findById(nonExistentReportId)).thenReturn(Optional.empty());

            // Toggle like for non-existent fuel report
            assertThatThrownBy(() -> fuelReportLikeService.toggleLike(nonExistentReportId, "testuser"))
                    .isInstanceOf(ResourceNotFoundException.class);

            // Verify like not saved or deleted
            verify(fuelReportLikeRepository, never()).save(any());
            verify(fuelReportLikeRepository, never()).delete(any());
        }
    }


    @Nested
    @DisplayName("getLikeStatus")
    class GetLikeStatusTests {

        @Test
        @DisplayName("should return liked status when user has liked")
        void getLikeStatus_UserLiked_ReturnsLikedTrue() {

            // Mock dependencies
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(fuelReportLikeRepository.existsByUserIdAndFuelReportId(testUser.getId(), testFuelReport.getId())).thenReturn(true);
            when(fuelReportLikeRepository.countByFuelReportId(testFuelReport.getId())).thenReturn(5L);

            // Get like status
            FuelReportLikeResponse result = fuelReportLikeService.getLikeStatus(testFuelReport.getId(), "testuser");

            // Verify result -> liked
            assertThat(result.isLiked()).isTrue();
            assertThat(result.getLikesCount()).isEqualTo(5);
        }

        @Test
        @DisplayName("should return not liked status when user has not liked")
        void getLikeStatus_UserNotLiked_ReturnsLikedFalse() {

            // Mock dependencies
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(fuelReportLikeRepository.existsByUserIdAndFuelReportId(testUser.getId(), testFuelReport.getId())).thenReturn(false);
            when(fuelReportLikeRepository.countByFuelReportId(testFuelReport.getId())).thenReturn(3L);

            // Get like status
            FuelReportLikeResponse result = fuelReportLikeService.getLikeStatus(testFuelReport.getId(), "testuser");

            // Verify result -> not liked
            assertThat(result.isLiked()).isFalse();
            assertThat(result.getLikesCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void getLikeStatus_UserNotFound_ThrowsException() {

            // Mock dependencies
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            // Get like status for non-existent user and verify results -> ResourceNotFoundException is thrown
            assertThatThrownBy(() -> fuelReportLikeService.getLikeStatus(testFuelReport.getId(), "unknown"))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(fuelReportLikeRepository, never()).existsByUserIdAndFuelReportId(any(), any());
        }
    }
}
