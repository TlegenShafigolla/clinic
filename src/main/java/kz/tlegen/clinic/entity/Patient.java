package kz.tlegen.clinic.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(nullable = false)
    private boolean active;

    protected Patient() {
    }

    public Patient(
            String firstName,
            String lastName,
            LocalDate birthDate,
            String phone,
            boolean active) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public LocalDate getBirthDate() {
        return birthDate;
    }


    public String getPhone() {
        return phone;
    }


    public boolean isActive() {
        return active;
    }

    public void update(
            String firstName,
            String lastName,
            LocalDate birthDate,
            String phone,
            boolean active
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.active = active;
    }

}
