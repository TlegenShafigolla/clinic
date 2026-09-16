package kz.tlegen.clinic.repository;

import kz.tlegen.clinic.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
public class AppointmentRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private SpecializationRepository specializationRepository;

    @Test
    void saveAndFindById_shouldReturnAppointment() {
        Specialization specialization =
                specializationRepository.save(
                        new Specialization("Cardiology")
                );

        Doctor doctor = doctorRepository.save(
                new Doctor(
                        "Alex",
                        "Smith",
                        5,
                        true,
                        specialization
                )
        );

        Patient patient = patientRepository.save(
                new Patient(
                        "Arman",
                        "Tsarukian",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                )
        );

        Appointment appointment = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 9, 25, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );
        Appointment saved = appointmentRepository.save(appointment);

        Optional<Appointment> result = appointmentRepository.findById(saved.getId());

        assertTrue(result.isPresent());

        Appointment found = result.get();

        assertEquals(saved.getId(), found.getId());
        assertEquals(doctor.getId(), found.getDoctor().getId());
        assertEquals(patient.getId(), found.getPatient().getId());
        assertEquals(
                LocalDateTime.of(2026, 9, 25, 10, 0),
                found.getAppointmentDateTime()
        );
        assertEquals(AppointmentStatus.SCHEDULED, found.getStatus());
        assertEquals("Consultation", found.getReason());
    }

    @Test
    void delete_shouldRemoveAppointment() {
        Specialization specialization =
                specializationRepository.save(
                        new Specialization("Cardiology")
                );

        Doctor doctor = doctorRepository.save(
                new Doctor(
                        "Alex",
                        "Smith",
                        5,
                        true,
                        specialization
                )
        );

        Patient patient = patientRepository.save(
                new Patient(
                        "Arman",
                        "Tsarukian",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                )
        );

        Appointment appointment = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 9, 25, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );

        Appointment saved = appointmentRepository.save(appointment);
        appointmentRepository.delete(saved);
        Optional<Appointment> result = appointmentRepository.findById(saved.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllAppointments() {
        Specialization specialization =
                specializationRepository.save(
                        new Specialization("Cardiology")
                );

        Doctor doctor = doctorRepository.save(
                new Doctor(
                        "Alex",
                        "Smith",
                        5,
                        true,
                        specialization
                )
        );

        Patient patient = patientRepository.save(
                new Patient(
                        "Arman",
                        "Tsarukian",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                )
        );

        Appointment firstAppointment = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 9, 25, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );
        Appointment secondAppointment = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 9, 26, 12, 0),
                AppointmentStatus.COMPLETED,
                "Checking"
        );

        appointmentRepository.save(firstAppointment);
        appointmentRepository.save(secondAppointment);

        List<Appointment> appointments = appointmentRepository.findAll();

        assertEquals(2, appointments.size());
        assertTrue(appointments.stream()
                .anyMatch(a -> a.getReason().equals("Consultation")));
        assertTrue(
                appointments.stream()
                        .anyMatch(a -> a.getReason().equals("Checking"))
        );
    }

    @Test
    void existsByDoctorIdAndAppointmentDateTime_shouldReturnTrue() {
        Specialization specialization =
                specializationRepository.save(
                        new Specialization("Cardiology")
                );

        Doctor doctor = doctorRepository.save(
                new Doctor(
                        "Alex",
                        "Smith",
                        5,
                        true,
                        specialization
                )
        );

        Patient patient = patientRepository.save(
                new Patient(
                        "Arman",
                        "Tsarukian",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                )
        );

        Appointment appointment = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 9, 25, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );
        appointmentRepository.save(appointment);
        boolean exists =
                appointmentRepository.existsByDoctorIdAndAppointmentDateTime(
                        doctor.getId(),
                        LocalDateTime.of(2026, 9, 25, 10, 0)
                );

        assertTrue(exists);
    }

    @Test
    void existsByDoctorIdAndAppointmentDateTime_shouldReturnFalse() {
        Specialization specialization =
                specializationRepository.save(
                        new Specialization("Cardiology")
                );

        Doctor doctor = doctorRepository.save(
                new Doctor(
                        "Alex",
                        "Smith",
                        5,
                        true,
                        specialization
                )
        );

        Patient patient = patientRepository.save(
                new Patient(
                        "Arman",
                        "Tsarukian",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                )
        );

        Appointment appointment = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 9, 25, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );
        appointmentRepository.save(appointment);

        boolean exists = appointmentRepository.existsByDoctorIdAndAppointmentDateTime(
                doctor.getId(),
                LocalDateTime.of(2026, 9, 25, 11, 0)
        );

        assertFalse(exists);
    }
}
