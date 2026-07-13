//package kz.tlegen.clinic.controller;
//
//import kz.tlegen.clinic.model.Doctor;
//import kz.tlegen.clinic.model.Patient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class HelloController {
//
//    @GetMapping("/hello")
//    public String hello() {
//        return "Hello World";
//    }
//
//    @GetMapping("/clinic")
//    public String clinic() {
//        return "Clinic Management System";
//    }
//    @GetMapping("/patient")
//    public Patient getPatient() {
//
//        return new Patient(
//                1L,
//                "Alex",
//                25
//        );
//    }
//    @GetMapping("/doctor")
//    public Doctor getDoctor() {
//        return new Doctor(
//                1L,
//                "John Smith",
//                "Cardiologist"
//        );
//    }
//}
