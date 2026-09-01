package com.carsplatform.backend.api.reviewLikes;

import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.users.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Entity
@Table(
    name = "review_likes",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_review_like", columnNames = {"user_id", "review_id"})
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    @JsonIgnore
    private Review review;
}
