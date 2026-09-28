package kz.tlegen.clinic.security;

import kz.tlegen.clinic.entity.Doctor;
import kz.tlegen.clinic.entity.Patient;
import kz.tlegen.clinic.entity.User;
import kz.tlegen.clinic.exception.DoctorNotFoundException;
import kz.tlegen.clinic.exception.PatientNotFoundException;
import kz.tlegen.clinic.repository.DoctorRepository;
import kz.tlegen.clinic.repository.PatientRepository;
import org.springframework.stereotype.Service;

@Service
public class CurrentProfileService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public CurrentProfileService(
            DoctorRepository doctorRepository,
            PatientRepository patientRepository
    ) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public Patient getCurrentPatient(User currentUser) {
        return patientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() ->
                        new PatientNotFoundException(
                                "Patient profile not found for current user"
                        )
                );
    }

    public Doctor getCurrentDoctor(User currentUser) {
        return doctorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() ->
                        new DoctorNotFoundException(
                                "Doctor profile not found for current user"
                        )
                );
    }
}