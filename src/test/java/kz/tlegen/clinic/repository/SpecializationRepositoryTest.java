package kz.tlegen.clinic.repository;

import kz.tlegen.clinic.entity.Specialization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@Testcontainers
public class SpecializationRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private SpecializationRepository repository;

    @Test
    void existsByName_shouldReturnTrue_whenSpecializationExists() {
        Specialization specialization = new Specialization("Cardiology");
        repository.save(specialization);
        boolean exists =
                repository.existsByName("Cardiology");

        assertTrue(exists);

    }

    @Test
    void existsByName_shouldReturnFalse_whenSpecializationDoesNotExist() {
        boolean exists =
                repository.existsByName("Neurology");
        assertFalse(exists);
    }

    @Test
    void existsByNameAndIdNot_shouldReturnTrue_whenNameBelongsToAnotherSpecialization() {
        repository.save(new Specialization("Neurology"));
        boolean exists =
                repository.existsByNameAndIdNot("Neurology", 999L);
        assertTrue(exists);
    }

    @Test
    void existsByNameAndIdNot_shouldReturnFalse_whenNameBelongsToSameSpecialization() {
        Specialization saved =
                repository.save(new Specialization("Neurology"));

        Long id = saved.getId();
        boolean exists =
                repository.existsByNameAndIdNot("Neurology", id);
        assertFalse(exists);
    }
}
