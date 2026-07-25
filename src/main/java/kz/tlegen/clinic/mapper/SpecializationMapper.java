package kz.tlegen.clinic.mapper;

import kz.tlegen.clinic.dto.specialization.SpecializationRequest;
import kz.tlegen.clinic.dto.specialization.SpecializationResponse;
import kz.tlegen.clinic.entity.Specialization;

public class SpecializationMapper {
    public Specialization toEntity(SpecializationRequest request){
        String name=request.getName();
        return new Specialization(name);
    }

    public SpecializationResponse toResponse (Specialization specialization){
        Long id=specialization.getId();
        String name=specialization.getName();
        return new SpecializationResponse(id,name);

    }
}
