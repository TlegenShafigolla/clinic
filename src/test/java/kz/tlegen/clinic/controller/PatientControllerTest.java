package kz.tlegen.clinic.controller;


import kz.tlegen.clinic.dto.patient.PatientRequest;
import kz.tlegen.clinic.dto.patient.PatientResponse;
import kz.tlegen.clinic.exception.PatientNotFoundException;
import kz.tlegen.clinic.service.PatientService;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PatientController.class)
class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService service;

    @Test
    void create_shouldReturnCreatedPatient() throws Exception {
        PatientResponse response = new PatientResponse(
                1L,
                "Alex",
                "Smith",
                LocalDate.of(2000, 5, 10),
                "+77001234567",
                true
        );

        when(service.create(any(PatientRequest.class)))
                .thenReturn(response);
        mockMvc.perform(
                        post("/api/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                         {
                                          "firstName": "Alex",
                                          "lastName": "Smith",
                                          "birthDate": "2000-05-10",
                                          "phone": "+77001234567",
                                          "active": true
                                          }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Alex"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Smith"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthDate").value("2000-05-10"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("+77001234567"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.active").value(true));
    }

    @Test
    void create_shouldReturnBadRequest_whenFirstNameIsBlank() throws Exception {
        mockMvc.perform(
                post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 {
                                           "firstName": "",
                                           "lastName": "Smith",
                                           "birthDate": "2000-05-10",
                                           "phone": "+77001234567",
                                           "active": true
                                           }
                                """)
        ).andExpect(status().isBadRequest());
        verify(service, never()).create(any(PatientRequest.class));
    }

    @Test
    void findAll_shouldReturnAllPatients() throws Exception {
        PatientResponse firstResponse = new PatientResponse(
                1L,
                "Alex",
                "Smith",
                LocalDate.of(2000, 5, 10),
                "+77001234567",
                true
        );
        PatientResponse secondResponse = new PatientResponse(
                2L,
                "Maria",
                "Kiss",
                LocalDate.of(2005, 6, 14),
                "+77001267569",
                true
        );
        when(service.findAll())
                .thenReturn(List.of(firstResponse, secondResponse));
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Alex"))
                .andExpect(jsonPath("$[1].firstName").value("Maria"));
    }

    @Test
    void findById_shouldReturnPatient_whenPatientExists() throws Exception {
        PatientResponse response = new PatientResponse(
                1L,
                "Alex",
                "Smith",
                LocalDate.of(2000, 5, 10),
                "+77001234567",
                true
        );
        when(service.findById(1L))
                .thenReturn(response);
        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alex"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.birthDate").value("2000-05-10"))
                .andExpect(jsonPath("$.phone").value("+77001234567"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void findById_shouldReturnNotFound_whenPatientDoesNotExist() throws Exception {
        when(service.findById(999L))
                .thenThrow(new PatientNotFoundException("Patient not found with id: 999"));
        mockMvc.perform(get("/api/patients/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found with id: 999"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void update_shouldReturnUpdatedPatient() throws Exception {
        PatientResponse response = new PatientResponse(1L,
                "Alex",
                "Brown",
                LocalDate.of(2000, 5, 14),
                "+77001239567",
                false);
        when(service.update(
                eq(1L),
                any(PatientRequest.class)
        )).thenReturn(response);

        mockMvc.perform(put("/api/patients/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {
                                                  "firstName": "Alex",
                                                  "lastName": "Brown",
                                                  "birthDate": "2000-05-14",
                                                  "phone": "+77001239567",
                                                  "active": false
                                                  }
                                """
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alex"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.birthDate").value("2000-05-14"))
                .andExpect(jsonPath("$.phone").value("+77001239567"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void update_shouldReturnNotFound_whenPatientDoesNotExist() throws Exception {
        when(service.update(eq(999L), any(PatientRequest.class))).thenThrow(new PatientNotFoundException("Patient not found with id: 999"));

        mockMvc.perform(put("/api/patients/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {
                                                  "firstName": "Alex",
                                                  "lastName": "Smith",
                                                  "birthDate": "2000-05-10",
                                                  "phone": "+77001234567",
                                                  "active": true
                                                  }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found with id: 999"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void delete_shouldReturnNoContent_whenPatientExists() throws Exception {
        doNothing().when(service).delete(1L);
        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isNoContent());
        verify(service).delete(1L);
    }

    @Test
    void delete_shouldReturnNotFound_whenPatientDoesNotExist() throws Exception {
        doThrow(new PatientNotFoundException("Patient not found with id: 999"))
                .when(service).delete(999L);
        mockMvc.perform(delete("/api/patients/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found with id: 999"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
