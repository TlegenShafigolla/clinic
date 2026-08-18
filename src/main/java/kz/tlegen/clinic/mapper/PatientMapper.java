package kz.tlegen.clinic.mapper;

import kz.tlegen.clinic.dto.patient.PatientRequest;
import kz.tlegen.clinic.dto.patient.PatientResponse;
import kz.tlegen.clinic.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {
    public Patient toEntity(PatientRequest request) {
        return new Patient(request.getFirstName(),
                request.getLastName(),
                request.getBirthDate(),
                request.getPhone(),
                request.isActive());
    }

    public PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getBirthDate(),
                patient.getPhone(),
                patient.isActive()
        );
    }


}
