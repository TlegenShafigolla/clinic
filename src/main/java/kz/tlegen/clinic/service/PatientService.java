package kz.tlegen.clinic.service;

import kz.tlegen.clinic.model.Patient;
import org.springframework.stereotype.Service;

@Service
public class PatientService {
    public Patient getPatient() {
        return new Patient(
                1L,
                "Alex",
                25
        );
    }
}
