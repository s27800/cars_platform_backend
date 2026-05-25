package com.carsplatform.backend.api.likes;

import com.carsplatform.backend.api.likes.dtos.LikeResponse;
import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.reviews.ReviewRepository;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("LikeService Tests")
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private LikeService likeService;

    private User testUser;
    private Review testReview;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = TestDataFactory.defaultUser()
                .id(1L)
                .build();

        // Create test review
        testReview = Review.builder()
                .id(1L)
                .comment("Test review")
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
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
            when(likeRepository.findByUserIdAndReviewId(1L, 1L)).thenReturn(Optional.empty());
            when(likeRepository.countByReviewId(1L)).thenReturn(1L);

            // Toggle like
            LikeResponse result = likeService.toggleLike(1L, "testuser");

            // Verify like added
            assertThat(result.isLiked()).isTrue();
            assertThat(result.getLikesCount()).isEqualTo(1);

            // Check like saved
            ArgumentCaptor<Like> likeCaptor = ArgumentCaptor.forClass(Like.class);
            verify(likeRepository).save(likeCaptor.capture());

            // Verify saved like has correct user and review
            Like savedLike = likeCaptor.getValue();
            assertThat(savedLike.getUser()).isEqualTo(testUser);
            assertThat(savedLike.getReview()).isEqualTo(testReview);
        }

        @Test
        @DisplayName("should remove like when already liked")
        void toggleLike_AlreadyLiked_RemovesLike() {

            // Create like
            Like existingLike = Like.builder()
                    .id(1L)
                    .user(testUser)
                    .review(testReview)
                    .build();

            // Mock dependencies
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
            when(likeRepository.findByUserIdAndReviewId(1L, 1L)).thenReturn(Optional.of(existingLike));
            when(likeRepository.countByReviewId(1L)).thenReturn(0L);

            // Toggle like
            LikeResponse result = likeService.toggleLike(1L, "testuser");

            // Verify like removed
            assertThat(result.isLiked()).isFalse();
            assertThat(result.getLikesCount()).isZero();

            verify(likeRepository).delete(existingLike);
            verify(likeRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void toggleLike_UserNotFound_ThrowsException() {

            // Mock dependencies
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            // Toggle like for non-existent user
            assertThatThrownBy(() -> likeService.toggleLike(1L, "unknown"))
                    .isInstanceOf(ResourceNotFoundException.class);

            // Verify like not saved or deleted
            verify(likeRepository, never()).save(any());
            verify(likeRepository, never()).delete(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when review not found")
        void toggleLike_ReviewNotFound_ThrowsException() {

            // Mock dependencies
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

            // Toggle like for non-existent review
            assertThatThrownBy(() -> likeService.toggleLike(999L, "testuser"))
                    .isInstanceOf(ResourceNotFoundException.class);

            // Verify like not saved or deleted
            verify(likeRepository, never()).save(any());
            verify(likeRepository, never()).delete(any());
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
            when(likeRepository.existsByUserIdAndReviewId(1L, 1L)).thenReturn(true);
            when(likeRepository.countByReviewId(1L)).thenReturn(5L);

            // Create like
            LikeResponse result = likeService.getLikeStatus(1L, "testuser");

            // Verify result
            assertThat(result.isLiked()).isTrue();
            assertThat(result.getLikesCount()).isEqualTo(5);
        }

        @Test
        @DisplayName("should return not liked status when user has not liked")
        void getLikeStatus_UserNotLiked_ReturnsLikedFalse() {

            // Mock dependencies
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(likeRepository.existsByUserIdAndReviewId(1L, 1L)).thenReturn(false);
            when(likeRepository.countByReviewId(1L)).thenReturn(3L);

            // Get like status
            LikeResponse result = likeService.getLikeStatus(1L, "testuser");

            // Verify result
            assertThat(result.isLiked()).isFalse();
            assertThat(result.getLikesCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void getLikeStatus_UserNotFound_ThrowsException() {

            // Mock dependencies
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            // Get like status for non-existent user
            assertThatThrownBy(() -> likeService.getLikeStatus(1L, "unknown"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
