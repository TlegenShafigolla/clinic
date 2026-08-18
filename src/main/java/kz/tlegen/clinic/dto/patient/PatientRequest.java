package kz.tlegen.clinic.dto.patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class PatientRequest {

    @NotBlank(message = "Patient first name must not be blank")
    @Size(max = 100, message = "Patient first name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Patient last name must not be blank")
    @Size(max = 100, message = "Patient last name must not exceed 100 characters")
    private String lastName;

    @NotNull(message = "Birth date must not be null")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotBlank(message = "Phone must not be blank")
    @Size(max = 30, message = "Phone must not exceed 30 characters")
    private String phone;

    private boolean active;

    public PatientRequest() {
    }

    public PatientRequest(String firstName, String lastName, LocalDate birthDate, String phone, boolean active) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.active = active;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
