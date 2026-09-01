package com.carsplatform.backend.api.reviewLikes;

import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.reviews.ReviewRepository;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.common.LikeResponse;
import com.carsplatform.backend.common.ModerationStatus;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewLikeService Tests")
class ReviewLikeServiceTest {

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewLikeService reviewLikeService;

    private User testUser;
    private Review testReview;

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.defaultUser()
                .id(UUID.randomUUID())
                .build();
        testReview = Review.builder()
                .id(UUID.randomUUID())
                .comment("Test review")
                .status(ModerationStatus.APPROVED)
                .build();
    }


    @Nested
    @DisplayName("toggleLike")
    class ToggleLikeTests {

        @Test
        @DisplayName("should add like when not already liked")
        void toggleLike_NotLiked_AddsLike() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));
            when(reviewLikeRepository.findByUserIdAndReviewId(testUser.getId(), testReview.getId())).thenReturn(Optional.empty());
            when(reviewLikeRepository.countByReviewId(testReview.getId())).thenReturn(1L);

            LikeResponse result = reviewLikeService.toggleLike(testReview.getId(), "testuser");

            assertThat(result.isLiked()).isTrue();
            assertThat(result.getLikesCount()).isEqualTo(1);

            ArgumentCaptor<ReviewLike> likeCaptor = ArgumentCaptor.forClass(ReviewLike.class);
            verify(reviewLikeRepository).save(likeCaptor.capture());

            ReviewLike savedLike = likeCaptor.getValue();
            assertThat(savedLike.getUser()).isEqualTo(testUser);
            assertThat(savedLike.getReview()).isEqualTo(testReview);
        }

        @Test
        @DisplayName("should remove like when already liked")
        void toggleLike_AlreadyLiked_RemovesLike() {
            ReviewLike existingLike = ReviewLike.builder()
                    .id(UUID.randomUUID())
                    .user(testUser)
                    .review(testReview)
                    .build();

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));
            when(reviewLikeRepository.findByUserIdAndReviewId(testUser.getId(), testReview.getId())).thenReturn(Optional.of(existingLike));
            when(reviewLikeRepository.countByReviewId(testReview.getId())).thenReturn(0L);

            LikeResponse result = reviewLikeService.toggleLike(testReview.getId(), "testuser");

            assertThat(result.isLiked()).isFalse();
            assertThat(result.getLikesCount()).isZero();

            verify(reviewLikeRepository).delete(existingLike);
            verify(reviewLikeRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void toggleLike_UserNotFound_ThrowsException() {
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reviewLikeService.toggleLike(testReview.getId(), "unknown"))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(reviewLikeRepository, never()).save(any());
            verify(reviewLikeRepository, never()).delete(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when review not found")
        void toggleLike_ReviewNotFound_ThrowsException() {
            UUID nonExistentReviewId = UUID.randomUUID();

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(reviewRepository.findById(nonExistentReviewId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reviewLikeService.toggleLike(nonExistentReviewId, "testuser"))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(reviewLikeRepository, never()).save(any());
            verify(reviewLikeRepository, never()).delete(any());
        }
    }


    @Nested
    @DisplayName("getLikeStatus")
    class GetLikeStatusTests {

        @Test
        @DisplayName("should return liked status when user has liked")
        void getLikeStatus_UserLiked_ReturnsLikedTrue() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(reviewLikeRepository.existsByUserIdAndReviewId(testUser.getId(), testReview.getId())).thenReturn(true);
            when(reviewLikeRepository.countByReviewId(testReview.getId())).thenReturn(5L);

            LikeResponse result = reviewLikeService.getLikeStatus(testReview.getId(), "testuser");
            assertThat(result.isLiked()).isTrue();
            assertThat(result.getLikesCount()).isEqualTo(5);
        }

        @Test
        @DisplayName("should return not liked status when user has not liked")
        void getLikeStatus_UserNotLiked_ReturnsLikedFalse() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(reviewLikeRepository.existsByUserIdAndReviewId(testUser.getId(), testReview.getId())).thenReturn(false);
            when(reviewLikeRepository.countByReviewId(testReview.getId())).thenReturn(3L);

            LikeResponse result = reviewLikeService.getLikeStatus(testReview.getId(), "testuser");
            assertThat(result.isLiked()).isFalse();
            assertThat(result.getLikesCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void getLikeStatus_UserNotFound_ThrowsException() {
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reviewLikeService.getLikeStatus(testReview.getId(), "unknown"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
