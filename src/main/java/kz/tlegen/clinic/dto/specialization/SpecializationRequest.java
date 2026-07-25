package kz.tlegen.clinic.dto.specialization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SpecializationRequest {

    @NotBlank(message = "Specialization name must not be blank")
    @Size(
            max = 100,
            message = "Specialization name must not exceed 100 characters"
    )
    private String name;

    public SpecializationRequest() {

    }

    public SpecializationRequest(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
