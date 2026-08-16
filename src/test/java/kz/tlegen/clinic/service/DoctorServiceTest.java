package kz.tlegen.clinic.service;

import kz.tlegen.clinic.dto.doctor.DoctorRequest;
import kz.tlegen.clinic.dto.doctor.DoctorResponse;
import kz.tlegen.clinic.dto.specialization.SpecializationRequest;
import kz.tlegen.clinic.entity.Doctor;
import kz.tlegen.clinic.entity.Specialization;
import kz.tlegen.clinic.exception.DoctorNotFoundException;
import kz.tlegen.clinic.exception.SpecializationNotFoundException;
import kz.tlegen.clinic.mapper.DoctorMapper;
import kz.tlegen.clinic.repository.DoctorRepository;
import kz.tlegen.clinic.repository.SpecializationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {
    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private DoctorMapper mapper;

    @InjectMocks
    private DoctorService service;

    @Test
    void create_shouldReturnResponse_whenSpecializationExists() {
        DoctorRequest request =
                new DoctorRequest("Alex", "Smith", 5, true, 1L);

        Specialization specialization =
                new Specialization("Cardiology");

        Doctor doctor =
                new Doctor("Alex", "Smith", 5, true, specialization);

        DoctorResponse expectedResponse =
                new DoctorResponse(
                        1L,
                        "Alex",
                        "Smith",
                        5,
                        true,
                        1L,
                        "Cardiology"
                );

        when(specializationRepository.findById(request.getSpecializationId()))
                .thenReturn(Optional.of(specialization));
        when(mapper.toEntity(request, specialization))
                .thenReturn(doctor);
        when(doctorRepository.save(doctor))
                .thenReturn(doctor);
        when(mapper.toResponse(doctor))
                .thenReturn(expectedResponse);
        DoctorResponse actualResponse = service.create(request);

        assertEquals(1L, actualResponse.getId());
        assertEquals("Alex", actualResponse.getFirstName());
        assertEquals("Smith", actualResponse.getLastName());
        assertEquals(5, actualResponse.getExperienceYears());
        assertTrue(actualResponse.isActive());
        assertEquals(1L, actualResponse.getSpecializationId());
        assertEquals("Cardiology", actualResponse.getSpecializationName());

    }

    @Test
    void create_shouldThrowException_whenSpecializationDoesNotExist() {
        DoctorRequest request =
                new DoctorRequest("Alex", "Smith", 5, true, 999L);

        when(specializationRepository.findById(999L))
                .thenReturn(Optional.empty());

        SpecializationNotFoundException exception =
                assertThrows(SpecializationNotFoundException.class,
                        () -> service.create(request)
                );
        assertEquals(
                "Specialization not found with id: 999",
                exception.getMessage()
        );

        verify(doctorRepository, never()).save(any());
    }

    @Test
    void findById_shouldReturnResponse_whenDoctorExists() {
        Specialization specialization =
                new Specialization("Cardiology");

        Doctor doctor =
                new Doctor("Alex", "Smith", 5, true, specialization);

        DoctorResponse expectedResponse =
                new DoctorResponse(
                        1L,
                        "Alex",
                        "Smith",
                        5,
                        true,
                        1L,
                        "Cardiology"
                );

        when(doctorRepository.findById(1L))
                .thenReturn(Optional.of(doctor));

        when(mapper.toResponse(doctor))
                .thenReturn(expectedResponse);
        DoctorResponse actualResponse = service.findById(1L);
        assertEquals(1L, actualResponse.getId());
        assertEquals("Alex", actualResponse.getFirstName());
        assertEquals("Smith", actualResponse.getLastName());
        assertEquals(5, actualResponse.getExperienceYears());
        assertTrue(actualResponse.isActive());
        assertEquals(1L, actualResponse.getSpecializationId());
        assertEquals("Cardiology", actualResponse.getSpecializationName());
    }

    @Test
    void findById_shouldThrowException_whenDoctorDoesNotExist() {
        when(doctorRepository.findById(999L))
                .thenReturn(Optional.empty());
        DoctorNotFoundException exception =
                assertThrows(DoctorNotFoundException.class,
                        () -> service.findById(999L));

        assertEquals("Doctor not found with id: 999", exception.getMessage());
    }

    @Test
    void findAll_shouldReturnAllDoctors() {
        Specialization specialization =
                new Specialization("Cardiology");
        Doctor firstDoctor =
                new Doctor("Alex", "Smith", 5, true, specialization);
        Doctor secondDoctor =
                new Doctor("Maria", "Askar", 5, true, specialization);
        DoctorResponse firstDoctorResponse = new DoctorResponse(
                1L,
                "Alex",
                "Smith",
                5,
                true,
                1L,
                "Cardiology");
        DoctorResponse secondDoctorResponse = new DoctorResponse(
                2L,
                "Maria",
                "Askar",
                5,
                true,
                1L,
                "Cardiology");
        when(doctorRepository.findAll())
                .thenReturn(List.of(firstDoctor, secondDoctor));
        when(mapper.toResponse(firstDoctor))
                .thenReturn(firstDoctorResponse);

        when(mapper.toResponse(secondDoctor))
                .thenReturn(secondDoctorResponse);

        List<DoctorResponse> result = service.findAll();
        assertEquals(2, result.size());
        assertEquals("Alex", result.get(0).getFirstName());
        assertEquals("Maria", result.get(1).getFirstName());
    }

    @Test
    void update_shouldReturnUpdatedDoctor_whenDoctorAndSpecializationExist() {
        DoctorRequest request =
                new DoctorRequest(
                        "Alex",
                        "Brown",
                        7,
                        true,
                        2L
                );

        Specialization oldSpecialization =
                new Specialization("Cardiology");

        Specialization newSpecialization =
                new Specialization("Neurology");

        Doctor doctor =
                new Doctor(
                        "Alex",
                        "Smith",
                        5,
                        true,
                        oldSpecialization
                );

        DoctorResponse expectedResponse =
                new DoctorResponse(
                        1L,
                        "Alex",
                        "Brown",
                        7,
                        true,
                        2L,
                        "Neurology"
                );

        when(doctorRepository.findById(1L))
                .thenReturn(Optional.of(doctor));

        when(specializationRepository.findById(2L))
                .thenReturn(Optional.of(newSpecialization));

        when(doctorRepository.save(doctor))
                .thenReturn(doctor);

        when(mapper.toResponse(doctor))
                .thenReturn(expectedResponse);

        DoctorResponse actualResponse =
                service.update(1L, request);

        assertEquals("Brown", actualResponse.getLastName());
        assertEquals(7, actualResponse.getExperienceYears());
        assertEquals(2L, actualResponse.getSpecializationId());
        assertEquals("Neurology", actualResponse.getSpecializationName());
    }

    @Test
    void update_shouldThrowException_whenDoctorDoesNotExist() {
        DoctorRequest request =
                new DoctorRequest(
                        "Alex",
                        "Brown",
                        7,
                        true,
                        2L
                );

        when(doctorRepository.findById(999L))
                .thenReturn(Optional.empty());

        DoctorNotFoundException exception =
                assertThrows(DoctorNotFoundException.class,
                        () -> service.update(999L, request));

        assertEquals("Doctor not found with id: 999", exception.getMessage());
        verify(specializationRepository, never()).findById(any());
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowException_whenSpecializationDoesNotExist() {
        Specialization specialization =
                new Specialization("Cardiology");
        Doctor doctor =
                new Doctor(
                        "Alex",
                        "Smith",
                        5,
                        true,
                        specialization
                );

        DoctorRequest request =
                new DoctorRequest(
                        "Alex",
                        "Brown",
                        7,
                        true,
                        999L
                );
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(specializationRepository.findById(999L)).thenReturn(Optional.empty());
        SpecializationNotFoundException exception = assertThrows(
                SpecializationNotFoundException.class,
                () -> service.update(1L, request)
        );
        assertEquals("Specialization not found with id: 999", exception.getMessage());
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteDoctor_whenDoctorExists() {
        Specialization specialization =
                new Specialization("Cardiology");
        Doctor doctor =
                new Doctor(
                        "Alex",
                        "Smith",
                        5,
                        true,
                        specialization
                );
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        service.delete(1L);
        verify(doctorRepository).delete(doctor);

    }

    @Test
    void delete_shouldThrowException_whenDoctorDoesNotExist(){
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> service.delete(999L));
        assertEquals("Doctor not found with id: 999", exception.getMessage());
        verify(doctorRepository, never()).delete(any());
    }
}

