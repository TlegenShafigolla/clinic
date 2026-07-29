package kz.tlegen.clinic.controller;

import jakarta.validation.Valid;
import kz.tlegen.clinic.dto.specialization.SpecializationRequest;
import kz.tlegen.clinic.dto.specialization.SpecializationResponse;
import kz.tlegen.clinic.service.SpecializationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {
    private final SpecializationService service;

    public SpecializationController(SpecializationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SpecializationResponse create(@Valid @RequestBody SpecializationRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<SpecializationResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public SpecializationResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }
}
