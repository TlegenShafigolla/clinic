package kz.tlegen.clinic.config;

import kz.tlegen.clinic.controller.AppointmentController;
import kz.tlegen.clinic.controller.DoctorController;
import kz.tlegen.clinic.controller.PatientController;
import kz.tlegen.clinic.controller.UserController;
import kz.tlegen.clinic.dto.appointment.AppointmentRequest;
import kz.tlegen.clinic.dto.appointment.AppointmentResponse;
import kz.tlegen.clinic.dto.doctor.DoctorRequest;
import kz.tlegen.clinic.dto.doctor.DoctorResponse;
import kz.tlegen.clinic.entity.AppointmentStatus;
import kz.tlegen.clinic.repository.UserRepository;
import kz.tlegen.clinic.security.JwtService;
import kz.tlegen.clinic.service.AppointmentService;
import kz.tlegen.clinic.service.DoctorService;
import kz.tlegen.clinic.service.PatientService;
import kz.tlegen.clinic.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class,
        DoctorController.class,
        PatientController.class,
        AppointmentController.class})
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

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private AppointmentService appointmentService;

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

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void createDoctor_shouldReturn201_whenUserIsAdmin() throws Exception {
        DoctorResponse response = new DoctorResponse(
                1L,
                "Alex",
                "Smith",
                5,
                true,
                1L,
                "Cardiology"
        );
        when(doctorService.create(any(DoctorRequest.class)))
                .thenReturn(response);

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
        ).andExpect(status().isCreated());
        verify(doctorService).create(any(DoctorRequest.class));
    }

    @Test
    @WithMockUser(username = "patient@gmail.com", roles = "PATIENT")
    void getDoctors_shouldReturn200_whenUserIsPatient() throws Exception {
        when(doctorService.findAll())
                .thenReturn(List.of());

        mockMvc.perform(
                get("/api/doctors")
        ).andExpect(status().isOk());
        verify(doctorService).findAll();
    }

    @Test
    @WithMockUser(username = "doctor@gmail.com", roles = "DOCTOR")
    void deleteDoctor_shouldReturn403_whenUserIsDoctor() throws Exception {
        mockMvc.perform(
                delete("/api/doctors/{id}", 1L)
        ).andExpect(status().isForbidden());
        verifyNoInteractions(doctorService);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void deleteDoctor_shouldReturn204_whenUserIsAdmin() throws Exception {
        mockMvc.perform(
                delete("/api/doctors/{id}", 1L)
        ).andExpect(status().isNoContent());
        verify(doctorService).delete(1L);
    }

    @Test
    @WithMockUser(username = "patient@gmail.com", roles = "PATIENT")
    void getPatients_shouldReturn403_whenUserIsPatient() throws Exception {
        mockMvc.perform(
                get("/api/patients")
        ).andExpect(status().isForbidden());
        verifyNoInteractions(patientService);
    }

    @Test
    @WithMockUser(username = "doctor@gmail.com", roles = "DOCTOR")
    void getPatients_shouldReturn200_whenUserIsDoctor() throws Exception {
        when(patientService.findAll()).thenReturn(List.of());
        mockMvc.perform(
                get("/api/patients")
        ).andExpect(status().isOk());
        verify(patientService).findAll();
    }

    @Test
    @WithMockUser(username = "doctor@gmail.com", roles = "DOCTOR")
    void deletePatient_shouldReturn403_whenUserIsDoctor() throws Exception {
        mockMvc.perform(
                delete("/api/patients/{id}", 1L)
        ).andExpect(status().isForbidden());
        verifyNoInteractions(patientService);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void deletePatient_shouldReturn204_whenUserIsAdmin() throws Exception {
        mockMvc.perform(
                delete("/api/patients/{id}", 1L)
        ).andExpect(status().isNoContent());
        verify(patientService).delete(1L);
    }

    @Test
    @WithMockUser(username = "patient@gmail.com", roles = "PATIENT")
    void getAppointments_shouldReturn403_whenUserIsPatient() throws Exception {
        mockMvc.perform(
                get("/api/appointments")
        ).andExpect(status().isForbidden());
        verifyNoInteractions(appointmentService);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void getAppointments_shouldReturn200_whenUserIsAdmin() throws Exception {
        when(appointmentService.findAll()).thenReturn(List.of());
        mockMvc.perform(
                get("/api/appointments")
        ).andExpect(status().isOk());
        verify(appointmentService).findAll();
    }

    @Test
    @WithMockUser(username = "patient@gmail.com", roles = "PATIENT")
    void createAppointment_shouldBeAllowed_whenUserIsPatient() throws Exception {
        AppointmentResponse response = new AppointmentResponse(
                10L,
                1L,
                "Alex",
                "Smith",
                1L,
                "Maria",
                "Manas",
                LocalDateTime.of(2026, 9, 25, 10, 0),
                AppointmentStatus.SCHEDULED,
                "Consultation"
        );

        when(appointmentService.create(any(AppointmentRequest.class)))
                .thenReturn(response);
        mockMvc.perform(
                post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "doctorId": 1,
                                  "patientId": 1,
                                  "appointmentDateTime": "2026-09-25T10:00:00",
                                  "status": "SCHEDULED",
                                  "reason": "Consultation"
                                }
                                """)).andExpect(status().isCreated());
        verify(appointmentService).create(any(AppointmentRequest.class));
    }

    @Test
    @WithMockUser(username = "alex@gmail.com", roles = "PATIENT")
    void deleteAppointment_shouldBeAllowed_whenUserIsPatient() throws Exception {
        mockMvc.perform(delete("/api/appointments/{id}", 1L))
                .andExpect(status().isNoContent());
        verify(appointmentService).delete(1L);
    }
}
