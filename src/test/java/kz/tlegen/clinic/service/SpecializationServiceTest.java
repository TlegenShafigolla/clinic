package kz.tlegen.clinic.service;

import kz.tlegen.clinic.dto.specialization.SpecializationRequest;
import kz.tlegen.clinic.dto.specialization.SpecializationResponse;
import kz.tlegen.clinic.entity.Specialization;
import kz.tlegen.clinic.exception.SpecializationAlreadyExistsException;
import kz.tlegen.clinic.mapper.SpecializationMapper;
import kz.tlegen.clinic.repository.SpecializationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
