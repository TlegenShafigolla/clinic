package kz.tlegen.clinic.security;

import kz.tlegen.clinic.entity.Role;
import kz.tlegen.clinic.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "jwtSecret",
                "MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE="
        );

        ReflectionTestUtils.setField(
                jwtService,
                "jwtExpiration",
                86400000L
        );
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        User user = new User(
                "alex@gmail.com",
                "encodedPassword",
                Role.PATIENT,
                true
        );

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());

        assertEquals(
                "alex@gmail.com",
                jwtService.extractEmail(token)
        );

        assertEquals(
                "PATIENT",
                jwtService.extractRole(token)
        );
    }
}
