package br.com.fiap.restaurant.security.jwt;

import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.model.enums.UserType;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;
    private static final String SECRET_KEY = Base64.getEncoder().encodeToString("my-super-secret-key-for-testing-that-is-long-enough".getBytes());
    private static final long EXPIRATION_MS = 3600000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET_KEY, EXPIRATION_MS);
        testUser = User.builder()
                .id(1L)
                .login("joao123")
                .name("João Silva")
                .email("joao@email.com")
                .userType(UserType.CLIENT)
                .enabled(true)
                .build();
    }

    @Test
    void testGenerateTokenSuccess() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void testGenerateTokenWithDifferentUsers() {
        User user2 = User.builder()
                .id(2L)
                .login("maria456")
                .name("Maria Silva")
                .userType(UserType.RESTAURANT_OWNER)
                .enabled(true)
                .build();

        String token1 = jwtService.generateToken(testUser);
        String token2 = jwtService.generateToken(user2);

        assertNotEquals(token1, token2);
    }

    @Test
    void testExtractUsernameSuccess() {
        String token = jwtService.generateToken(testUser);

        String username = jwtService.extractUsername(token);

        assertNotNull(username);
        assertEquals("joao123", username);
    }

    @Test
    void testExtractUsernameFromDifferentToken() {
        User user2 = User.builder()
                .id(2L)
                .login("maria456")
                .name("Maria Silva")
                .userType(UserType.RESTAURANT_OWNER)
                .enabled(true)
                .build();

        String token = jwtService.generateToken(user2);

        String username = jwtService.extractUsername(token);

        assertEquals("maria456", username);
    }

    @Test
    void testIsTokenValidSuccess() {
        String token = jwtService.generateToken(testUser);

        boolean isValid = jwtService.isTokenValid(token, testUser);

        assertTrue(isValid);
    }

    @Test
    void testIsTokenValidWithWrongUser() {
        String token = jwtService.generateToken(testUser);
        User differentUser = User.builder()
                .id(2L)
                .login("maria456")
                .name("Maria Silva")
                .userType(UserType.RESTAURANT_OWNER)
                .enabled(true)
                .build();

        boolean isValid = jwtService.isTokenValid(token, differentUser);

        assertFalse(isValid);
    }

    @Test
    void testIsTokenValidWithInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThrows(JwtException.class, () -> jwtService.isTokenValid(invalidToken, testUser));
    }

    @Test
    void testGetExpirationMs() {
        long expirationMs = jwtService.getExpirationMs();

        assertEquals(EXPIRATION_MS, expirationMs);
    }

    @Test
    void testTokenStructure() {
        String token = jwtService.generateToken(testUser);
        String[] parts = token.split("\\.");

        assertEquals(3, parts.length);
        assertNotNull(parts[0]);
        assertNotNull(parts[1]);
        assertNotNull(parts[2]);
    }

    @Test
    void testTokenExpirationValidation() throws InterruptedException {
        JwtService shortExpirationService = new JwtService(SECRET_KEY, 1);
        String token = shortExpirationService.generateToken(testUser);

        Thread.sleep(100);

        assertThrows(JwtException.class, () -> shortExpirationService.isTokenValid(token, testUser));
    }

    @Test
    void testMultipleTokensFromSameUser() {
        String token1 = jwtService.generateToken(testUser);
        String token2 = jwtService.generateToken(testUser);

        assertNotEquals(token1, token2);

        assertTrue(jwtService.isTokenValid(token1, testUser));
        assertTrue(jwtService.isTokenValid(token2, testUser));
    }
}
