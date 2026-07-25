package kz.tlegen.clinic.dto.specialization;

public class SpecializationResponse {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SpecializationResponse() {
    }

    public SpecializationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
