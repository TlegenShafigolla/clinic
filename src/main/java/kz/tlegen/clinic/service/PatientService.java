package kz.tlegen.clinic.service;

import kz.tlegen.clinic.dto.patient.PatientRequest;
import kz.tlegen.clinic.dto.patient.PatientResponse;
import kz.tlegen.clinic.entity.Patient;
import kz.tlegen.clinic.exception.PatientNotFoundException;
import kz.tlegen.clinic.mapper.PatientMapper;
import kz.tlegen.clinic.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper mapper;

    public PatientService(PatientRepository patientRepository, PatientMapper mapper) {
        this.patientRepository = patientRepository;
        this.mapper = mapper;
    }

    public PatientResponse create(PatientRequest request) {
        Patient patient = mapper.toEntity(request);
        Patient savedPatient = patientRepository.save(patient);
        return mapper.toResponse(savedPatient);
    }

    public List<PatientResponse> findAll() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
                .map(mapper::toResponse)
                .toList();
    }

    public PatientResponse findById(Long id) {
        Patient patient = getPatientByIdOrThrow(id);
        return mapper.toResponse(patient);
    }

    public void delete(Long id) {
        Patient patient = getPatientByIdOrThrow(id);
        patientRepository.delete(patient);

    }

    public PatientResponse update(Long id, PatientRequest request) {
        Patient patient = getPatientByIdOrThrow(id);
        patient.update(request.getFirstName(),
                request.getLastName(),
                request.getBirthDate(),
                request.getPhone(),
                request.isActive());
        Patient savedPatient = patientRepository.save(patient);
        return mapper.toResponse(savedPatient);
    }

    private Patient getPatientByIdOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(
                        () -> new PatientNotFoundException("Patient not found with id: " + id));
    }

}
