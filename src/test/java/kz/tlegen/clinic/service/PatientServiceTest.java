package kz.tlegen.clinic.service;

import kz.tlegen.clinic.dto.patient.PatientRequest;
import kz.tlegen.clinic.dto.patient.PatientResponse;
import kz.tlegen.clinic.entity.Patient;
import kz.tlegen.clinic.exception.PatientNotFoundException;
import kz.tlegen.clinic.exception.SpecializationNotFoundException;
import kz.tlegen.clinic.mapper.PatientMapper;
import kz.tlegen.clinic.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper mapper;

    @InjectMocks
    private PatientService patientService;

    @Test
    void create_shouldReturnResponse() {
        PatientRequest request =
                new PatientRequest(
                        "Alex",
                        "Smith",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                );

        Patient patient =
                new Patient(
                        "Alex",
                        "Smith",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                );

        PatientResponse expectedResponse =
                new PatientResponse(
                        1L,
                        "Alex",
                        "Smith",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                );

        when(mapper.toEntity(request)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(mapper.toResponse(patient)).thenReturn(expectedResponse);

        PatientResponse actualResponse = patientService.create(request);

        assertEquals(1L, actualResponse.getId());
        assertEquals("Alex", actualResponse.getFirstName());
    }

    @Test
    void findById_shouldReturnResponse_whenPatientExists() {
        Patient patient =
                new Patient(
                        "Alex",
                        "Smith",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                );
        PatientResponse expectedResponse =
                new PatientResponse(
                        1L,
                        "Alex",
                        "Smith",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                );

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(mapper.toResponse(patient)).thenReturn(expectedResponse);
        PatientResponse actualResponse = patientService.findById(1L);

        assertEquals(1L, actualResponse.getId());
        assertEquals("Alex", actualResponse.getFirstName());
    }

    @Test
    void findById_shouldThrowException_whenPatientDoesNotExist() {
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());
        PatientNotFoundException exception =
                assertThrows(
                        PatientNotFoundException.class,
                        () -> patientService.findById(999L));

        assertEquals("Patient not found with id: 999", exception.getMessage());
    }

    @Test
    void findAll_shouldReturnAllPatients() {
        Patient Alex =
                new Patient(
                        "Alex",
                        "Smith",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                );

        Patient Maria =
                new Patient(
                        "Maria",
                        "Kiss",
                        LocalDate.of(2005, 6, 14),
                        "+77001267569",
                        true
                );

        PatientResponse expectedFirstResponse =
                new PatientResponse(1L,
                        "Alex",
                        "Smith",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true);

        PatientResponse expectedSecondResponse =
                new PatientResponse(2L,
                        "Maria",
                        "Kiss",
                        LocalDate.of(2005, 6, 14),
                        "+77001267569",
                        true);

        when(patientRepository.findAll()).thenReturn(List.of(Alex, Maria));
        when(mapper.toResponse(Alex)).thenReturn(expectedFirstResponse);
        when(mapper.toResponse(Maria)).thenReturn(expectedSecondResponse);
        List<PatientResponse> actualResponse = patientService.findAll();
        assertEquals(2, actualResponse.size());
        assertEquals(1L, actualResponse.get(0).getId());
        assertEquals("Alex", actualResponse.get(0).getFirstName());
        assertEquals(2L, actualResponse.get(1).getId());
        assertEquals("Maria", actualResponse.get(1).getFirstName());
    }

    @Test
    void update_shouldReturnUpdatedPatient_whenPatientExists() {
        PatientRequest request =
                new PatientRequest("Alex",
                        "Brown",
                        LocalDate.of(2000, 5, 10),
                        "+77009999999",
                        false);

        Patient patient = new Patient("Alex",
                "Smith",
                LocalDate.of(2000, 5, 10),
                "+77001234567",
                true);

        PatientResponse expectedResponse = new PatientResponse(1L,
                "Alex",
                "Brown",
                LocalDate.of(2000, 5, 10),
                "+77009999999",
                false);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(patient)).thenReturn(patient);
        when(mapper.toResponse(patient)).thenReturn(expectedResponse);

        PatientResponse actualResponse = patientService.update(1L, request);

        assertEquals(1L, actualResponse.getId());
        assertEquals("Brown", actualResponse.getLastName());
        assertEquals("+77009999999", actualResponse.getPhone());
        assertFalse(actualResponse.isActive());
        verify(patientRepository).save(patient);
    }

    @Test
    void update_shouldThrowException_whenPatientDoesNotExist() {
        PatientRequest request =
                new PatientRequest("Alex",
                        "Brown",
                        LocalDate.of(2000, 5, 10),
                        "+77009999999",
                        false);

        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        PatientNotFoundException exception =
                assertThrows(
                        PatientNotFoundException.class,
                        () -> patientService.update(999L, request));

        assertEquals("Patient not found with id: 999", exception.getMessage());

        verify(patientRepository, never()).save(any());

    }

    @Test
    void delete_shouldDeletePatient_whenPatientExists() {
        Patient patient = new Patient("Alex",
                "Smith",
                LocalDate.of(2000, 5, 10),
                "+77001234567",
                true);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        patientService.delete(1L);
        verify(patientRepository).delete(patient);
    }

    @Test
    void delete_shouldThrowException_whenPatientDoesNotExist() {
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        PatientNotFoundException exception =
                assertThrows(
                        PatientNotFoundException.class,
                        () -> patientService.delete(999L));

        assertEquals("Patient not found with id: 999", exception.getMessage());
        verify(patientRepository, never()).delete(any());
    }
}
