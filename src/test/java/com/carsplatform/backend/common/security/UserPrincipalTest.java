package com.carsplatform.backend.common.security;

import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.*;


@DisplayName("UserPrincipal Tests")
class UserPrincipalTest {


    @Nested
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("should create UserPrincipal with ROLE_USER for regular user")
        void create_RegularUser_HasRoleUser() {

            // Create regular user
            User user = TestDataFactory.defaultUser()
                    .id(1L)
                    .username("testuser")
                    .email("test@example.com")
                    .password("encodedPassword")
                    .isAdmin(false)
                    .build();

            // Create UserPrincipal
            UserPrincipal principal = UserPrincipal.create(user);

            // Verify UserPrincipal has correct data and ROLE_USER authority
            assertThat(principal).isNotNull();
            assertThat(principal.getId()).isEqualTo(1L);
            assertThat(principal.getUsername()).isEqualTo("testuser");
            assertThat(principal.getEmail()).isEqualTo("test@example.com");
            assertThat(principal.getPassword()).isEqualTo("encodedPassword");

            Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

            assertThat(authorities).hasSize(1);
            assertThat(authorities.stream().map(GrantedAuthority::getAuthority))
                    .containsExactly("ROLE_USER");
        }

        @Test
        @DisplayName("should create UserPrincipal with ROLE_ADMIN for admin user")
        void create_AdminUser_HasRoleAdmin() {

            // Create admin user
            User adminUser = TestDataFactory.adminUser()
                    .id(2L)
                    .username("admin")
                    .email("admin@example.com")
                    .password("encodedAdminPassword")
                    .isAdmin(true)
                    .build();

            // Create UserPrincipal
            UserPrincipal principal = UserPrincipal.create(adminUser);

            // Verify UserPrincipal has correct data and ROLE_ADMIN authority
            assertThat(principal).isNotNull();
            assertThat(principal.getId()).isEqualTo(2L);
            assertThat(principal.getUsername()).isEqualTo("admin");

            Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

            assertThat(authorities).hasSize(1);
            assertThat(authorities.stream().map(GrantedAuthority::getAuthority))
                    .containsExactly("ROLE_ADMIN");
        }

        @Test
        @DisplayName("should treat null isAdmin as regular user")
        void create_NullIsAdmin_TreatedAsRegularUser() {

            // Create user with null isAdmin
            User user = TestDataFactory.defaultUser()
                    .id(3L)
                    .isAdmin(null)
                    .build();

            // Create UserPrincipal
            UserPrincipal principal = UserPrincipal.create(user);

            // Verify UserPrincipal has ROLE_USER authority
            Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

            assertThat(authorities).hasSize(1);
            assertThat(authorities.stream().map(GrantedAuthority::getAuthority))
                    .containsExactly("ROLE_USER");
        }
    }


    @Nested
    @DisplayName("isAdmin")
    class IsAdminTests {

        @Test
        @DisplayName("should return true when user has ROLE_ADMIN")
        void isAdmin_AdminUser_ReturnsTrue() {

            // Create admin user principal
            User adminUser = TestDataFactory.adminUser()
                    .id(1L)
                    .isAdmin(true)
                    .build();

            UserPrincipal principal = UserPrincipal.create(adminUser);

            // Verify isAdmin returns true
            assertThat(principal.isAdmin()).isTrue();
        }

        @Test
        @DisplayName("should return false when user has ROLE_USER")
        void isAdmin_RegularUser_ReturnsFalse() {

            // Create regular user principal
            User user = TestDataFactory.defaultUser()
                    .id(1L)
                    .isAdmin(false)
                    .build();

            UserPrincipal principal = UserPrincipal.create(user);

            // Verify isAdmin returns false
            assertThat(principal.isAdmin()).isFalse();
        }
    }


    @Nested
    @DisplayName("UserDetails interface methods")
    class UserDetailsInterfaceTests {

        @Test
        @DisplayName("isAccountNonExpired should return true")
        void isAccountNonExpired_ReturnsTrue() {

            // Create user principal
            User user = TestDataFactory.defaultUser().id(1L).build();

            UserPrincipal principal = UserPrincipal.create(user);

            // Verify isAccountNonExpired returns true
            assertThat(principal.isAccountNonExpired()).isTrue();
        }

        @Test
        @DisplayName("isAccountNonLocked should return true")
        void isAccountNonLocked_ReturnsTrue() {

            // Create user principal
            User user = TestDataFactory.defaultUser().id(1L).build();

            UserPrincipal principal = UserPrincipal.create(user);

            // Verify isAccountNonLocked returns true
            assertThat(principal.isAccountNonLocked()).isTrue();
        }

        @Test
        @DisplayName("isCredentialsNonExpired should return true")
        void isCredentialsNonExpired_ReturnsTrue() {

            // Create user principal
            User user = TestDataFactory.defaultUser().id(1L).build();

            UserPrincipal principal = UserPrincipal.create(user);

            // Verify isCredentialsNonExpired returns true
            assertThat(principal.isCredentialsNonExpired()).isTrue();
        }

        @Test
        @DisplayName("isEnabled should return true")
        void isEnabled_ReturnsTrue() {

            // Create user principal
            User user = TestDataFactory.defaultUser().id(1L).build();
            
            UserPrincipal principal = UserPrincipal.create(user);

            // Verify isEnabled returns true
            assertThat(principal.isEnabled()).isTrue();
        }

        @Test
        @DisplayName("getPassword should return user password")
        void getPassword_ReturnsUserPassword() {

            // Create user principal with specific password
            User user = TestDataFactory.defaultUser()
                    .id(1L)
                    .password("mySecretPassword")
                    .build();

            UserPrincipal principal = UserPrincipal.create(user);

            // Verify getPassword returns correct password
            assertThat(principal.getPassword()).isEqualTo("mySecretPassword");
        }

        @Test
        @DisplayName("getUsername should return user username")
        void getUsername_ReturnsUserUsername() {

            // Create user principal with specific username
            User user = TestDataFactory.defaultUser()
                    .id(1L)
                    .username("specificUsername")
                    .build();

            UserPrincipal principal = UserPrincipal.create(user);

            // Verify getUsername returns correct username
            assertThat(principal.getUsername()).isEqualTo("specificUsername");
        }
    }
}
