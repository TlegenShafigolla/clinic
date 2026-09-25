package kz.tlegen.clinic.security;

import kz.tlegen.clinic.entity.Appointment;
import kz.tlegen.clinic.entity.Doctor;
import kz.tlegen.clinic.entity.Patient;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


@Service
public class AppointmentAuthorizationService {
    public void validatePatientOwnsAppointment(
            Patient currentPatient,
            Appointment appointment,
            String message
    ) {
        if (!currentPatient.getId().equals(appointment.getPatient().getId())) {
            throw new AccessDeniedException(message);
        }
    }

    public void validateDoctorOwnsAppointment(
            Doctor currentDoctor,
            Appointment appointment,
            String message
    ) {
        if (!currentDoctor.getId().equals(appointment.getDoctor().getId())) {
            throw new AccessDeniedException(message);
        }
    }

    public void validatePatientRequestOwner(
            Patient currentPatient,
            Long patientId,
            String message
    ) {
        if (!currentPatient.getId().equals(patientId)) {
            throw new AccessDeniedException(message);
        }
    }

    public void validateDoctorRequestOwner(
            Doctor currentDoctor,
            Long doctorId,
            String message
    ) {
        if (!currentDoctor.getId().equals(doctorId)) {
            throw new AccessDeniedException(message);
        }
    }
}
