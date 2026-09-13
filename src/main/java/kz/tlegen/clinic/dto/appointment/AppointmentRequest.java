package kz.tlegen.clinic.dto.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kz.tlegen.clinic.entity.AppointmentStatus;

import java.time.LocalDateTime;

public class AppointmentRequest {

    @NotNull(message = "Doctor id must not be null")
    private Long doctorId;

    @NotNull(message = "Patient id must not be null")
    private Long patientId;

    @NotNull(message = "Appointment date time must not be null")
    @Future(message = "Appointment date time must be in the future")
    private LocalDateTime appointmentDateTime;

    @NotNull(message = "Appointment status must not be null")
    private AppointmentStatus status;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;

    public AppointmentRequest() {
    }

    public AppointmentRequest(Long doctorId, Long patientId, LocalDateTime appointmentDateTime, AppointmentStatus status, String reason) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.appointmentDateTime = appointmentDateTime;
        this.status = status;
        this.reason = reason;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
