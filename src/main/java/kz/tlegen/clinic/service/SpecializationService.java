package kz.tlegen.clinic.service;

import kz.tlegen.clinic.dto.specialization.SpecializationRequest;
import kz.tlegen.clinic.dto.specialization.SpecializationResponse;
import kz.tlegen.clinic.entity.Specialization;
import kz.tlegen.clinic.exception.SpecializationAlreadyExistsException;
import kz.tlegen.clinic.mapper.SpecializationMapper;
import kz.tlegen.clinic.repository.SpecializationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecializationService {
    private final SpecializationRepository repository;
    private final SpecializationMapper mapper;

    public SpecializationService(SpecializationRepository repository, SpecializationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public SpecializationResponse create(
            SpecializationRequest request
    ) {
        String name = request.getName();
        if (repository.existsByName(name)) {
            throw new SpecializationAlreadyExistsException(
                    "Specialization already exists: " + name
            );
        }
        Specialization specialization = mapper.toEntity(request);
        Specialization savedSpecialization = repository.save(specialization);

        return mapper.toResponse(savedSpecialization);
    }

    public List<SpecializationResponse> findAll() {
        List<Specialization> specializations = repository.findAll();
        return specializations.stream()
                .map(mapper::toResponse)
                .toList();
    }
}
