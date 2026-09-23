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
import kz.tlegen.clinic.security.CurrentUserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper mapper;
    private final CurrentUserService currentUserService;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            AppointmentMapper mapper,
            CurrentUserService currentUserService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        Long doctorId = request.getDoctorId();
        Long patientId = request.getPatientId();
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(
                () -> new DoctorNotFoundException("Doctor not found with id: " + doctorId)
        );
        Patient patient = patientRepository.findById(patientId).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with id: " + patientId)
        );
        boolean timeConflict = appointmentRepository.existsByDoctorIdAndAppointmentDateTime(
                request.getDoctorId(),
                request.getAppointmentDateTime()
        );
        if (timeConflict) {
            throw new AppointmentTimeConflictException(
                    "Doctor already has an appointment at this time"
            );
        }

        Appointment appointment = mapper.toEntity(request, doctor, patient);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return mapper.toResponse(savedAppointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findAll() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponse findById(Long id) {
        Appointment appointment = getAppointmentByIdOrThrow(id);
        return mapper.toResponse(appointment);
    }

    @Transactional
    public void delete(Long id) {
        Appointment appointment = getAppointmentByIdOrThrow(id);
        User currentUser = currentUserService.getCurrentUser();
        Role role = currentUser.getRole();
        if (role == Role.ADMIN) {
            appointmentRepository.delete(appointment);
            return;
        }
        if (role == Role.PATIENT) {
            Patient currentPatient = patientRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() ->
                            new PatientNotFoundException(
                                    "Patient profile not found for current user"
                            )
                    );
            if (!currentPatient.getId().equals(appointment.getPatient().getId())) {
                throw new AccessDeniedException(
                        "You cannot delete another patient's appointment"
                );
            }
            appointmentRepository.delete(appointment);
            return;
        }
        if (role == Role.DOCTOR) {
            Doctor currentDoctor = doctorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(()->
                            new DoctorNotFoundException("Doctor not found for current user"));
            if (!currentDoctor.getId().equals(appointment.getDoctor().getId())) {
                throw new AccessDeniedException(
                        "You cannot delete another doctor's appointment");
            }
            appointmentRepository.delete(appointment);
            return;
        }
        throw new AccessDeniedException(
                "You do not have permission to delete this appointment"
        );
    }

    @Transactional
    public AppointmentResponse update(Long id, AppointmentRequest request) {
        Appointment appointment = getAppointmentByIdOrThrow(id);

        Doctor doctor = doctorRepository.findById(request.getDoctorId()).orElseThrow(
                () -> new DoctorNotFoundException("Doctor not found with id: " + request.getDoctorId())
        );
        Patient patient = patientRepository.findById(request.getPatientId()).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with id: " + request.getPatientId())
        );

        boolean timeConflict =
                appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndIdNot(
                        request.getDoctorId(),
                        request.getAppointmentDateTime(),
                        id
                );

        if (timeConflict) {
            throw new AppointmentTimeConflictException(
                    "Doctor already has an appointment at this time"
            );
        }

        appointment.update(doctor, patient,
                request.getAppointmentDateTime(),
                request.getStatus(),
                request.getReason());

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return mapper.toResponse(savedAppointment);
    }

    private Appointment getAppointmentByIdOrThrow(Long id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(
                "Appointment not found with id: " + id
        ));
    }
}