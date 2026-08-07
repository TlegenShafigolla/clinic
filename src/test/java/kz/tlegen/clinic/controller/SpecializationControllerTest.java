package kz.tlegen.clinic.controller;

import kz.tlegen.clinic.dto.specialization.SpecializationRequest;
import kz.tlegen.clinic.dto.specialization.SpecializationResponse;
import kz.tlegen.clinic.exception.SpecializationAlreadyExistsException;
import kz.tlegen.clinic.exception.SpecializationNotFoundException;
import kz.tlegen.clinic.service.SpecializationService;
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

@WebMvcTest(SpecializationController.class)
class SpecializationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpecializationService service;

    @Test
    void create_shouldReturn201AndResponse_whenRequestIsValid() throws Exception {
        SpecializationResponse response = new SpecializationResponse(1L, "Cardiology");
        when(service.create(any(SpecializationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/specializations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "Cardiology"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Cardiology"));

    }


    @Test
    void create_shouldReturn400_whenNameIsBlank() throws Exception {
        mockMvc.perform(
                        post("/api/specializations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": ""
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Specialization name must not be blank"));

        verify(service, never())
                .create(any(SpecializationRequest.class));
    }

    @Test
    void create_shouldReturn409_whenSpecializationAlreadyExists() throws Exception {
        when(service.create(any(SpecializationRequest.class)))
                .thenThrow(
                        new SpecializationAlreadyExistsException(
                                "Specialization already exists: Cardiology"
                        )
                );
        mockMvc.perform(
                        post("/api/specializations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "Cardiology"
                                        }
                                        """)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Specialization already exists: Cardiology"));
    }

    @Test
    void findAll_shouldReturn200AndResponses() throws Exception {
        List<SpecializationResponse> responses = List.of(
                new SpecializationResponse(1L, "Cardiology"),
                new SpecializationResponse(2L, "Neurology")
        );
        when(service.findAll()).thenReturn(responses);
        mockMvc.perform(
                        get("/api/specializations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Cardiology"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Neurology"));

    }

    @Test
    void findById_shouldReturn200AndResponse_whenSpecializationExists()
            throws Exception {
        SpecializationResponse response =
                new SpecializationResponse(2L, "Neurology");
        when(service.findById(2L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/specializations/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Neurology"));

    }

    @Test
    void findById_shouldReturn404_whenSpecializationDoesNotExist()
            throws Exception {
        when(service.findById(999L))
                .thenThrow(
                        new SpecializationNotFoundException(
                                "Specialization not found with id: 999"
                        )
                );
        mockMvc.perform(
                        get("/api/specializations/{id}", 999L)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Specialization not found with id: 999"));
    }

    @Test
    void update_shouldReturn200AndResponse_whenRequestIsValid()
            throws Exception {
        SpecializationResponse response =
                new SpecializationResponse(2L, "Dermatologist");

        when(service.update(
                eq(2L),
                any(SpecializationRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/specializations/{id}", 2L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "Dermatologist"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Dermatologist"));
    }

    @Test
    void update_shouldReturn404_whenSpecializationDoesNotExist()
            throws Exception {
        when(service.update(
                eq(999L),
                any(SpecializationRequest.class)
        )).thenThrow(
                new SpecializationNotFoundException(
                        "Specialization not found with id: 999"
                )
        );

        mockMvc.perform(
                        put("/api/specializations/{id}", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "Dermatologist"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Specialization not found with id: 999"));
    }

    @Test
    void update_shouldReturn409_whenSpecializationAlreadyExists()
            throws Exception {
        when(service.update(
                eq(2L),
                any(SpecializationRequest.class)
        )).thenThrow(
                new SpecializationAlreadyExistsException(
                        "Specialization already exists: Neurology"
                )
        );
        mockMvc.perform(
                        put("/api/specializations/{id}", 2L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "Neurology"
                                        }
                                        """)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Specialization already exists: Neurology"));
    }

    @Test
    void delete_shouldReturn204_whenSpecializationExists()
            throws Exception {
        mockMvc.perform(
                delete("/api/specializations/{id}", 2L)
        ).andExpect(status().isNoContent());

        verify(service).delete(2L);
    }

    @Test
    void delete_shouldReturn404_whenSpecializationDoesNotExist()
            throws Exception {
        doThrow(
                new SpecializationNotFoundException(
                        "Specialization not found with id: 999"
                )
        ).when(service).delete(999L);

        mockMvc.perform(
                        delete("/api/specializations/{id}", 999L)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Specialization not found with id: 999"));
    }
}