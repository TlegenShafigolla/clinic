package kz.tlegen.clinic.repository;

import kz.tlegen.clinic.entity.Appointment;
import kz.tlegen.clinic.entity.AppointmentStatus;
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

    List<Appointment> findAllByPatientIdOrderByAppointmentDateTimeAsc(Long patientId);
    List<Appointment> findAllByDoctorIdOrderByAppointmentDateTimeAsc(Long doctorId);

    List<Appointment> findAllByPatientIdAndStatusOrderByAppointmentDateTimeAsc(
            Long patientId,
            AppointmentStatus status
    );

    List<Appointment> findAllByDoctorIdAndStatusOrderByAppointmentDateTimeAsc(
            Long doctorId,
            AppointmentStatus status
    );

    List<Appointment> findAllByStatusOrderByAppointmentDateTimeAsc(
            AppointmentStatus status
    );

    List<Appointment> findAllByOrderByAppointmentDateTimeAsc();
}