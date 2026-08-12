package kz.tlegen.clinic.service;

import kz.tlegen.clinic.dto.doctor.DoctorRequest;
import kz.tlegen.clinic.dto.doctor.DoctorResponse;
import kz.tlegen.clinic.entity.Doctor;
import kz.tlegen.clinic.entity.Specialization;
import kz.tlegen.clinic.exception.DoctorNotFoundException;
import kz.tlegen.clinic.exception.SpecializationNotFoundException;
import kz.tlegen.clinic.mapper.DoctorMapper;
import kz.tlegen.clinic.repository.DoctorRepository;
import kz.tlegen.clinic.repository.SpecializationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final DoctorMapper mapper;

    public DoctorService(DoctorRepository doctorRepository, SpecializationRepository specializationRepository, DoctorMapper mapper) {
        this.doctorRepository = doctorRepository;
        this.specializationRepository = specializationRepository;
        this.mapper = mapper;
    }

    public DoctorResponse create(DoctorRequest request) {
        Long specializationId = request.getSpecializationId();

        Specialization specialization =
                specializationRepository.findById(specializationId)
                        .orElseThrow(
                                () -> new SpecializationNotFoundException(
                                        "Specialization not found with id: " + specializationId
                                )
                        );

        Doctor doctor = mapper.toEntity(request, specialization);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return mapper.toResponse(savedDoctor);
    }

    public List<DoctorResponse> findAll() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream()
                .map(mapper::toResponse)
                .toList();

    }

    public DoctorResponse findById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));
        return mapper.toResponse(doctor);
    }
}
