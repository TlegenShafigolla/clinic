package kz.tlegen.clinic.dto.appointment;

import kz.tlegen.clinic.entity.AppointmentStatus;

import java.time.LocalDateTime;

public class AppointmentResponse {
    private Long id;

    private Long doctorId;
    private String doctorFirstName;
    private String doctorLastName;

    private Long patientId;
    private String patientFirstName;
    private String patientLastName;

    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private String reason;

    public AppointmentResponse() {
    }
    public AppointmentResponse(Long id, Long doctorId, String doctorFirstName, String doctorLastName,
                               Long patientId, String patientFirstName, String  patientLastName,
                               LocalDateTime appointmentDateTime, AppointmentStatus status, String reason) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorFirstName = doctorFirstName;
        this.doctorLastName = doctorLastName;
        this.patientId = patientId;
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.appointmentDateTime = appointmentDateTime;
        this.status = status;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getDoctorId() {
        return doctorId;
    }
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
    public String getDoctorFirstName() {
        return doctorFirstName;
    }
    public void setDoctorFirstName(String doctorFirstName) {
        this.doctorFirstName = doctorFirstName;
    }
    public String getDoctorLastName() {
        return doctorLastName;
    }
    public void setDoctorLastName(String doctorLastName) {
        this.doctorLastName = doctorLastName;
    }

    public Long getPatientId() {
        return patientId;
    }
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
    public String getPatientFirstName() {
        return patientFirstName;
    }
    public void setPatientFirstName(String patientFirstName) {
        this.patientFirstName = patientFirstName;
    }
    public String getPatientLastName() {
        return patientLastName;
    }
    public void setPatientLastName(String patientLastName) {
        this.patientLastName = patientLastName;
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
