package kz.tlegen.clinic.repository;

import kz.tlegen.clinic.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecializationRepository
        extends JpaRepository<Specialization, Long> {
    boolean existsByName(String name);
}

