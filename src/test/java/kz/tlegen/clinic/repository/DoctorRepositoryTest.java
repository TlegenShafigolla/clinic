package kz.tlegen.clinic.repository;

import kz.tlegen.clinic.entity.Doctor;
import kz.tlegen.clinic.entity.Specialization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
class DoctorRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SpecializationRepository specializationRepository;


    @Test
    void findAllBySpecializationIdAndActiveTrue_shouldReturnOnlyActiveDoctorsFromSpecialization() {
        Specialization cardiology =
                specializationRepository.save(
                        new Specialization("Cardiology")
                );
        Specialization neurology =
                specializationRepository.save(
                        new Specialization("Neurology")
                );

        doctorRepository.save(
                new Doctor("Alex", "Smith", 5, true, cardiology)
        );

        doctorRepository.save(
                new Doctor("John", "Brown", 7, false, cardiology)
        );

        doctorRepository.save(
                new Doctor("Maria", "White", 4, true, neurology)
        );
        List<Doctor> result =
                doctorRepository.findAllBySpecializationIdAndActiveTrue(
                        cardiology.getId()
                );

        assertEquals(1, result.size());
        assertEquals("Alex", result.get(0).getFirstName());
    }

    @Test
    void findAllBySpecializationIdAndActiveTrue_shouldReturnEmptyList_whenNoActiveDoctorsExist() {
        Specialization cardiology =
                specializationRepository.save(
                        new Specialization("Cardiology")
                );
        doctorRepository.save(
                new Doctor("Alex", "Smith", 5, false, cardiology)
        );

        doctorRepository.save(
                new Doctor("John", "Brown", 7, false, cardiology)
        );
        List<Doctor> result =
                doctorRepository.findAllBySpecializationIdAndActiveTrue(
                        cardiology.getId()
                );
        assertTrue(result.isEmpty());
    }
}