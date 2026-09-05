package kz.tlegen.clinic.controller;

import jakarta.validation.Valid;
import kz.tlegen.clinic.dto.patient.PatientRequest;
import kz.tlegen.clinic.dto.patient.PatientResponse;
import kz.tlegen.clinic.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientResponse create(
            @Valid @RequestBody PatientRequest request
    ) {
        return patientService.create(request);
    }

    @GetMapping
    public List<PatientResponse> findAll() {
        return patientService.findAll();
    }

    @GetMapping("/{id}")
    public PatientResponse findById(@PathVariable Long id) {
        return patientService.findById(id);
    }

    @PutMapping("/{id}")
    public PatientResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request
    ) {
        return patientService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)
    {
        patientService.delete(id);
    }
}
