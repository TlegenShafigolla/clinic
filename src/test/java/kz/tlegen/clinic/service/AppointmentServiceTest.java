package kz.tlegen.clinic.service;

import kz.tlegen.clinic.dto.appointment.AppointmentRequest;
import kz.tlegen.clinic.dto.appointment.AppointmentResponse;
import kz.tlegen.clinic.entity.*;
import kz.tlegen.clinic.exception.AppointmentNotFoundException;
import kz.tlegen.clinic.exception.AppointmentTimeConflictException;
import kz.tlegen.clinic.exception.DoctorNotFoundException;
import kz.tlegen.clinic.exception.PatientNotFoundException;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

        AppointmentRequest request = new AppointmentRequest(1L, 1L,
                LocalDate.of(2026, 9, 20).atStartOfDay(),
                AppointmentStatus.SCHEDULED,
                "Consultation");

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
        Appointment appointment = new Appointment(doctor,
                patient,
                request.getAppointmentDateTime(),
                request.getStatus(),
                request.getReason());

        AppointmentResponse expectedResponse = new AppointmentResponse(
                10L,
                1L,
                "Alex",
                "Smith",
                1L,
                "Maria",
                "Manas",
                request.getAppointmentDateTime(),
                AppointmentStatus.SCHEDULED,
                "Consultation");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));


        when(appointmentMapper.toEntity(request, doctor, patient)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(expectedResponse);

        AppointmentResponse actualResponse = appointmentService.create(request);

        assertEquals(10L, actualResponse.getId());
        assertEquals(1L, actualResponse.getDoctorId());
        assertEquals("Alex", actualResponse.getDoctorFirstName());
        assertEquals(1L, actualResponse.getPatientId());

        verify(doctorRepository).findById(1L);
        verify(patientRepository).findById(1L);
        verify(appointmentMapper).toEntity(request, doctor, patient);
        verify(appointmentRepository).save(appointment);
        verify(appointmentMapper).toResponse(appointment);
    }

    @Test
    void create_shouldThrowDoctorNotFoundException() {
        AppointmentRequest request = new AppointmentRequest(999L, 1L,
                LocalDate.of(2026, 9, 20).atStartOfDay(),
                AppointmentStatus.SCHEDULED,
                "Consultation");


        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

        DoctorNotFoundException exception =
                assertThrows(DoctorNotFoundException.class,
                        () -> appointmentService.create(request)
                );

        assertEquals("Doctor not found with id: 999", exception.getMessage());

        verify(doctorRepository).findById(999L);
        verify(patientRepository, never()).findById(any());
        verify(appointmentRepository, never()).save(any());

    }

    @Test
    void create_shouldThrowPatientNotFoundException() {
        AppointmentRequest request = new AppointmentRequest(1L, 999L,
                LocalDate.of(2026, 9, 20).atStartOfDay(),
                AppointmentStatus.SCHEDULED,
                "Consultation");

        Specialization specialization = new Specialization("Cardiology");

        Doctor doctor = new Doctor(
                "Alex",
                "Smith",
                5,
                true,
                specialization
        );

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        PatientNotFoundException exception =
                assertThrows(PatientNotFoundException.class,
                        () -> appointmentService.create(request)
                );

        assertEquals("Patient not found with id: 999", exception.getMessage());

        verify(doctorRepository).findById(1L);
        verify(patientRepository).findById(999L);
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowAppointmentTimeConflictException() {

        AppointmentRequest request = new AppointmentRequest(1L, 1L,
                LocalDate.of(2026, 9, 20).atStartOfDay(),
                AppointmentStatus.SCHEDULED,
                "Consultation");

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

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateTime(
                1L,
                request.getAppointmentDateTime()
        )).thenReturn(true);

        AppointmentTimeConflictException exception =
                assertThrows(
                        AppointmentTimeConflictException.class,
                        () -> appointmentService.create(request)
                );

        assertEquals(
                "Doctor already has an appointment at this time",
                exception.getMessage()
        );

        verify(doctorRepository).findById(1L);
        verify(patientRepository).findById(1L);

        verify(appointmentRepository)
                .existsByDoctorIdAndAppointmentDateTime(
                        1L,
                        request.getAppointmentDateTime()
                );
        verify(appointmentMapper, never()).toEntity(request, doctor, patient);
        verify(appointmentRepository, never()).save(any());

    }

    @Test
    void findById_shouldReturnAppointmentResponse() {
        AppointmentRequest request = new AppointmentRequest(1L, 1L,
                LocalDate.of(2026, 9, 20).atStartOfDay(),
                AppointmentStatus.SCHEDULED,
                "Consultation");

        Appointment appointment = getAppointment(request);

        AppointmentResponse expectedResponse = new AppointmentResponse(
                10L,
                1L,
                "Alex",
                "Smith",
                1L,
                "Maria",
                "Manas",
                request.getAppointmentDateTime(),
                AppointmentStatus.SCHEDULED,
                "Consultation");

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toResponse(appointment)).thenReturn(expectedResponse);

        AppointmentResponse actualResponse = appointmentService.findById(1L);
        assertEquals(10L, actualResponse.getId());
        assertEquals(1L, actualResponse.getDoctorId());
        assertEquals("Alex", actualResponse.getDoctorFirstName());
        assertEquals(1L, actualResponse.getPatientId());
        assertEquals("Maria", actualResponse.getPatientFirstName());
        assertEquals(AppointmentStatus.SCHEDULED, actualResponse.getStatus());
        assertEquals("Consultation", actualResponse.getReason());

        verify(appointmentRepository).findById(1L);
        verify(appointmentMapper).toResponse(appointment);
    }

    private static Appointment getAppointment(AppointmentRequest request) {
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
        return new Appointment(doctor,
                patient,
                request.getAppointmentDateTime(),
                request.getStatus(),
                request.getReason());
    }

    @Test
    void findById_shouldThrowAppointmentNotFoundException() {
        when(appointmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        AppointmentNotFoundException exception =
                assertThrows(AppointmentNotFoundException.class,
                        () -> appointmentService.findById(999L));

        assertEquals("Appointment not found with id: 999", exception.getMessage());

        verify(appointmentRepository).findById(999L);
        verify(patientRepository, never()).findById(any());
        verify(doctorRepository, never()).findById(any());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void findAll_shouldReturnAppointmentResponses() {
        AppointmentRequest firstRequest = new AppointmentRequest(1L, 1L,
                LocalDate.of(2026, 9, 25).atStartOfDay(),
                AppointmentStatus.SCHEDULED,
                "Consultation");

        AppointmentRequest secondRequest = new AppointmentRequest(1L, 1L,
                LocalDate.of(2026, 9, 20).atStartOfDay(),
                AppointmentStatus.SCHEDULED,
                "Checking");

        Appointment firstAppointment = getAppointment(firstRequest);
        Appointment secondAppointment = getAppointment(secondRequest);

        AppointmentResponse firstAppointmentResponse = new AppointmentResponse(
                10L,
                1L,
                "Alex",
                "Smith",
                1L,
                "Maria",
                "Manas",
                firstRequest.getAppointmentDateTime(),
                firstRequest.getStatus(),
                firstRequest.getReason());
        AppointmentResponse secondAppointmentResponse = new AppointmentResponse(
                11L,
                1L,
                "Alex",
                "Smith",
                1L,
                "Maria",
                "Manas",
                secondRequest.getAppointmentDateTime(),
                secondRequest.getStatus(),
                secondRequest.getReason());

        when(appointmentRepository.findAll()).thenReturn(List.of(firstAppointment, secondAppointment));
        when(appointmentMapper.toResponse(firstAppointment)).thenReturn(firstAppointmentResponse);
        when(appointmentMapper.toResponse(secondAppointment)).thenReturn(secondAppointmentResponse);

        List<AppointmentResponse> appointmentResponses = appointmentService.findAll();

        assertEquals(2, appointmentResponses.size());
        assertEquals(firstAppointmentResponse.getAppointmentDateTime(), appointmentResponses.getFirst().getAppointmentDateTime());
        assertEquals(firstRequest.getStatus(), appointmentResponses.get(0).getStatus());
        assertEquals(firstRequest.getReason(), appointmentResponses.get(0).getReason());
        assertEquals(secondAppointmentResponse.getAppointmentDateTime(), appointmentResponses.get(1).getAppointmentDateTime());
        assertEquals(secondRequest.getStatus(), appointmentResponses.get(1).getStatus());
        assertEquals(secondRequest.getReason(), appointmentResponses.get(1).getReason());

        verify(appointmentRepository).findAll();
        verify(appointmentMapper).toResponse(firstAppointment);
        verify(appointmentMapper).toResponse(secondAppointment);
    }


    @Test
    void update_shouldReturnUpdatedAppointmentResponse() {
        AppointmentRequest oldRequest = new AppointmentRequest(
                1L,
                1L,
                LocalDate.of(2026, 9, 20).atStartOfDay(),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );


        AppointmentRequest request = new AppointmentRequest(1L, 1L,
                LocalDate.of(2026, 9, 25).atStartOfDay(),
                AppointmentStatus.COMPLETED,
                "Checking");


        Appointment appointment = getAppointment(oldRequest);


        AppointmentResponse appointmentResponse = new AppointmentResponse(
                10L,
                1L,
                "Alex",
                "Smith",
                1L,
                "Maria",
                "Manas",
                request.getAppointmentDateTime(),
                request.getStatus(),
                request.getReason());

        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(appointment.getDoctor()));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(appointment.getPatient()));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(appointmentResponse);

        AppointmentResponse actualResponse = appointmentService.update(10L, request);
        assertEquals(request.getAppointmentDateTime(), appointment.getAppointmentDateTime());
        assertEquals(request.getStatus(), appointment.getStatus());
        assertEquals(request.getReason(), appointment.getReason());
        assertEquals(10L, actualResponse.getId());
        assertEquals(request.getAppointmentDateTime(), actualResponse.getAppointmentDateTime());
        assertEquals(request.getStatus(), actualResponse.getStatus());
        assertEquals(request.getReason(), actualResponse.getReason());

        verify(appointmentRepository).findById(10L);
        verify(doctorRepository).findById(1L);
        verify(patientRepository).findById(1L);
        verify(appointmentRepository).save(appointment);
        verify(appointmentMapper).toResponse(appointment);
    }

    @Test
    void update_shouldThrowAppointmentTimeConflictException() {
        AppointmentRequest oldRequest = new AppointmentRequest(
                1L,
                1L,
                LocalDate.of(2026, 9, 20).atStartOfDay(),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );

        AppointmentRequest request = new AppointmentRequest(1L, 1L,
                LocalDate.of(2026, 9, 22).atStartOfDay(),
                AppointmentStatus.SCHEDULED,
                "Consultation");

        Appointment appointment = getAppointment(oldRequest);

        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(appointment.getDoctor()));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(appointment.getPatient()));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndIdNot(
                        1L,
                        request.getAppointmentDateTime(),
                        10L
                )
        ).thenReturn(true);
        AppointmentTimeConflictException exception = assertThrows(
                AppointmentTimeConflictException.class,
                () -> appointmentService.update(10L, request));
        assertEquals("Doctor already has an appointment at this time", exception.getMessage());
        assertEquals(
                oldRequest.getAppointmentDateTime(),
                appointment.getAppointmentDateTime()
        );

        verify(appointmentRepository).findById(10L);
        verify(doctorRepository).findById(1L);
        verify(patientRepository).findById(1L);
        verify(appointmentRepository)
                .existsByDoctorIdAndAppointmentDateTimeAndIdNot(
                        1L,
                        request.getAppointmentDateTime(),
                        10L
                );

        verify(appointmentRepository, never()).save(any());
    }


    @Test
    void update_shouldThrowAppointmentNotFoundException() {
        AppointmentRequest request = new AppointmentRequest(
                1L,
                1L,
                LocalDate.of(2026, 9, 20).atStartOfDay(),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );

        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        AppointmentNotFoundException exception =
                assertThrows(AppointmentNotFoundException.class,
                        () -> appointmentService.update(999L, request));

        assertEquals("Appointment not found with id: 999", exception.getMessage());
        verify(appointmentRepository).findById(999L);
        verify(doctorRepository, never()).findById(any());
        verify(patientRepository, never()).findById(any());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowDoctorNotFoundException() {
        AppointmentRequest request = new AppointmentRequest(
                999L,
                1L,
                LocalDate.of(2026, 9, 20).atStartOfDay(),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );

        Appointment appointment = getAppointment(request);
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> appointmentService.update(10L, request));

        assertEquals("Doctor not found with id: 999", exception.getMessage());
        verify(appointmentRepository).findById(10L);
        verify(doctorRepository).findById(999L);
        verify(patientRepository, never()).findById(any());
        verify(appointmentMapper, never()).toResponse(any());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowPatientNotFoundException() {
        AppointmentRequest request = new AppointmentRequest(
                1L,
                999L,
                LocalDate.of(2026, 9, 20).atStartOfDay(),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );

        Appointment appointment = getAppointment(request);
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(appointment.getDoctor()));
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
                () -> appointmentService.update(10L, request));
        assertEquals("Patient not found with id: 999", exception.getMessage());

        verify(appointmentRepository).findById(10L);
        verify(doctorRepository).findById(1L);
        verify(patientRepository).findById(999L);
        verify(appointmentMapper, never()).toResponse(any());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteAppointment() {
        AppointmentRequest request = new AppointmentRequest(1L,
                1L,
                LocalDate.of(2026, 9, 20).atStartOfDay(),
                AppointmentStatus.SCHEDULED,
                "Consultation");
        Appointment appointment = getAppointment(request);
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));
        appointmentService.delete(10L);
        verify(appointmentRepository).findById(10L);
        verify(appointmentRepository).delete(appointment);
    }

    @Test
    void delete_shouldThrowAppointmentNotFoundException() {
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());
        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class,
                () -> appointmentService.delete(999L));
        assertEquals("Appointment not found with id: 999", exception.getMessage());
        verify(appointmentRepository).findById(999L);
        verify(appointmentRepository, never()).delete(any());
    }
}
