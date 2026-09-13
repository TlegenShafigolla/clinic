package kz.tlegen.clinic.mapper;

import kz.tlegen.clinic.dto.appointment.AppointmentRequest;
import kz.tlegen.clinic.dto.appointment.AppointmentResponse;
import kz.tlegen.clinic.entity.Appointment;
import kz.tlegen.clinic.entity.Doctor;
import kz.tlegen.clinic.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {
    public Appointment toEntity(
            AppointmentRequest request,
            Doctor doctor,
            Patient patient
    ) {
        return new Appointment(doctor, patient, request.getAppointmentDateTime(),
                request.getStatus(),
                request.getReason()
        );
    }

    public AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(appointment.getId(), appointment.getDoctor().getId(),
                appointment.getDoctor().getFirstName(), appointment.getDoctor().getLastName(),
                appointment.getPatient().getId(), appointment.getPatient().getFirstName(),
                appointment.getPatient().getLastName(),
                appointment.getAppointmentDateTime(),
                appointment.getStatus(), appointment.getReason());
    }

}
