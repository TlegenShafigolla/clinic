package kz.tlegen.clinic.service;

import kz.tlegen.clinic.model.Doctor;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {
    public Doctor getDoctor() {
        return new Doctor(
                1L,
                "John Smith",
                "Cardiologist"
        );
    }
}
