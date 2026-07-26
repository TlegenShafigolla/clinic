package kz.tlegen.clinic.controller;

import kz.tlegen.clinic.service.SpecializationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {
    private final SpecializationService service;

    public SpecializationController(SpecializationService service) {
        this.service = service;
    }
}
