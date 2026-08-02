package kz.tlegen.clinic.service;

import kz.tlegen.clinic.dto.specialization.SpecializationRequest;
import kz.tlegen.clinic.dto.specialization.SpecializationResponse;
import kz.tlegen.clinic.entity.Specialization;
import kz.tlegen.clinic.exception.SpecializationAlreadyExistsException;
import kz.tlegen.clinic.exception.SpecializationNotFoundException;
import kz.tlegen.clinic.mapper.SpecializationMapper;
import kz.tlegen.clinic.repository.SpecializationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpecializationServiceTest {
    @Mock
    private SpecializationRepository repository;
    @Mock
    private SpecializationMapper mapper;

    @InjectMocks
    private SpecializationService service;


    @Test
    void create_shouldReturnResponse_whenNameDoesNotExist() {
        SpecializationRequest request = new SpecializationRequest("Cardiologist");

        Specialization specialization = new Specialization("Cardiologist");

        SpecializationResponse expectedResponse = new SpecializationResponse(1L, "Cardiologist");

        when(repository.existsByName("Cardiologist"))
                .thenReturn(false);

        when(mapper.toEntity(request))
                .thenReturn(specialization);

        when(repository.save(specialization))
                .thenReturn(specialization);

        when(mapper.toResponse(specialization))
                .thenReturn(expectedResponse);

        SpecializationResponse actualResponse =
                service.create(request);

        assertEquals(1L, actualResponse.getId());
        assertEquals("Cardiologist", actualResponse.getName());
    }

    @Test
    void create_shouldThrowException_whenNameAlreadyExists() {
        SpecializationRequest request = new SpecializationRequest("Cardiologist");
        when(repository.existsByName("Cardiologist"))
                .thenReturn(true);
        SpecializationAlreadyExistsException exception =
                assertThrows(
                        SpecializationAlreadyExistsException.class,
                        () -> service.create(request)
                );

        assertEquals(
                "Specialization already exists: Cardiologist",
                exception.getMessage()
        );
        verify(repository, never()).save(any());
    }

    @Test
    void findById_shouldReturnResponse_whenSpecializationExists() {
        Specialization specialization = new Specialization("Dermatology");


        SpecializationResponse expectedResponse =
                new SpecializationResponse(2L, "Dermatology");

        when(repository.findById(2L))
                .thenReturn(Optional.of(specialization));

        when(mapper.toResponse(specialization))
                .thenReturn(expectedResponse);

        SpecializationResponse actualResponse =
                service.findById(2L);

        assertEquals(2L, actualResponse.getId());
        assertEquals("Dermatology", actualResponse.getName());
    }

    @Test
    void findById_shouldThrowException_whenSpecializationDoesNotExist() {
        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        SpecializationNotFoundException exception =
                assertThrows(
                        SpecializationNotFoundException.class,
                        () -> service.findById(999L)
                );
        assertEquals(
                "Specialization not found with id: 999",
                exception.getMessage()
        );

        verify(mapper, never()).toResponse(any());
    }

    @Test
    void findAll_shouldReturnResponses() {
        Specialization first =
                new Specialization("Neurologist");

        Specialization second =
                new Specialization("Cardiologist");

        SpecializationResponse firstResponse =
                new SpecializationResponse(1L, "Neurologist");

        SpecializationResponse secondResponse =
                new SpecializationResponse(2L, "Cardiologist");

        when(repository.findAll())
                .thenReturn(List.of(first, second));

        when(mapper.toResponse(first))
                .thenReturn(firstResponse);

        when(mapper.toResponse(second))
                .thenReturn(secondResponse);

        List<SpecializationResponse> result =
                service.findAll();

        assertEquals(2, result.size());
        assertEquals("Neurologist", result.get(0).getName());
        assertEquals("Cardiologist", result.get(1).getName());
    }

    @Test
    void update_shouldReturnUpdatedResponse_whenDataIsValid() {
        SpecializationRequest request = new SpecializationRequest("Dermatologist");
        Specialization specialization = new Specialization("Dermatology");
        SpecializationResponse expectedResponse = new SpecializationResponse(2L, "Dermatologist");

        when(repository.findById(2L))
                .thenReturn(Optional.of(specialization));

        when(repository.existsByNameAndIdNot("Dermatologist", 2L))
                .thenReturn(false);

        when(repository.save(specialization))
                .thenReturn(specialization);

        when(mapper.toResponse(specialization))
                .thenReturn(expectedResponse);
        SpecializationResponse actualResponse =
                service.update(2L, request);

        assertEquals(2L, actualResponse.getId());
        assertEquals("Dermatologist", actualResponse.getName());

        assertEquals("Dermatologist", specialization.getName());
    }

    @Test
    void update_shouldThrowException_whenNameBelongsToAnotherSpecialization() {
        SpecializationRequest request = new SpecializationRequest("Neurologist");
        Specialization specialization = new Specialization("Dermatology");

        when(repository.findById(2L))
                .thenReturn(Optional.of(specialization));

        when(repository.existsByNameAndIdNot("Neurologist", 2L))
                .thenReturn(true);

        SpecializationAlreadyExistsException exception =
                assertThrows(
                        SpecializationAlreadyExistsException.class,
                        () -> service.update(2L, request)
                );

        assertEquals(
                "Specialization already exists: Neurologist",
                exception.getMessage()
        );
        verify(repository, never()).save(any());
        assertEquals("Dermatology", specialization.getName());
    }
}
