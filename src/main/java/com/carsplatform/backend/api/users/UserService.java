package com.carsplatform.backend.api.users;

import com.carsplatform.backend.api.users.dtos.UserChangePasswordRequest;
import com.carsplatform.backend.api.users.dtos.UserModifyRequest;
import com.carsplatform.backend.common.resourceExceptions.ResourceAlreadyExistsException;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;
import com.carsplatform.backend.common.security.UserPrincipal;
import com.carsplatform.backend.common.security.crypto.BlindIndexService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


/**
 * User profiles, and at the same time the {@link UserDetailsService} that Spring Security
 * calls to load a user during login and on every request carrying a JWT.
 *
 * Deleting an account takes the reviews, fuel reports, proposals and likes with it through
 * the cascades declared on the relations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final BlindIndexService blindIndexService;


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        log.info("User found: {}", username);

        return UserPrincipal.create(user);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(UUID userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        return UserPrincipal.create(user);
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() throws ResourceNotFoundException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    @Transactional
    public void updateUserProfile(UserModifyRequest userModifyRequest) throws ResourceAlreadyExistsException {
        User currentUser = getCurrentUser();

        if (
                !currentUser.getEmail().equals(userModifyRequest.getEmail())
                && userRepository.existsByEmailHash(blindIndexService.hash(userModifyRequest.getEmail()))
        )
            throw new ResourceAlreadyExistsException("Email", userModifyRequest.getEmail());

        userMapper.updateEntityFromDto(userModifyRequest, currentUser);

        log.info("Profile updated for user: {}", currentUser.getUsername());

        userRepository.save(currentUser);
    }

    @Transactional
    public void changeUserPassword(UserChangePasswordRequest changePasswordRequest) throws IllegalArgumentException {
        User currentUser = getCurrentUser();

        if (!passwordEncoder.matches(
                changePasswordRequest.getCurrentPassword(), currentUser.getPassword()))
            throw new IllegalArgumentException("Current password is incorrect.");

        currentUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(currentUser);

        log.info("Password changed for user: {}", currentUser.getUsername());
    }

    @Transactional
    public void deleteCurrentUser() {
        User currentUser = getCurrentUser();
        String username = currentUser.getUsername();

        userRepository.delete(currentUser);

        log.info("User account deleted: {}", username);
    }
}
