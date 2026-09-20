package kz.tlegen.clinic.service;

import kz.tlegen.clinic.dto.user.UserRequest;
import kz.tlegen.clinic.dto.user.UserResponse;
import kz.tlegen.clinic.entity.User;
import kz.tlegen.clinic.exception.UserAlreadyExistsException;
import kz.tlegen.clinic.exception.UserNotFoundException;
import kz.tlegen.clinic.mapper.UserMapper;
import kz.tlegen.clinic.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User already exists with email: " + request.getEmail());
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = userMapper.toEntity(request, encodedPassword);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = getUserByIdOrThrow(id);
        return userMapper.toResponse(user);
    }

    private User getUserByIdOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public void delete(Long id) {
        User user = getUserByIdOrThrow(id);
        userRepository.delete(user);
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = getUserByIdOrThrow(id);

        boolean emailExists =
                userRepository.existsByEmailAndIdNot(
                        request.getEmail(),
                        id
                );

        if (emailExists) {
            throw new UserAlreadyExistsException(
                    "User already exists with email: " + request.getEmail()
            );
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.update(request.getEmail(),
                encodedPassword,
                request.getRole(),
                request.isActive()
        );
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}
