package kz.tlegen.clinic.dto.user;

import kz.tlegen.clinic.entity.Role;

public class UserResponse {
    private Long id;
    private String email;
    private Role role;
    private boolean active;

    public UserResponse() {
    }

    public UserResponse(Long id, String email, Role role, boolean active) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
