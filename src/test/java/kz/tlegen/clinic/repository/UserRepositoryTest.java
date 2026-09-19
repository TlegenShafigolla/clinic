package kz.tlegen.clinic.repository;


import kz.tlegen.clinic.entity.User;
import kz.tlegen.clinic.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@DataJpaTest
@Testcontainers
public class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;


//    @Test
//    void save_shouldPersistUser() throws Exception {
//        User user = userRepository.save(new User(
//
//        ));
//    }

}
