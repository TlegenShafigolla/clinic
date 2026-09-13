package kz.tlegen.clinic.controller;

import jakarta.validation.Valid;
import kz.tlegen.clinic.dto.appointment.AppointmentRequest;
import kz.tlegen.clinic.dto.appointment.AppointmentResponse;
import kz.tlegen.clinic.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(
            @Valid @RequestBody AppointmentRequest request
    ) {
        AppointmentResponse response = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> update(@PathVariable Long id,
                                                      @Valid
                                                      @RequestBody
                                                      AppointmentRequest request) {
        AppointmentResponse response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
