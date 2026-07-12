package kz.tlegen.clinic.controller;

import kz.tlegen.clinic.model.Patient;
import kz.tlegen.clinic.service.PatientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService){
        this.patientService=patientService;
    }

    @GetMapping("/patient")
    public Patient getPatinet(){
        return patientService.getPatient();
    }
}
