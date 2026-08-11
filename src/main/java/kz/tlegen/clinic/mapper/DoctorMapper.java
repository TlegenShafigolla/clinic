package kz.tlegen.clinic.mapper;

import kz.tlegen.clinic.dto.doctor.DoctorRequest;
import kz.tlegen.clinic.dto.doctor.DoctorResponse;
import kz.tlegen.clinic.entity.Doctor;
import kz.tlegen.clinic.entity.Specialization;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {
    public Doctor toEntity(DoctorRequest request, Specialization specialization) {
        return new Doctor(request.getFirstName(),
                request.getLastName(),
                request.getExperienceYears(),
                request.isActive(),
                specialization);
    }

    public DoctorResponse toResponse(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getExperienceYears(),
                doctor.isActive(),
                doctor.getSpecialization().getId(),
                doctor.getSpecialization().getName()
        );

    }
}
