package kz.tlegen.clinic.dto.doctor;

public class DoctorResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer experienceYears;
    private boolean active;

    private Long specializationId;
    private String specializationName;

    public DoctorResponse() {
    }

    public DoctorResponse(Long id,
                          String firstName,
                          String lastName,
                          Integer experienceYears,
                          boolean active,
                          Long specializationId,
                          String specializationName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.experienceYears = experienceYears;
        this.active = active;
        this.specializationId = specializationId;
        this.specializationName = specializationName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSpecializationName() {
        return specializationName;
    }

    public void setSpecializationName(String specializationName) {
        this.specializationName = specializationName;
    }
}
