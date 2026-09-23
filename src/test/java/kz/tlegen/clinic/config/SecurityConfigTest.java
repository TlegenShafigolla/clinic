package kz.tlegen.clinic.config;

import kz.tlegen.clinic.controller.DoctorController;
import kz.tlegen.clinic.controller.UserController;
import kz.tlegen.clinic.dto.doctor.DoctorRequest;
import kz.tlegen.clinic.repository.UserRepository;
import kz.tlegen.clinic.security.JwtService;
import kz.tlegen.clinic.service.DoctorService;
import kz.tlegen.clinic.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class,
        DoctorController.class})
@Import(SecurityConfig.class)
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private DoctorService doctorService;

    @Test
    @WithMockUser(username = "patient@gmail.com", roles = "PATIENT")
    void getUsers_shouldReturn403_whenUserIsPatient() throws Exception {
        mockMvc.perform(
                get("/api/users")
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void getUsers_shouldReturn200_whenUserIsAdmin() throws Exception {
        when(userService.findAll())
                .thenReturn(List.of());
        mockMvc.perform(
                get("/api/users")
        ).andExpect(status().isOk());

        verify(userService).findAll();
    }

    @Test
    void getUsers_shouldReturn401_whenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(
                get("/api/users")
        ).andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(username = "patient@gmail.com", roles = "PATIENT")
    void createDoctor_shouldReturn403_whenUserIsPatient() throws Exception {
        mockMvc.perform(
                post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                          "firstName": "Alex",
                                          "lastName": "Smith",
                                          "experienceYears": 5,
                                          "active": true,
                                          "specializationId": 1
                                }
                                """
                        )
        ).andExpect(status().isForbidden());

        verifyNoInteractions(doctorService);
    }
}
