package kz.tlegen.clinic.controller;

import kz.tlegen.clinic.model.Doctor;
import kz.tlegen.clinic.service.DoctorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DoctorController {
    public final DoctorService doctorService;

    public DoctorController(DoctorService doctorService){
        this.doctorService=doctorService;
    }

    @GetMapping("/doctor")
    public Doctor getDoctor(){
        return doctorService.getDoctor();
    }
}
