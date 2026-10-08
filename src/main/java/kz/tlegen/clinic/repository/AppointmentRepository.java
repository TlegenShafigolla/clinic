package kz.tlegen.clinic.repository;

import kz.tlegen.clinic.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByDoctorIdAndAppointmentDateTime(
            Long doctorId,
            LocalDateTime appointmentDateTime
    );

    boolean existsByDoctorIdAndAppointmentDateTimeAndIdNot(
            Long doctorId,
            LocalDateTime appointmentDateTime,
            Long id
    );
    List<Appointment> findAllByPatientId(Long patientId);
    List<Appointment> findAllByDoctorId(Long doctorId);
}