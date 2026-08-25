package br.com.fiap.restaurant.security;

import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.model.enums.UserType;
import br.com.fiap.restaurant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .login("joao123")
                .name("João Silva")
                .email("joao@email.com")
                .password("encoded_password")
                .userType(UserType.CLIENT)
                .enabled(true)
                .build();
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        when(userRepository.findByLogin("joao123")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("joao123");

        assertNotNull(userDetails);
        assertEquals("joao123", userDetails.getUsername());
        assertEquals("encoded_password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByLogin("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("nonexistent"));
    }

    @Test
    void testLoadUserByUsernameWithDifferentUsers() {
        User user2 = User.builder()
                .id(2L)
                .login("maria456")
                .name("Maria Silva")
                .userType(UserType.RESTAURANT_OWNER)
                .enabled(true)
                .build();

        when(userRepository.findByLogin("maria456")).thenReturn(Optional.of(user2));

        UserDetails userDetails = userDetailsService.loadUserByUsername("maria456");

        assertNotNull(userDetails);
        assertEquals("maria456", userDetails.getUsername());
    }

    @Test
    void testLoadDisabledUser() {
        User disabledUser = User.builder()
                .id(3L)
                .login("disabled_user")
                .name("Disabled User")
                .enabled(false)
                .userType(UserType.CLIENT)
                .build();

        when(userRepository.findByLogin("disabled_user")).thenReturn(Optional.of(disabledUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("disabled_user");

        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled());
    }

    @Test
    void testLoadUserByUsernameWithEmptyLogin() {
        when(userRepository.findByLogin("")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername(""));
    }

    @Test
    void testUserAuthorities() {
        when(userRepository.findByLogin("joao123")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("joao123");

        assertNotNull(userDetails.getAuthorities());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT")));
    }
}
