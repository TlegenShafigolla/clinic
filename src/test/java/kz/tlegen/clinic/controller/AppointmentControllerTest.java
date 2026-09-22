package kz.tlegen.clinic.controller;

import kz.tlegen.clinic.dto.appointment.AppointmentRequest;
import kz.tlegen.clinic.dto.appointment.AppointmentResponse;
import kz.tlegen.clinic.entity.AppointmentStatus;
import kz.tlegen.clinic.exception.AppointmentNotFoundException;
import kz.tlegen.clinic.exception.AppointmentTimeConflictException;
import kz.tlegen.clinic.security.JwtAuthenticationFilter;
import kz.tlegen.clinic.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void post_shouldCreateAppointment() throws Exception {
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
                                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.doctorId").value(1))
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.appointmentDateTime").value("2026-09-25T10:00:00"))
                .andExpect(jsonPath("$.reason").value("Consultation"));

        verify(appointmentService)
                .create(any(AppointmentRequest.class));
    }

    @Test
    void post_shouldReturnBadRequestWhenDoctorIdIsNull() throws Exception {
        mockMvc.perform(
                post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "patientId": 1,
                                      "appointmentDateTime": "2026-09-25T10:00:00",
                                      "status": "SCHEDULED",
                                      "reason": "Consultation"
                                    }
                                """)
        ).andExpect(status().isBadRequest());
        verify(appointmentService, never()).create(any(AppointmentRequest.class));
    }

    @Test
    void post_shouldReturnConflictWhenDoctorTimeIsBusy() throws Exception {

        when(appointmentService.create(any(AppointmentRequest.class)))
                .thenThrow(
                        new AppointmentTimeConflictException("Doctor already has an appointment at this time")
                );
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "doctorId": 1,
                                  "patientId": 1,
                                  "appointmentDateTime": "2026-09-25T10:00:00",
                                  "status": "SCHEDULED",
                                  "reason": "Consultation"
                                }
                                """)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Doctor already has an appointment at this time"));

        verify(appointmentService)
                .create(any(AppointmentRequest.class));
    }

    @Test
    void getAll_shouldReturnAppointments() throws Exception {
        AppointmentResponse firstResponse = new AppointmentResponse(
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
        AppointmentResponse secondResponse = new AppointmentResponse(
                11L,
                1L,
                "Alex",
                "Smith",
                1L,
                "Maria",
                "Manas",
                LocalDateTime.of(2026, 9, 14, 10, 0),
                AppointmentStatus.COMPLETED,
                "Checking"
        );
        when(appointmentService.findAll())
                .thenReturn(List.of(firstResponse, secondResponse));

        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].doctorId").value(1))
                .andExpect(jsonPath("$[0].patientId").value(1))
                .andExpect(jsonPath("$[0].status").value("SCHEDULED"))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].doctorId").value(1))
                .andExpect(jsonPath("$[1].patientId").value(1));
        verify(appointmentService).findAll();
    }

    @Test
    void getById_shouldReturnAppointment() throws Exception {
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
                "Consultation");

        when(appointmentService.findById(10L))
                .thenReturn(response);
        mockMvc.perform(get("/api/appointments/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.doctorId").value(1))
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.appointmentDateTime").value("2026-09-25T10:00:00"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.reason").value("Consultation"));
        verify(appointmentService).findById(10L);
    }

    @Test
    void getById_shouldReturnNotFound() throws Exception {
        when(appointmentService.findById(999L))
                .thenThrow(new AppointmentNotFoundException("Appointment not found with id: " + 999L));
        mockMvc.perform(get("/api/appointments/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Appointment not found with id: " + 999L))
                .andExpect(jsonPath("$.status").value(404));

        verify(appointmentService).findById(999L);
    }

    @Test
    void put_shouldUpdateAppointment() throws Exception {
        AppointmentResponse response = new AppointmentResponse(
                10L,
                1L,
                "Alex",
                "Smith",
                1L,
                "Maria",
                "Manas",
                LocalDateTime.of(2026, 9, 25, 10, 0),
                AppointmentStatus.COMPLETED,
                "Checked"
        );

        when(appointmentService.update(
                eq(10L),
                any(AppointmentRequest.class)
        )).thenReturn(response);

        mockMvc.perform(put("/api/appointments/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                     "doctorId": 1,
                                     "patientId": 1,
                                     "appointmentDateTime": "2026-09-25T10:00:00",
                                     "status": "COMPLETED",
                                     "reason": "Checked"
                                    }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.reason").value("Checked"))
                .andExpect(jsonPath("$.appointmentDateTime")
                        .value("2026-09-25T10:00:00"));
        verify(appointmentService).update(eq(10L), any(AppointmentRequest.class));
    }

    @Test
    void put_shouldReturnNotFound() throws Exception {
        when(appointmentService.update(
                eq(999L),
                any(AppointmentRequest.class)))
                .thenThrow(new AppointmentNotFoundException(
                        "Appointment not found with id: " + 999L
                ));
        mockMvc.perform(put("/api/appointments/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                     "doctorId": 1,
                                     "patientId": 1,
                                     "appointmentDateTime": "2026-09-25T10:00:00",
                                     "status": "COMPLETED",
                                     "reason": "Checked"
                                    }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value("Appointment not found with id: " + 999L));
        verify(appointmentService).update(eq(999L), any(AppointmentRequest.class));
    }

    @Test
    void put_shouldReturnConflictWhenDoctorTimeIsBusy() throws Exception {
        when(appointmentService.update(
                eq(10L),
                any(AppointmentRequest.class)
        )).thenThrow(new AppointmentTimeConflictException("Doctor already has an appointment at this time"));

        mockMvc.perform(put("/api/appointments/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                     "doctorId": 1,
                                     "patientId": 1,
                                     "appointmentDateTime": "2026-09-25T10:00:00",
                                     "status": "COMPLETED",
                                     "reason": "Checked"
                                    }
                                """)
                ).andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Doctor already has an appointment at this time"));

        verify(appointmentService)
                .update(eq(10L), any(AppointmentRequest.class));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/appointments/{id}", 10L))
                .andExpect(status().isNoContent());
        verify(appointmentService).delete(10L);
    }

    @Test
    void delete_shouldReturnNotFound() throws Exception {
        doThrow(
                new AppointmentNotFoundException(
                        "Appointment not found with id: " + 999L
                )).when(appointmentService).delete(999L);

        mockMvc.perform(delete("/api/appointments/{id}", 999L))
                .andExpect(status()
                        .isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value("Appointment not found with id: " + 999L));
        verify(appointmentService).delete(999L);
    }
}
