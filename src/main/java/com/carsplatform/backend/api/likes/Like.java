package com.carsplatform.backend.api.likes;

import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.users.User;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "review_id"}, name = "uk_user_review_like")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;
}
