package com.carsplatform.backend.common.security;

import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("UserPrincipal Tests")
class UserPrincipalTest {


    @Nested
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("should create UserPrincipal with ROLE_USER for regular user")
        void create_RegularUser_HasRoleUser() {
            User user = TestDataFactory.defaultUser()
                    .id(UUID.randomUUID())
                    .username("testuser")
                    .email("test@example.com")
                    .password("encodedPassword")
                    .isAdmin(false)
                    .build();

            UserPrincipal principal = UserPrincipal.create(user);

            assertThat(principal).isNotNull();
            assertThat(principal.getId()).isEqualTo(user.getId());
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
            User adminUser = TestDataFactory.adminUser()
                    .id(UUID.randomUUID())
                    .username("admin")
                    .email("admin@example.com")
                    .password("encodedAdminPassword")
                    .isAdmin(true)
                    .build();

            UserPrincipal principal = UserPrincipal.create(adminUser);

            assertThat(principal).isNotNull();
            assertThat(principal.getId()).isEqualTo(adminUser.getId());
            assertThat(principal.getUsername()).isEqualTo("admin");

            Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

            assertThat(authorities).hasSize(1);
            assertThat(authorities.stream().map(GrantedAuthority::getAuthority))
                    .containsExactly("ROLE_ADMIN");
        }

        @Test
        @DisplayName("should treat null isAdmin as regular user")
        void create_NullIsAdmin_TreatedAsRegularUser() {
            User user = TestDataFactory.defaultUser()
                    .id(UUID.randomUUID())
                    .isAdmin(null)
                    .build();

            UserPrincipal principal = UserPrincipal.create(user);

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
            User adminUser = TestDataFactory.adminUser()
                    .id(UUID.randomUUID())
                    .isAdmin(true)
                    .build();

            UserPrincipal principal = UserPrincipal.create(adminUser);

            assertThat(principal.isAdmin()).isTrue();
        }

        @Test
        @DisplayName("should return false when user has ROLE_USER")
        void isAdmin_RegularUser_ReturnsFalse() {
            User user = TestDataFactory.defaultUser()
                    .id(UUID.randomUUID())
                    .isAdmin(false)
                    .build();

            UserPrincipal principal = UserPrincipal.create(user);

            assertThat(principal.isAdmin()).isFalse();
        }
    }


    @Nested
    @DisplayName("UserDetails interface methods")
    class UserDetailsInterfaceTests {

        @Test
        @DisplayName("isAccountNonExpired should return true")
        void isAccountNonExpired_ReturnsTrue() {
            User user = TestDataFactory.defaultUser().id(UUID.randomUUID()).build();

            UserPrincipal principal = UserPrincipal.create(user);

            assertThat(principal.isAccountNonExpired()).isTrue();
        }

        @Test
        @DisplayName("isAccountNonLocked should return true")
        void isAccountNonLocked_ReturnsTrue() {
            User user = TestDataFactory.defaultUser().id(UUID.randomUUID()).build();

            UserPrincipal principal = UserPrincipal.create(user);

            assertThat(principal.isAccountNonLocked()).isTrue();
        }

        @Test
        @DisplayName("isCredentialsNonExpired should return true")
        void isCredentialsNonExpired_ReturnsTrue() {
            User user = TestDataFactory.defaultUser().id(UUID.randomUUID()).build();

            UserPrincipal principal = UserPrincipal.create(user);

            assertThat(principal.isCredentialsNonExpired()).isTrue();
        }

        @Test
        @DisplayName("isEnabled should return true")
        void isEnabled_ReturnsTrue() {
            User user = TestDataFactory.defaultUser().id(UUID.randomUUID()).build();

            UserPrincipal principal = UserPrincipal.create(user);

            assertThat(principal.isEnabled()).isTrue();
        }

        @Test
        @DisplayName("getPassword should return user password")
        void getPassword_ReturnsUserPassword() {
            User user = TestDataFactory.defaultUser()
                    .id(UUID.randomUUID())
                    .password("mySecretPassword")
                    .build();

            UserPrincipal principal = UserPrincipal.create(user);

            assertThat(principal.getPassword()).isEqualTo("mySecretPassword");
        }

        @Test
        @DisplayName("getUsername should return user username")
        void getUsername_ReturnsUserUsername() {
            User user = TestDataFactory.defaultUser()
                    .id(UUID.randomUUID())
                    .username("specificUsername")
                    .build();

            UserPrincipal principal = UserPrincipal.create(user);

            assertThat(principal.getUsername()).isEqualTo("specificUsername");
        }
    }
}
