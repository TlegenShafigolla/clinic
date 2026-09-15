package kz.tlegen.clinic.repository;

import kz.tlegen.clinic.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByDoctorIdAndAppointmentDateTime(
            Long doctorId,
            LocalDateTime appointmentDateTime
    );
}