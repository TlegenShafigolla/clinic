package kz.tlegen.clinic.service;

import kz.tlegen.clinic.dto.appointment.AppointmentRequest;
import kz.tlegen.clinic.dto.appointment.AppointmentResponse;
import kz.tlegen.clinic.entity.*;
import kz.tlegen.clinic.mapper.AppointmentMapper;
import kz.tlegen.clinic.repository.AppointmentRepository;
import kz.tlegen.clinic.repository.DoctorRepository;
import kz.tlegen.clinic.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentService appointmentService;


    @Test
    void create_shouldReturnAppointmentResponse() {

        AppointmentRequest appointmentRequest = new AppointmentRequest(1L, 1L,
                LocalDate.of(2026, 5, 10).atStartOfDay(),
                AppointmentStatus.COMPLETED, "ok");

        Specialization specialization =
                new Specialization("Cardiology");

        Doctor doctor =
                new Doctor("Alex", "Smith", 5, true, specialization);

        Patient patient =
                new Patient(
                        "Maria",
                        "Manas",
                        LocalDate.of(2000, 5, 10),
                        "+77001234567",
                        true
                );
        Appointment appointment = new Appointment(doctor, patient, LocalDate.of(2026, 5, 10).atStartOfDay(), AppointmentStatus.COMPLETED, "Ok");

        AppointmentResponse expectedResponse = appointmentService.create(appointmentRequest);

        when(doctorRepository.findById(appointmentRequest.getDoctorId())).thenReturn(Optional.of(doctor));

        when(patientRepository.findById(appointmentRequest.getPatientId())).thenReturn(Optional.of(patient));


        when(appointmentMapper.toEntity(appointmentRequest, doctor, patient)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(expectedResponse);

        AppointmentResponse actualResponse = appointmentService.create(appointmentRequest);
        assertEquals(1L, actualResponse.getId());
        assertEquals(1L, actualResponse.getDoctorId());

        verify(appointmentService.create(appointmentRequest));
    }
}
