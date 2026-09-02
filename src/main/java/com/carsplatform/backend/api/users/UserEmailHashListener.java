package com.carsplatform.backend.api.users;

import com.carsplatform.backend.common.security.crypto.BlindIndexService;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;


/**
 * Keeps {@code users.email_hash} in step with {@code users.email}. Recomputing the hash here
 * rather than at the places that set an address means the two columns cannot drift apart -
 * whichever path saves a user, the hash is rewritten from the address being stored.
 *
 * Hibernate resolves entity listeners through Spring's bean container, so the blind index
 * service can be injected here.
 */
@Component
@RequiredArgsConstructor
public class UserEmailHashListener {

    private final BlindIndexService blindIndexService;


    @PrePersist
    @PreUpdate
    void syncEmailHash(User user) {
        user.setEmailHash(blindIndexService.hash(user.getEmail()));
    }
}
