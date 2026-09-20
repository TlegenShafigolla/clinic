package kz.tlegen.clinic.service;

import kz.tlegen.clinic.dto.user.UserRequest;
import kz.tlegen.clinic.dto.user.UserResponse;
import kz.tlegen.clinic.entity.Role;
import kz.tlegen.clinic.entity.User;
import kz.tlegen.clinic.exception.UserAlreadyExistsException;
import kz.tlegen.clinic.exception.UserNotFoundException;
import kz.tlegen.clinic.mapper.UserMapper;
import kz.tlegen.clinic.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void create_shouldReturnUserResponse() {
        UserRequest request = new UserRequest(
                "alex@gmail.com",
                "Qwerty123",
                Role.ADMIN,
                true
        );

        User user = new User(
                request.getEmail(),
                "encodedPassword",
                request.getRole(),
                request.isActive()
        );

        UserResponse expectedResponse = new UserResponse(
                1L,
                "alex@gmail.com",
                Role.ADMIN,
                true
        );

        when(userRepository.existsByEmail("alex@gmail.com"))
                .thenReturn(false);
        when(passwordEncoder.encode("Qwerty123"))
                .thenReturn("encodedPassword");
        when(userMapper.toEntity(request, "encodedPassword"))
                .thenReturn(user);
        when(userRepository.save(user))
                .thenReturn(user);
        when(userMapper.toResponse(user))
                .thenReturn(expectedResponse);

        UserResponse actualResponse = userService.create(request);

        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getEmail(), actualResponse.getEmail());
        assertEquals(expectedResponse.getRole(), actualResponse.getRole());
        assertEquals(expectedResponse.isActive(), actualResponse.isActive());

        verify(passwordEncoder).encode("Qwerty123");
        verify(userRepository).existsByEmail("alex@gmail.com");
        verify(userMapper).toEntity(request, "encodedPassword");
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    public void create_shouldThrowUserAlreadyExistsException() {
        UserRequest request = new UserRequest(
                "alex@gmail.com",
                "Qwerty123",
                Role.ADMIN,
                true
        );

        when(userRepository.existsByEmail("alex@gmail.com"))
                .thenReturn(true);
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () ->
                userService.create(request)
        );

        assertEquals(
                "User already exists with email: alex@gmail.com",
                exception.getMessage()
        );

        verify(userRepository)
                .existsByEmail("alex@gmail.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).toEntity(any(UserRequest.class), anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void findById_shouldReturnUserResponse() {
        User user = new User(
                "alex@gmail.com",
                "Qwerty123",
                Role.ADMIN,
                true
        );
        UserResponse response = new UserResponse(
                1L,
                "alex@gmail.com",
                Role.ADMIN,
                true
        );
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(userMapper.toResponse(user))
                .thenReturn(response);
        UserResponse actualResponse = userService.findById(1L);
        assertEquals(response.getId(), actualResponse.getId());
        assertEquals(response.getRole(), actualResponse.getRole());
        assertEquals(response.getEmail(), actualResponse.getEmail());
        assertEquals(response.isActive(), actualResponse.isActive());
        verify(userRepository).findById(1L);
        verify(userMapper).toResponse(user);
    }

    @Test
    public void findById_shouldThrowUserNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.findById(999L));
        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    public void findAll_shouldReturnUserResponses() {
        User firstUser = new User(
                "alex@gmail.com",
                "Qwerty123",
                Role.ADMIN,
                true
        );

        User secondUser = new User(
                "maks@gmail.com",
                "Qwerty123",
                Role.DOCTOR,
                false
        );
        UserResponse firstResponse = new UserResponse(
                1L,
                "alex@gmail.com",
                Role.ADMIN,
                true
        );

        UserResponse secondResponse = new UserResponse(
                2L,
                "maks@gmail.com",
                Role.DOCTOR,
                false
        );

        when(userRepository.findAll()).thenReturn(List.of(firstUser, secondUser));
        when(userMapper.toResponse(firstUser)).thenReturn(firstResponse);
        when(userMapper.toResponse(secondUser))
                .thenReturn(secondResponse);
        List<UserResponse> actualResponses = userService.findAll();
        assertEquals(2, actualResponses.size());
        assertEquals(firstResponse.getId(), actualResponses.get(0).getId());
        assertEquals(secondResponse.getId(), actualResponses.get(1).getId());
        verify(userRepository).findAll();
        verify(userMapper).toResponse(firstUser);
        verify(userMapper).toResponse(secondUser);
    }

    @Test
    public void update_shouldReturnUpdatedUserResponse() {
        UserRequest request = new UserRequest(
                "alex@gmail.com",
                "Qwerty123",
                Role.DOCTOR,
                false
        );

        User oldUser = new User(
                "alex@gmail.com",
                "encodedPassword",
                Role.ADMIN,
                true
        );
        UserResponse response = new UserResponse(
                1L,
                "alex@gmail.com",
                Role.DOCTOR,
                false
        );
        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.existsByEmailAndIdNot(
                "alex@gmail.com",
                1L
        )).thenReturn(false);
        when(userRepository.save(oldUser)).thenReturn(oldUser);
        when(passwordEncoder.encode("Qwerty123")).thenReturn("encodedPassword");
        when(userMapper.toResponse(oldUser)).thenReturn(response);
        UserResponse updatedResponse = userService.update(1L, request);
        assertEquals("encodedPassword",oldUser.getPassword());
        assertEquals(response.getId(), updatedResponse.getId());
        assertEquals(response.getRole(), updatedResponse.getRole());
        assertEquals(response.getEmail(), updatedResponse.getEmail());
        assertEquals(response.isActive(), updatedResponse.isActive());
        assertEquals(Role.DOCTOR, oldUser.getRole());
        assertFalse(oldUser.isActive());

        verify(userRepository).findById(1L);
        verify(userRepository)
                .existsByEmailAndIdNot("alex@gmail.com", 1L);
        verify(userRepository).save(oldUser);
        verify(passwordEncoder).encode("Qwerty123");
        verify(userMapper).toResponse(oldUser);
    }

    @Test
    public void update_shouldThrowUserAlreadyExistsException() {
        UserRequest request = new UserRequest(
                "alex@gmail.com",
                "Qwerty123",
                Role.DOCTOR,
                false
        );

        User oldUser = new User(
                "alex@gmail.com",
                "encodedPassword",
                Role.ADMIN,
                true
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.existsByEmailAndIdNot(
                "alex@gmail.com",
                1L
        )).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> userService.update(1L, request));
        assertEquals("User already exists with email: alex@gmail.com", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository)
                .existsByEmailAndIdNot("alex@gmail.com", 1L);
        verify(userRepository, never()).save(any());
        verify(passwordEncoder,never()).encode(anyString());
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    public void update_shouldThrowUserNotFoundException() {
        UserRequest request = new UserRequest(
                "alex@gmail.com",
                "Qwerty123",
                Role.DOCTOR,
                false
        );

        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.update(1L, request));
        assertEquals("User not found with id: 1", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    public void delete_shouldDeleteUser() {
        User user = new User(
                "alex@gmail.com",
                "Qwerty123",
                Role.ADMIN,
                true
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.delete(1L);
        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
    }

    @Test
    public void delete_shouldThrowUserNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.delete(1L));
        assertEquals("User not found with id: 1", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
        verify(userRepository, never()).delete(any());
        verify(userMapper, never()).toResponse(any());
    }


}
