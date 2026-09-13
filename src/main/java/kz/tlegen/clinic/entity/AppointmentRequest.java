package kz.tlegen.clinic.entity;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class AppointmentRequest {

    @NotNull
    private Long doctorId;

    @NotNull
    private Long patientId;

    @NotNull
    private LocalDateTime appointmentDateTime;

    @NotNull
    @Future
    private AppointmentStatus status;

    @Size(max = 500)
    private String reason;

    protected AppointmentRequest() {
    }

}
