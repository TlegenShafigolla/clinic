package kz.tlegen.clinic.repository;


import kz.tlegen.clinic.entity.Role;
import kz.tlegen.clinic.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;

@DataJpaTest
@Testcontainers
public class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_shouldPersistUser() {
        User savedUser = userRepository.save(new User(
                "alex@gmail.com",
                "Qwerty123",
                Role.ADMIN,
                true
        ));

        Optional<User> result = userRepository.findById(savedUser.getId());
        assertTrue(result.isPresent());
        User foundUser = result.get();
        assertNotNull(savedUser.getId());
        assertEquals(savedUser.getId(), foundUser.getId());
        assertEquals(savedUser.getEmail(), foundUser.getEmail());
        assertEquals(savedUser.getPassword(), foundUser.getPassword());
        assertEquals(savedUser.getRole(), foundUser.getRole());
        assertEquals(savedUser.isActive(), foundUser.isActive());
    }

    @Test
    void findByEmail_shouldReturnUser() {
        User user = userRepository.save(new User(
                "alex@gmail.com",
                "Qwerty123",
                Role.ADMIN,
                true
        ));
        Optional<User> result = userRepository.findByEmail(user.getEmail());
        assertTrue(result.isPresent());
        User foundUser = result.get();
        assertNotNull(foundUser.getId());
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getEmail(), foundUser.getEmail());
        assertEquals(user.getPassword(), foundUser.getPassword());
        assertEquals(user.getRole(), foundUser.getRole());
        assertEquals(user.isActive(), foundUser.isActive());
    }

    @Test
    void existsByEmail_shouldReturnTrueWhenEmailExists() {
        User user = userRepository.save(new User(
                "alex@gmail.com",
                "Qwerty123",
                Role.ADMIN,
                true)
        );
        assertTrue(userRepository.existsByEmail(user.getEmail()));
    }

    @Test
    void existsByEmail_shouldReturnFalseWhenEmailDoesNotExists() {
        assertFalse(userRepository.existsByEmail("unknown@gmail.com"));
    }

    @Test
    void existsByEmailAndIdNot_shouldReturnFalseForSameUser() {
        User user = userRepository.save(new User(
                "alex@gmail.com",
                "Qwerty123",
                Role.ADMIN,
                true)
        );
        assertFalse(userRepository.existsByEmailAndIdNot(user.getEmail(), user.getId()));
    }

    @Test
    void existsByEmailAndIdNot_shouldReturnTrueWhenAnotherUserHasEmail() {
        User user = userRepository.save(new User(
                "alex@gmail.com",
                "Qwerty123",
                Role.ADMIN,
                true)
        );
        assertTrue(userRepository.existsByEmailAndIdNot(user.getEmail(), 999L));
    }

    @Test
    void delete_shouldRemoveUser() {
        User user = userRepository.save(new User(
                "alex@gmail.com",
                "Qwerty123",
                Role.ADMIN,
                true
        ));

        Long userId = user.getId();

        assertTrue(userRepository.existsById(userId));

        userRepository.delete(user);

        assertFalse(userRepository.existsById(userId));
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        User firstUser = userRepository.save(new User(
                "alex@gmail.com",
                "Qwerty123",
                Role.ADMIN,
                true));
        User secondUser = userRepository.save(new User(
                "maksim@gmail.com",
                "Qwerty12s3",
                Role.DOCTOR,
                true
        ));

        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());
    }

    @Test
    void findByEmail_shouldReturnEmptyWhenEmailDoesNotExist() {
        assertTrue(userRepository.findByEmail("unknown").isEmpty());
    }

}
