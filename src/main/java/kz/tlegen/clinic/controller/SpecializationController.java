package kz.tlegen.clinic.controller;

import jakarta.validation.Valid;
import kz.tlegen.clinic.dto.specialization.SpecializationRequest;
import kz.tlegen.clinic.dto.specialization.SpecializationResponse;
import kz.tlegen.clinic.service.SpecializationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {
    private final SpecializationService service;

    public SpecializationController(SpecializationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SpecializationResponse create (@Valid @RequestBody SpecializationRequest request){
        return service.create(request);
    }
}
