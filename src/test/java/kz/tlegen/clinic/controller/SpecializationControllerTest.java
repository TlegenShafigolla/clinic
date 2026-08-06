package kz.tlegen.clinic.controller;

import kz.tlegen.clinic.dto.specialization.SpecializationRequest;
import kz.tlegen.clinic.dto.specialization.SpecializationResponse;
import kz.tlegen.clinic.exception.SpecializationAlreadyExistsException;
import kz.tlegen.clinic.service.SpecializationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
}