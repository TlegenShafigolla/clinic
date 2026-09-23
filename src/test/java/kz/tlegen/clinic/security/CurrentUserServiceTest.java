package kz.tlegen.clinic.security;

import kz.tlegen.clinic.entity.Role;
import kz.tlegen.clinic.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CurrentUserServiceTest {
    private final CurrentUserService currentUserService =
            new CurrentUserService();

    @Test
    void getCurrentUser_shouldReturnUser_whenUserIsAuthenticated() {
        User user = new User(
                "alex@gmail.com",
                "encodedPassword",
                Role.PATIENT,
                true
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        User currentUser = currentUserService.getCurrentUser();

        assertSame(user, currentUser);
    }

    @Test
    void getCurrentUser_shouldThrowException_whenAuthenticationIsMissing() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                currentUserService::getCurrentUser
        );
        assertEquals(
                "User is not authenticated",
                exception.getMessage()
        );
    }

    @Test
    void getCurrentUser_shouldThrowException_whenPrincipalIsNotUser() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "anonymousUser",
                        null,
                        List.of()
                );
        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                currentUserService::getCurrentUser
        );
        assertEquals("Authenticated principal is not a User", exception.getMessage());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
