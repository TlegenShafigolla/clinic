package kz.tlegen.clinic.model;

public class Doctor {
    private Long id;
    private String fullName;
    private String specialization;

    public Doctor(Long id, String fullName, String specialization) {
        this.id = id;
        this.fullName = fullName;
        this.specialization = specialization;
    }

    public Long getId() {
        return id;
    }
    public String getFullName() {
        return fullName;
    }
    public String getSpecialization() {
        return specialization;
    }
}
