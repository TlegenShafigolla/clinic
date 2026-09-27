package kz.tlegen.clinic.security;

import kz.tlegen.clinic.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentAuthorizationServiceTest {


    private final AppointmentAuthorizationService authorizationService =
            new AppointmentAuthorizationService();

    @Test
    void validatePatientOwnsAppointment_shouldNotThrow_whenPatientOwnsAppointment() {
        Patient currentPatient = new Patient(
                "Alex",
                "Smith",
                LocalDate.of(2000, 5, 10),
                "+77001234567",
                true);
        Patient appointmentPatient = new Patient(
                "Maria",
                "Smith",
                LocalDate.of(2000, 5, 10),
                "+770012332467",
                true);

        Appointment appointment = new Appointment(
                null,
                appointmentPatient,
                LocalDateTime.of(2026, 10, 14, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );


        ReflectionTestUtils.setField(currentPatient, "id", 1L);
        ReflectionTestUtils.setField(appointmentPatient, "id", 1L);

        assertDoesNotThrow(() ->
                authorizationService.validatePatientOwnsAppointment(
                        currentPatient,
                        appointment,
                        "You cannot view another patient's appointment"
                )
        );
    }

    @Test
    void validatePatientOwnsAppointment_shouldThrow_whenPatientDoesNotOwnAppointment() {
        Patient currentPatient = new Patient(
                "Alex",
                "Smith",
                LocalDate.of(2000, 5, 10),
                "+77001234567",
                true);
        Patient appointmentPatient = new Patient(
                "Maria",
                "Smith",
                LocalDate.of(2000, 5, 10),
                "+770012332467",
                true);

        Appointment appointment = new Appointment(
                null,
                appointmentPatient,
                LocalDateTime.of(2026, 10, 14, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );


        ReflectionTestUtils.setField(currentPatient, "id", 1L);
        ReflectionTestUtils.setField(appointmentPatient, "id", 2L);

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> authorizationService.validatePatientOwnsAppointment(
                        currentPatient,
                        appointment,
                        "You cannot view another appointment"
                )
        );

        assertEquals(
                "You cannot view another appointment",
                exception.getMessage()
        );
    }

    @Test
    void validateDoctorOwnsAppointment_shouldNotThrow_whenDoctorOwnsAppointment() {
        Specialization specialization =
                new Specialization("Cardiology");

        Doctor currentDoctor =
                new Doctor("Alex",
                        "Smith",
                        5,
                        true,
                        specialization);

        Doctor appointmentDoctor =
                new Doctor("Alex",
                        "Smith",
                        5,
                        true,
                        specialization);

        Appointment appointment = new Appointment(
                appointmentDoctor,
                null,
                LocalDateTime.of(2026, 10, 14, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );


        ReflectionTestUtils.setField(currentDoctor, "id", 1L);
        ReflectionTestUtils.setField(appointmentDoctor, "id", 1L);

        assertDoesNotThrow(() ->
                authorizationService.validateDoctorOwnsAppointment(
                        currentDoctor,
                        appointment,
                        "You cannot view another doctor's appointment"
                )
        );
    }

    @Test
    void validateDoctorOwnsAppointment_shouldThrow_whenDoctorDoesNotOwnAppointment() {
        Specialization specialization =
                new Specialization("Cardiology");

        Doctor currentDoctor =
                new Doctor("Alex",
                        "Smith",
                        5,
                        true,
                        specialization);

        Doctor appointmentDoctor =
                new Doctor("Alex",
                        "Smith",
                        5,
                        true,
                        specialization);

        Appointment appointment = new Appointment(
                appointmentDoctor,
                null,
                LocalDateTime.of(2026, 10, 14, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );


        ReflectionTestUtils.setField(currentDoctor, "id", 1L);
        ReflectionTestUtils.setField(appointmentDoctor, "id", 2L);

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> authorizationService.validateDoctorOwnsAppointment(
                        currentDoctor,
                        appointment,
                        "You cannot view another doctor's appointment"
                )
        );

        assertEquals(
                "You cannot view another doctor's appointment",
                exception.getMessage()
        );
    }

    @Test
    void validatePatientRequestOwner_shouldNotThrow_whenIdsMatch() {
        Patient currentPatient = new Patient(
                "Alex",
                "Smith",
                LocalDate.of(2000, 5, 10),
                "+77001234567",
                true);

        ReflectionTestUtils.setField(currentPatient, "id", 1L);


        assertDoesNotThrow(() ->
                authorizationService.validatePatientRequestOwner(
                        currentPatient,
                        1L,
                        "Patient cannot change appointment owner"
                ));
    }

    @Test
    void validatePatientRequestOwner_shouldThrow_whenIdsDiffer() {
        Patient currentPatient = new Patient(
                "Alex",
                "Smith",
                LocalDate.of(2000, 5, 10),
                "+77001234567",
                true);

        ReflectionTestUtils.setField(currentPatient, "id", 1L);


        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> authorizationService.validatePatientRequestOwner(
                        currentPatient,
                        2L,
                        "Patient cannot change appointment owner"
                )
        );

        assertEquals(
                "Patient cannot change appointment owner",
                exception.getMessage()
        );
    }

    @Test
    void validateDoctorRequestOwner_shouldNotThrow_whenIdsMatch() {
        Specialization specialization =
                new Specialization("Cardiology");

        Doctor currentDoctor =
                new Doctor("Alex",
                        "Smith",
                        5,
                        true,
                        specialization);

        ReflectionTestUtils.setField(currentDoctor, "id", 1L);
        assertDoesNotThrow(() ->
                authorizationService.validateDoctorRequestOwner(
                        currentDoctor,
                        1L,
                        "Doctor cannot change appointment owner"
                ));
    }

    @Test
    void validateDoctorRequestOwner_shouldThrow_whenIdsDiffer() {
        Specialization specialization =
                new Specialization("Cardiology");

        Doctor currentDoctor = new Doctor("Alex",
                "Smith",
                5,
                true,
                specialization);
        ReflectionTestUtils.setField(currentDoctor, "id", 1L);
        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> authorizationService.validateDoctorRequestOwner(
                        currentDoctor,
                        2L,
                        "Doctor cannot change appointment owner"
                )
        );
        assertEquals(
                "Doctor cannot change appointment owner",
                exception.getMessage()
        );
    }
}
