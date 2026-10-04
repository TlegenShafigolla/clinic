package kz.tlegen.clinic.security;

import kz.tlegen.clinic.entity.*;
import kz.tlegen.clinic.exception.DoctorNotFoundException;
import kz.tlegen.clinic.exception.PatientNotFoundException;
import kz.tlegen.clinic.repository.DoctorRepository;
import kz.tlegen.clinic.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentProfileServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private CurrentProfileService currentProfileService;

    @Test
    void getCurrentPatient_shouldReturnPatient_whenProfileExists() {
        User user = new User(
                "alex@gmail.com",
                "Qwerty123",
                Role.PATIENT,
                true
        );

        Patient patient = new Patient(
                "Maria",
                "Kiss",
                LocalDate.of(2005, 6, 14),
                "+77001267569",
                true
        );
        ReflectionTestUtils.setField(user, "id", 1L);

        when(patientRepository.findByUserId(1L))
                .thenReturn(Optional.of(patient));

        Patient result = currentProfileService.getCurrentPatient(user);

        assertEquals(patient, result);
        verify(patientRepository).findByUserId(1L);
    }

    @Test
    void getCurrentPatient_shouldThrowPatientNotFoundException_whenProfileDoesNotExist() {
        User user = new User(
                "alex@gmail.com",
                "Qwerty123",
                Role.PATIENT,
                true
        );

        ReflectionTestUtils.setField(user, "id", 1L);
        when(patientRepository.findByUserId(1L))
                .thenReturn(Optional.empty());
        PatientNotFoundException exception = assertThrows(
                PatientNotFoundException.class,
                () -> currentProfileService.getCurrentPatient(user)
        );
        assertEquals(
                "Patient profile not found for current user",
                exception.getMessage()
        );
        verify(patientRepository).findByUserId(1L);
    }

    @Test
    void getCurrentDoctor_shouldReturnDoctor_whenProfileExists() {
        User user = new User(
                "alex@gmail.com",
                "Qwerty123",
                Role.DOCTOR,
                true
        );

        Specialization specialization =
                new Specialization("Cardiology");

        Doctor doctor =
                new Doctor("Alex",
                        "Smith",
                        5,
                        true,
                        specialization);
        ReflectionTestUtils.setField(user, "id", 1L);
        when(doctorRepository.findByUserId(1L))
                .thenReturn(Optional.of(doctor));
        Doctor result = currentProfileService.getCurrentDoctor(user);
        assertEquals(doctor, result);
        verify(doctorRepository).findByUserId(1L);
    }

    @Test
    void getCurrentDoctor_shouldThrowDoctorNotFoundException_whenProfileDoesNotExist() {
        User user = new User(
                "alex@gmail.com",
                "Qwerty123",
                Role.DOCTOR,
                true
        );

        ReflectionTestUtils.setField(user, "id", 1L);
        when(doctorRepository.findByUserId(1L))
                .thenReturn(Optional.empty());
        DoctorNotFoundException exception = assertThrows(
                DoctorNotFoundException.class,
                () -> currentProfileService.getCurrentDoctor(user)
        );
        assertEquals(
                "Doctor profile not found for current user",
                exception.getMessage()
        );
        verify(doctorRepository).findByUserId(1L);
    }
}
