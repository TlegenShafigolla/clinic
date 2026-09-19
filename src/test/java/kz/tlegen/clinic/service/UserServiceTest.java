package kz.tlegen.clinic.service;

import kz.tlegen.clinic.dto.user.UserRequest;
import kz.tlegen.clinic.dto.user.UserResponse;
import kz.tlegen.clinic.entity.Role;
import kz.tlegen.clinic.entity.User;
import kz.tlegen.clinic.exception.UserAlreadyExistsException;
import kz.tlegen.clinic.mapper.UserMapper;
import kz.tlegen.clinic.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

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
                request.getPassword(),
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
        when(userMapper.toEntity(request))
                .thenReturn(user);
        when(userRepository.save(user))
                .thenReturn(user);
        when(userMapper.toResponse(user))
                .thenReturn(expectedResponse);

        UserResponse actualResponse = userService.create(request);

        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getEmail(), actualResponse.getEmail());
        assertEquals(expectedResponse.isActive(), actualResponse.isActive());

        verify(userRepository).existsByEmail("alex@gmail.com");
        verify(userMapper).toEntity(request);
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
                "Email already exists",
                exception.getMessage()
        );

        verify(userRepository)
                .existsByEmail("alex@gmail.com");
        verify(userMapper, never()).toEntity(request);
        verify(userRepository, never()).save(any());
    }

    @Test
    public void findById_shouldReturnUserResponse() {

    }
}
