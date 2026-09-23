package kz.tlegen.clinic.repository;

import kz.tlegen.clinic.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findAllBySpecializationIdAndActiveTrue(Long specializationId);
    Optional<Doctor> findByUserId(Long userId);
}
