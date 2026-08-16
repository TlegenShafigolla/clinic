package kz.tlegen.clinic.controller;

import kz.tlegen.clinic.dto.doctor.DoctorRequest;
import kz.tlegen.clinic.dto.doctor.DoctorResponse;
import kz.tlegen.clinic.dto.specialization.SpecializationRequest;
import kz.tlegen.clinic.exception.DoctorNotFoundException;
import kz.tlegen.clinic.exception.SpecializationNotFoundException;
import kz.tlegen.clinic.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService service;

    @Test
    void create_shouldReturnCreatedDoctor() throws Exception {
        DoctorResponse response = new DoctorResponse(
                1L,
                "Alex",
                "Smith",
                5,
                true,
                1L,
                "Cardiology"
        );
        when(service.create(any(DoctorRequest.class)))
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
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alex"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.experienceYears").value(5))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.specializationId").value(1))
                .andExpect(jsonPath("$.specializationName").value("Cardiology"));
    }

    @Test
    void create_shouldReturnBadRequest_whenFirstNameIsBlank() throws Exception {
        mockMvc.perform(
                post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                      {
                                "firstName": "",
                                "lastName": "Smith",
                                "experienceYears": 5,
                                "active": true,
                                "specializationId": 1
                                      }
                                """)
        ).andExpect(status().isBadRequest());
        verify(service, never()).create(any(DoctorRequest.class));
    }

    @Test
    void create_shouldReturnNotFound_whenSpecializationDoesNotExist() throws Exception {
        when(service.create(any(DoctorRequest.class)))
                .thenThrow(new SpecializationNotFoundException("Specialization Not Found"));
        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                              {
                                        "firstName": "Alex",
                                        "lastName": "Smith",
                                        "experienceYears": 5,
                                        "active": true,
                                        "specializationId": 999
                                              }
                                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Specialization Not Found"));

    }

    @Test
    void findAll_shouldReturnAllDoctors() throws Exception {
        DoctorResponse firstDoctorResponse = new DoctorResponse(
                1L,
                "Alex",
                "Smith",
                5,
                true,
                1L,
                "Cardiology");
        DoctorResponse secondDoctorResponse = new DoctorResponse(
                2L,
                "Maria",
                "Askar",
                5,
                true,
                1L,
                "Cardiology");
        when(service.findAll())
                .thenReturn(List.of(firstDoctorResponse, secondDoctorResponse));
        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Alex"))
                .andExpect(jsonPath("$[1].firstName").value("Maria"));

    }

    @Test
    void findById_shouldReturnDoctor_whenDoctorExists() throws Exception {
        DoctorResponse response = new DoctorResponse(1L,
                "Alex",
                "Smith",
                5,
                true,
                1L,
                "Cardiology");
        when(service.findById(1L))
                .thenReturn(response);
        mockMvc.perform(get("/api/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alex"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.specializationName").value("Cardiology"));
    }

    @Test
    void findById_shouldReturnNotFound_whenDoctorDoesNotExist() throws Exception {
        when(service.findById(999L))
                .thenThrow(new DoctorNotFoundException("Doctor not found with id: 999"));
        mockMvc.perform(
                        get("/api/doctors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Doctor not found with id: 999"));

    }

    @Test
    void update_shouldReturnUpdatedDoctor() throws Exception {
        DoctorResponse doctorResponse = new DoctorResponse(
                1L,
                "Alex",
                "Brown",
                7,
                true,
                2L,
                "Neurology");

        when(service.update(
                eq(1L),
                any(DoctorRequest.class)
        )).thenReturn(doctorResponse);

        mockMvc.perform(put("/api/doctors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "firstName": "Alex",
                                "lastName": "Brown",
                                "experienceYears": 7,
                                "active": true,
                                "specializationId": 2
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.experienceYears").value(7))
                .andExpect(jsonPath("$.specializationId").value(2))
                .andExpect(jsonPath("$.specializationName").value("Neurology"));

    }

    @Test
    void update_shouldReturnNotFound_whenDoctorDoesNotExist() throws Exception {
        when(service.update(
                eq(999L),
                any(DoctorRequest.class)
        )).thenThrow(
                new DoctorNotFoundException(
                        "Doctor not found with id: 999"
                )
        );

        mockMvc.perform(put("/api/doctors/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "firstName": "Alex",
                                "lastName": "Brown",
                                "experienceYears": 7,
                                "active": true,
                                "specializationId": 2
                                }
                                """)
                )
                .andExpect(status()
                        .isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value("Doctor not found with id: 999"));
    }

    @Test
    void update_shouldReturnNotFound_whenSpecializationDoesNotExist() throws Exception {
        when(service.update(eq(1L),
                any(DoctorRequest.class)))
                .thenThrow(new SpecializationNotFoundException("Specialization not found with id: 999"));
        mockMvc.perform(put("/api/doctors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "firstName": "Alex",
                                "lastName": "Brown",
                                "experienceYears": 7,
                                "active": true,
                                "specializationId": 999
                                }
                                """)
                )
                .andExpect(status()
                        .isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value("Specialization not found with id: 999"));
    }

    @Test
    void delete_shouldReturnNoContent_whenDoctorExists() throws Exception {
        doNothing().when(service).delete(1L);
        mockMvc.perform(delete("/api/doctors/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void delete_shouldReturnNotFound_whenDoctorDoesNotExist() throws Exception {
        doThrow(
                new DoctorNotFoundException(
                        "Doctor not found with id: 999"
                )
        ).when(service).delete(999L);

        mockMvc.perform(delete("/api/doctors/{id}", 999L))
                .andExpect(status()
                        .isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value("Doctor not found with id: 999"));

    }
}


