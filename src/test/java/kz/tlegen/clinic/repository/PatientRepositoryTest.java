package kz.tlegen.clinic.repository;

import kz.tlegen.clinic.entity.Patient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
public class PatientRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void saveAndFindById_shouldReturnPatient() {
        Patient patient = new Patient(
                "Alex",
                "Smith",
                LocalDate.of(2000, 5, 10),
                "+77001234567",
                true
        );

        Patient savedPatient = patientRepository.save(patient);

        Optional<Patient> result = patientRepository.findById(savedPatient.getId());
        assertTrue(result.isPresent());
        assertEquals("Alex", result.get().getFirstName());
        assertEquals("+77001234567", result.get().getPhone());
    }

    @Test
    void delete_shouldRemovePatient() {
        Patient patient = new Patient(
                "Alex",
                "Smith",
                LocalDate.of(2000, 5, 10),
                "+77001234567",
                true
        );

        Patient savedPatient = patientRepository.save(patient);
        patientRepository.delete(savedPatient);
        Optional<Patient> result = patientRepository.findById(savedPatient.getId());
        assertTrue(result.isEmpty());
    }

        @Test
        void findAll_shouldReturnAllPatients() {
            Patient alex =
                    new Patient(
                            "Alex",
                            "Smith",
                            LocalDate.of(2000, 5, 10),
                            "+77001234567",
                            true
                    );

            Patient maria =
                    new Patient(
                            "Maria",
                            "Kiss",
                            LocalDate.of(2005, 6, 14),
                            "+77001267569",
                            true
                    );
            Patient savedAlexPatient = patientRepository.save(alex);
            Patient savedMariaPatient = patientRepository.save(maria);
            List<Patient> patients = patientRepository.findAll();

            assertEquals(2, patients.size());
            assertTrue(
                    patients.stream()
                            .anyMatch(patient -> patient.getFirstName().equals("Alex"))
            );

            assertTrue(
                    patients.stream()
                            .anyMatch(patient -> patient.getFirstName().equals("Maria"))
            );
        }
}
