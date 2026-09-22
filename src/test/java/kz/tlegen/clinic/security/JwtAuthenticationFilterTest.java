package kz.tlegen.clinic.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import kz.tlegen.clinic.entity.Role;
import kz.tlegen.clinic.entity.User;
import kz.tlegen.clinic.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_shouldContinue_whenAuthorizationHeaderIsMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userRepository);
    }

    @Test
    void doFilter_shouldContinue_whenAuthorizationHeaderIsNotBearer() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Basic abc123");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userRepository);
    }

    @Test
    void doFilter_shouldContinue_whenTokenIsInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer broken-token");

        when(jwtService.extractEmail("broken-token"))
                .thenThrow(new JwtException("Invalid token"));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );
        verify(jwtService).extractEmail("broken-token");
        verifyNoInteractions(userRepository);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_shouldContinue_whenUserDoesNotExist() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer valid-token");

        when(jwtService.extractEmail("valid-token"))
                .thenReturn("alex@gmail.com");

        when(userRepository.findByEmail("alex@gmail.com"))
                .thenReturn(Optional.empty());

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(jwtService).extractEmail("valid-token");
        verify(userRepository).findByEmail("alex@gmail.com");
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).isTokenValid(any(), any());
    }

    @Test
    void doFilter_shouldContinue_whenTokenIsNotValidForUser() throws Exception {
        User user = new User(
                "alex@gmail.com",
                "encodedPassword",
                Role.PATIENT,
                true
        );
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer valid-token");

        when(jwtService.extractEmail("valid-token"))
                .thenReturn("alex@gmail.com");

        when(userRepository.findByEmail("alex@gmail.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.isTokenValid("valid-token", user))
                .thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(jwtService).extractEmail("valid-token");
        verify(userRepository).findByEmail("alex@gmail.com");
        verify(jwtService).isTokenValid("valid-token", user);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_shouldAuthenticateUser_whenTokenIsValid() throws Exception {
        User user = new User(
                "alex@gmail.com",
                "encodedPassword",
                Role.PATIENT,
                true
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer valid-token");

        when(jwtService.extractEmail("valid-token"))
                .thenReturn("alex@gmail.com");

        when(userRepository.findByEmail("alex@gmail.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.isTokenValid("valid-token", user))
                .thenReturn(true);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(user, authentication.getPrincipal());
        assertTrue(authentication.isAuthenticated());
        assertTrue(
                authentication.getAuthorities().stream()
                        .anyMatch(authority ->
                                authority.getAuthority().equals("ROLE_PATIENT")
                        )
        );
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_shouldContinue_whenAuthenticationAlreadyExists() throws Exception {
        UsernamePasswordAuthenticationToken existingAuthentication =
                new UsernamePasswordAuthenticationToken(
                        "existing-user",
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(existingAuthentication);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer valid-token");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertEquals(
                existingAuthentication,
                SecurityContextHolder.getContext().getAuthentication()
        );

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userRepository);
        verify(filterChain).doFilter(request, response);
    }
}
