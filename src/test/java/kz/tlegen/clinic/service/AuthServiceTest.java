package kz.tlegen.clinic.service;

import kz.tlegen.clinic.dto.auth.LoginRequest;
import kz.tlegen.clinic.dto.auth.RegisterRequest;
import kz.tlegen.clinic.dto.user.UserResponse;
import kz.tlegen.clinic.entity.Role;
import kz.tlegen.clinic.entity.User;
import kz.tlegen.clinic.exception.InvalidCredentialsException;
import kz.tlegen.clinic.exception.UserAlreadyExistsException;
import kz.tlegen.clinic.mapper.UserMapper;
import kz.tlegen.clinic.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldReturnUserResponse() {
        RegisterRequest request = new RegisterRequest(
                "alex@gmail.com",
                "Qwerty123"
        );

        UserResponse expectedResponse = new UserResponse(
                1L,
                "alex@gmail.com",
                Role.PATIENT,
                true
        );

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponse(any(User.class)))
                .thenReturn(expectedResponse);
        UserResponse actualResponse = authService.register(request);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("alex@gmail.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(Role.PATIENT, savedUser.getRole());
        assertTrue(savedUser.isActive());

        assertEquals(1L, actualResponse.getId());
        assertEquals("alex@gmail.com", actualResponse.getEmail());
        assertEquals(Role.PATIENT, actualResponse.getRole());
        assertTrue(actualResponse.isActive());

        verify(userRepository).existsByEmail("alex@gmail.com");
        verify(passwordEncoder).encode("Qwerty123");
        verify(userMapper).toResponse(any(User.class));
    }

    @Test
    void register_shouldThrowUserAlreadyExistsException() {
        RegisterRequest request = new RegisterRequest(
                "alex@gmail.com",
                "Qwerty123"
        );
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(request)
        );
        assertEquals(
                "User already exists with email: alex@gmail.com",
                exception.getMessage()
        );
        verify(userRepository).existsByEmail(request.getEmail());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnUserResponse() {
        LoginRequest request = new LoginRequest(
                "alex@gmail.com",
                "Qwerty123"
        );
        User user = new User(
                "alex@gmail.com",
                "encodedPassword",
                Role.PATIENT,
                true
        );

        UserResponse expectedResponse = new UserResponse(
                1L,
                "alex@gmail.com",
                Role.PATIENT,
                true
        );
        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Qwerty123", "encodedPassword"))
                .thenReturn(true);
        when(userMapper.toResponse(user))
                .thenReturn(expectedResponse);
        UserResponse response = authService.login(request);

        assertEquals(1L, response.getId());
        assertEquals("alex@gmail.com", response.getEmail());
        assertEquals(Role.PATIENT, response.getRole());
        assertTrue(response.isActive());

        verify(userRepository)
                .findByEmail("alex@gmail.com");

        verify(passwordEncoder)
                .matches("Qwerty123", "encodedPassword");

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userMapper)
                .toResponse(user);
    }

    @Test
    void login_shouldThrowInvalidCredentialsWhenEmailDoesNotExist() {
        LoginRequest request = new LoginRequest(
                "alex@gmail.com",
                "Qwerty123"
        );


        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );
        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository)
                .findByEmail("alex@gmail.com");
        verify(passwordEncoder,never()).matches(anyString(), anyString());
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    void login_shouldThrowInvalidCredentialsWhenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest(
                "alex@gmail.com",
                "Qwerty123"
        );
        User user = new User(
                "alex@gmail.com",
                "encodedPassword",
                Role.PATIENT,
                true
        );
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Qwerty123", "encodedPassword")).thenReturn(false);
        InvalidCredentialsException exception =assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request));
        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder).matches("Qwerty123", "encodedPassword");
        verify(userMapper, never()).toResponse(any());
    }
}
