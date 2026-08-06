package kz.tlegen.clinic.controller;

import kz.tlegen.clinic.dto.specialization.SpecializationRequest;
import kz.tlegen.clinic.dto.specialization.SpecializationResponse;
import kz.tlegen.clinic.service.SpecializationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.mockito.Mockito.when;
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
}