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
    void findAllByPatientIdAndStatusOrderByAppointmentDateTimeAsc_shouldReturnMatchingAppointmentsSorted() {
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

        Patient patientA = patientRepository.save(
                new Patient(
                        "Arman",
                        "Tsarukian",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                )
        );

        Patient patientB = patientRepository.save(
                new Patient(
                        "John",
                        "Doe",
                        LocalDate.of(1995, 3, 15),
                        "+77009999999",
                        true
                )
        );

        Appointment firstAppointment = new Appointment(
                doctor,
                patientA,
                LocalDateTime.of(2026, 11, 10, 15, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );
        Appointment secondAppointment = new Appointment(
                doctor,
                patientA,
                LocalDateTime.of(2026, 11, 10, 12, 0),
                AppointmentStatus.COMPLETED,
                "Checking"
        );
        Appointment thirdAppointment = new Appointment(
                doctor,
                patientA,
                LocalDateTime.of(2026, 11, 10, 9, 0),
                AppointmentStatus.SCHEDULED,
                "Checking"
        );
        Appointment fourthAppointment = new Appointment(
                doctor,
                patientB,
                LocalDateTime.of(2026, 11, 10, 8, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );

        appointmentRepository.save(firstAppointment);
        appointmentRepository.save(secondAppointment);
        appointmentRepository.save(thirdAppointment);
        appointmentRepository.save(fourthAppointment);
        List<Appointment> result =
                appointmentRepository.findAllByPatientIdAndStatusOrderByAppointmentDateTimeAsc(
                        patientA.getId(),
                        AppointmentStatus.SCHEDULED
                );
        assertEquals(2, result.size());
        assertTrue(
                result.stream()
                        .allMatch(a ->
                                a.getPatient().getId().equals(patientA.getId())
                                        && a.getStatus() == AppointmentStatus.SCHEDULED
                        )
        );
        assertEquals(
                LocalDateTime.of(2026, 11, 10, 9, 0),
                result.get(0).getAppointmentDateTime()
        );

        assertEquals(
                LocalDateTime.of(2026, 11, 10, 15, 0),
                result.get(1).getAppointmentDateTime()
        );
    }

    @Test
    void findAllByDoctorIdAndStatusOrderByAppointmentDateTimeAsc_shouldReturnMatchingAppointmentsSorted() {
        Specialization specialization =
                specializationRepository.save(
                        new Specialization("Cardiology")
                );

        Doctor doctorA = doctorRepository.save(
                new Doctor(
                        "Alex",
                        "Smith",
                        5,
                        true,
                        specialization
                )
        );
        Doctor doctorB = doctorRepository.save(
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
                doctorA,
                patient,
                LocalDateTime.of(2026, 11, 10, 15, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );
        Appointment secondAppointment = new Appointment(
                doctorA,
                patient,
                LocalDateTime.of(2026, 11, 10, 12, 0),
                AppointmentStatus.COMPLETED,
                "Checking"
        );
        Appointment thirdAppointment = new Appointment(
                doctorA,
                patient,
                LocalDateTime.of(2026, 11, 10, 9, 0),
                AppointmentStatus.SCHEDULED,
                "Checking"
        );
        Appointment fourthAppointment = new Appointment(
                doctorB,
                patient,
                LocalDateTime.of(2026, 11, 10, 8, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );
        appointmentRepository.save(firstAppointment);
        appointmentRepository.save(secondAppointment);
        appointmentRepository.save(thirdAppointment);
        appointmentRepository.save(fourthAppointment);

        List<Appointment> result =
                appointmentRepository.findAllByDoctorIdAndStatusOrderByAppointmentDateTimeAsc(
                        doctorA.getId(),
                        AppointmentStatus.SCHEDULED
                );
        assertEquals(2, result.size());
        assertTrue(
                result.stream()
                        .allMatch(a ->
                                a.getDoctor().getId().equals(doctorA.getId())
                                        && a.getStatus() == AppointmentStatus.SCHEDULED
                        )
        );
        assertEquals(
                LocalDateTime.of(2026, 11, 10, 9, 0),
                result.get(0).getAppointmentDateTime()
        );

        assertEquals(
                LocalDateTime.of(2026, 11, 10, 15, 0),
                result.get(1).getAppointmentDateTime()
        );
    }

    @Test
    void findAllByStatus_shouldReturnOnlyMatchingAppointments() {
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
        Appointment thirdAppointment = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 9, 26, 12, 0),
                AppointmentStatus.SCHEDULED,
                "Checking"
        );
        Appointment fourthAppointment = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 9, 27, 15, 0),
                AppointmentStatus.CANCELLED,
                "Consultation"
        );
        appointmentRepository.save(firstAppointment);
        appointmentRepository.save(secondAppointment);
        appointmentRepository.save(thirdAppointment);
        appointmentRepository.save(fourthAppointment);
        List<Appointment> result =
                appointmentRepository.findAllByStatusOrderByAppointmentDateTimeAsc(
                        AppointmentStatus.SCHEDULED);
        assertEquals(2, result.size());
        assertTrue(
                result.stream()
                        .allMatch(a ->
                                a.getStatus() == AppointmentStatus.SCHEDULED
                        )
        );
    }

    @Test
    void findAllByStatusOrderByAppointmentDateTimeAsc_shouldReturnMatchingAppointmentsSorted() {
        Specialization specialization =
                specializationRepository.save(
                        new Specialization("Cardiology"));
        Doctor doctor = doctorRepository.save(
                new Doctor(
                        "Alex",
                        "Smith",
                        5,
                        true,
                        specialization
                ));
        Patient patient = patientRepository.save(
                new Patient(
                        "Arman",
                        "Tsarukian",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                ));
        Appointment firstAppointment = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 11, 10, 15, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );
        Appointment secondAppointment = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 11, 10, 12, 0),
                AppointmentStatus.COMPLETED,
                "Checking"
        );
        Appointment thirdAppointment = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 11, 10, 9, 0),
                AppointmentStatus.SCHEDULED,
                "Checking"
        );
        Appointment fourthAppointment = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 9, 10, 8, 0),
                AppointmentStatus.CANCELLED,
                "Consultation"
        );
        appointmentRepository.save(firstAppointment);
        appointmentRepository.save(secondAppointment);
        appointmentRepository.save(thirdAppointment);
        appointmentRepository.save(fourthAppointment);
        List<Appointment> result =
                appointmentRepository.findAllByStatusOrderByAppointmentDateTimeAsc(
                                AppointmentStatus.SCHEDULED
                        );
        assertEquals(2, result.size());

        assertTrue(
                result.stream()
                        .allMatch(a ->
                                a.getStatus() == AppointmentStatus.SCHEDULED
                        )
        );

        assertEquals(
                LocalDateTime.of(2026, 11, 10, 9, 0),
                result.get(0).getAppointmentDateTime()
        );

        assertEquals(
                LocalDateTime.of(2026, 11, 10, 15, 0),
                result.get(1).getAppointmentDateTime()
        );
    }

    @Test
    void findAllByOrderByAppointmentDateTimeAsc_shouldReturnAllAppointmentsSorted() {
        Specialization specialization =
                specializationRepository.save(
                        new Specialization("Cardiology"));
        Doctor doctor = doctorRepository.save(
                new Doctor(
                        "Alex",
                        "Smith",
                        5,
                        true,
                        specialization
                ));
        Patient patient = patientRepository.save(
                new Patient(
                        "Arman",
                        "Tsarukian",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                ));
        Appointment firstAppointment = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 11, 10, 15, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );
        Appointment secondAppointment = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 11, 10, 12, 0),
                AppointmentStatus.COMPLETED,
                "Checking"
        );
        Appointment thirdAppointment = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 11, 10, 9, 0),
                AppointmentStatus.SCHEDULED,
                "Checking"
        );

        appointmentRepository.save(firstAppointment);
        appointmentRepository.save(secondAppointment);
        appointmentRepository.save(thirdAppointment);

        List<Appointment> result =
                appointmentRepository.findAllByOrderByAppointmentDateTimeAsc();

        assertEquals(3, result.size());

        assertEquals(
                LocalDateTime.of(2026, 11, 10, 9, 0),
                result.get(0).getAppointmentDateTime()
        );

        assertEquals(
                LocalDateTime.of(2026, 11, 10, 12, 0),
                result.get(1).getAppointmentDateTime()
        );

        assertEquals(
                LocalDateTime.of(2026, 11, 10, 15, 0),
                result.get(2).getAppointmentDateTime()
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

    @Test
    void existsByDoctorIdAndAppointmentDateTimeAndIdNot_shouldReturnFalseForSameAppointment() {
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
        Appointment saved =
                appointmentRepository.save(appointment);
        boolean exists = appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndIdNot(
                doctor.getId(),
                saved.getAppointmentDateTime(),
                saved.getId()
        );
        assertFalse(exists);
    }

    @Test
    void existsByDoctorIdAndAppointmentDateTimeAndIdNot_shouldReturnTrueForAnotherAppointment() {
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
                LocalDateTime.of(2026, 9, 25, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );
        Appointment firstSavedAppointment =
                appointmentRepository.save(firstAppointment);
        Appointment secondSavedAppointment =
                appointmentRepository.save(secondAppointment);
        boolean exists = appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndIdNot(
                doctor.getId(),
                secondSavedAppointment.getAppointmentDateTime(),
                secondSavedAppointment.getId()
        );
        assertTrue(exists);
    }

    @Test
    void findAllByPatientId_shouldReturnOnlyPatientAppointments() {
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

        Patient patientA = patientRepository.save(
                new Patient(
                        "Arman",
                        "Tsarukian",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                )
        );

        Patient patientB = patientRepository.save(
                new Patient(
                        "John",
                        "Doe",
                        LocalDate.of(1995, 3, 15),
                        "+77009999999",
                        true
                )
        );

        Appointment firstAppointment = new Appointment(
                doctor,
                patientA,
                LocalDateTime.of(2026, 9, 25, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );

        Appointment secondAppointment = new Appointment(
                doctor,
                patientA,
                LocalDateTime.of(2026, 9, 26, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );

        Appointment thirdAppointment = new Appointment(
                doctor,
                patientB,
                LocalDateTime.of(2026, 9, 27, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );
        appointmentRepository.save(firstAppointment);
        appointmentRepository.save(secondAppointment);
        appointmentRepository.save(thirdAppointment);
        List<Appointment> result =
                appointmentRepository.findAllByPatientIdOrderByAppointmentDateTimeAsc(patientA.getId());

        assertEquals(2, result.size());
        assertTrue(
                result.stream()
                        .allMatch(a ->
                                a.getPatient().getId().equals(patientA.getId())
                        )
        );
    }

    @Test
    void findAllByDoctorId_shouldReturnOnlyDoctorAppointments() {
        Specialization specialization =
                specializationRepository.save(
                        new Specialization("Cardiology")
                );

        Doctor doctorA = doctorRepository.save(
                new Doctor(
                        "Alex",
                        "Smith",
                        5,
                        true,
                        specialization
                )
        );

        Doctor doctorB = doctorRepository.save(
                new Doctor(
                        "Marat",
                        "Oralgazin",
                        7,
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
                doctorA,
                patient,
                LocalDateTime.of(2026, 9, 25, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );

        Appointment secondAppointment = new Appointment(
                doctorA,
                patient,
                LocalDateTime.of(2026, 9, 26, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );

        Appointment thirdAppointment = new Appointment(
                doctorB,
                patient,
                LocalDateTime.of(2026, 9, 27, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );
        appointmentRepository.save(firstAppointment);
        appointmentRepository.save(secondAppointment);
        appointmentRepository.save(thirdAppointment);
        List<Appointment> result =
                appointmentRepository.findAllByDoctorIdOrderByAppointmentDateTimeAsc(doctorA.getId());
        assertEquals(2, result.size());
        assertTrue(
                result.stream()
                        .allMatch(a ->
                                a.getDoctor().getId().equals(doctorA.getId())
                        )
        );
    }

    @Test
    void findAllByPatientIdOrderByAppointmentDateTimeAsc_shouldReturnAppointmentsSortedByDateTime() {
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
        Appointment appointmentAt15 = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 11, 10, 15, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );

        Appointment appointmentAt09 = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 11, 10, 9, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );

        Appointment appointmentAt12 = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 11, 10, 12, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );
        appointmentRepository.save(appointmentAt15);
        appointmentRepository.save(appointmentAt09);
        appointmentRepository.save(appointmentAt12);
        List<Appointment> result =
                appointmentRepository
                        .findAllByPatientIdOrderByAppointmentDateTimeAsc(
                                patient.getId()
                        );
        assertEquals(3, result.size());
        assertEquals(
                LocalDateTime.of(2026, 11, 10, 9, 0),
                result.get(0).getAppointmentDateTime()
        );

        assertEquals(
                LocalDateTime.of(2026, 11, 10, 12, 0),
                result.get(1).getAppointmentDateTime()
        );

        assertEquals(
                LocalDateTime.of(2026, 11, 10, 15, 0),
                result.get(2).getAppointmentDateTime()
        );
    }

    @Test
    void findAllByDoctorIdOrderByAppointmentDateTimeAsc_shouldReturnAppointmentsSortedByDateTime() {
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
                ));
        Patient patient = patientRepository.save(
                new Patient(
                        "Arman",
                        "Tsarukian",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                ));
        Appointment appointmentAt15 = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 11, 10, 15, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );

        Appointment appointmentAt09 = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 11, 10, 9, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );

        Appointment appointmentAt12 = new Appointment(
                doctor,
                patient,
                LocalDateTime.of(2026, 11, 10, 12, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );
        appointmentRepository.save(appointmentAt15);
        appointmentRepository.save(appointmentAt09);
        appointmentRepository.save(appointmentAt12);

        List<Appointment> result =
                appointmentRepository
                        .findAllByDoctorIdOrderByAppointmentDateTimeAsc(
                                doctor.getId()
                        );
        assertEquals(3, result.size());

        assertEquals(
                LocalDateTime.of(2026, 11, 10, 9, 0),
                result.get(0).getAppointmentDateTime()
        );

        assertEquals(
                LocalDateTime.of(2026, 11, 10, 12, 0),
                result.get(1).getAppointmentDateTime()
        );

        assertEquals(
                LocalDateTime.of(2026, 11, 10, 15, 0),
                result.get(2).getAppointmentDateTime()
        );
    }
}
