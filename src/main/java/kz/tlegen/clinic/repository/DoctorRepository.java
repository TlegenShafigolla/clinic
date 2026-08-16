package kz.tlegen.clinic.repository;

import kz.tlegen.clinic.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findAllBySpecializationIdAndActiveTrue(Long specializationId);
}
