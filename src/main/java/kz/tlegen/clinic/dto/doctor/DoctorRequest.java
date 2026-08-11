package kz.tlegen.clinic.dto.doctor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public class DoctorRequest {

    @NotBlank(message = "Doctor first name must not be blank")
    @Size(max = 100, message = "Doctor first name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Doctor last name must not be blank")
    @Size(max = 100, message = "Doctor last name must not exceed 100 characters")
    private String lastName;

    @NotNull(message = "Experience years must not be null")
    @Min(value = 0, message = "Experience years must be at least 0")
    @Max(value = 60, message = "Experience years must not exceed 60")
    private Integer experienceYears;

    private boolean active;

    @NotNull(message = "Specialization id must not be null")
    private Long specializationId;

    public DoctorRequest() {
    }

    public DoctorRequest(String firstName, String lastName, Integer experienceYears, boolean active,
                         Long specializationId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.experienceYears = experienceYears;
        this.active = active;
        this.specializationId = specializationId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getSpecializationId() {
        return specializationId;
    }

    public void setSpecializationId(Long specializationId) {
        this.specializationId = specializationId;
    }

}
