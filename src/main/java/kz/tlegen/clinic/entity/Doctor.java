package kz.tlegen.clinic.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false)
    private Integer experienceYears;

    @Column(nullable = false)
    private boolean active;


    @ManyToOne(optional = false)
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;


    protected Doctor() {
    }

    public Doctor(String firstName, String lastName, Integer experienceYears, boolean active, Specialization specialization) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.experienceYears = experienceYears;
        this.active = active;
        this.specialization = specialization;
    }

    public User getUser() {
        return user;
    }

    public void linkUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
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

    public void setExperienceYear(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void update(String firstName,
                       String lastName,
                       Integer experienceYears,
                       boolean active,
                       Specialization specialization) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.experienceYears = experienceYears;
        this.active = active;
        this.specialization = specialization;
    }
}
