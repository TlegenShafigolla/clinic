package kz.tlegen.clinic.service;

import kz.tlegen.clinic.mapper.SpecializationMapper;
import kz.tlegen.clinic.repository.SpecializationRepository;
import org.springframework.stereotype.Service;

@Service
public class SpecializationService {
    private final SpecializationRepository repository;
    private final SpecializationMapper mapper;

    public SpecializationService(SpecializationRepository repository, SpecializationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

}
