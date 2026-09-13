package kz.tlegen.clinic.service;

import kz.tlegen.clinic.dto.appointment.AppointmentRequest;
import kz.tlegen.clinic.dto.appointment.AppointmentResponse;
import kz.tlegen.clinic.entity.Appointment;
import kz.tlegen.clinic.entity.Doctor;
import kz.tlegen.clinic.entity.Patient;
import kz.tlegen.clinic.exception.AppointmentNotFoundException;
import kz.tlegen.clinic.exception.DoctorNotFoundException;
import kz.tlegen.clinic.exception.PatientNotFoundException;
import kz.tlegen.clinic.mapper.AppointmentMapper;
import kz.tlegen.clinic.repository.AppointmentRepository;
import kz.tlegen.clinic.repository.DoctorRepository;
import kz.tlegen.clinic.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper mapper;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            AppointmentMapper mapper
    ) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.mapper = mapper;
    }

    public AppointmentResponse create(AppointmentRequest request) {
        Long doctorId = request.getDoctorId();
        Long patientId = request.getPatientId();
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(
                () -> new DoctorNotFoundException("Doctor not found with id: " + doctorId)
        );
        Patient patient = patientRepository.findById(patientId).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with id: " + patientId)
        );
        Appointment appointment = mapper.toEntity(request, doctor, patient);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return mapper.toResponse(savedAppointment);
    }

    public List<AppointmentResponse> findAll() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .map(mapper::toResponse)
                .toList();
    }

    public AppointmentResponse findById(Long id) {
        Appointment appointment = getAppointmentByIdOrThrow(id);
        return mapper.toResponse(appointment);
    }

    public void delete(Long id) {
        Appointment appointment = getAppointmentByIdOrThrow(id);
        appointmentRepository.delete(appointment);
    }

    public AppointmentResponse update(Long id, AppointmentRequest request) {
        Appointment appointment = getAppointmentByIdOrThrow(id);

        Doctor doctor = doctorRepository.findById(request.getDoctorId()).orElseThrow(
                () -> new DoctorNotFoundException("Doctor not found with id: " + request.getDoctorId())
        );
        Patient patient = patientRepository.findById(request.getPatientId()).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with id: " + request.getPatientId())
        );
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