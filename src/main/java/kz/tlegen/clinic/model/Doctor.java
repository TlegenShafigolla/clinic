package kz.tlegen.clinic.model;


import jakarta.persistence.*;

@Entity
@Table(name="doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String specialization;

    protected  Doctor() {

    }

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
