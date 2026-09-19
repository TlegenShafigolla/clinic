package kz.tlegen.clinic.controller;

import kz.tlegen.clinic.dto.user.UserRequest;
import kz.tlegen.clinic.dto.user.UserResponse;
import kz.tlegen.clinic.entity.Role;
import kz.tlegen.clinic.exception.UserAlreadyExistsException;
import kz.tlegen.clinic.exception.UserNotFoundException;
import kz.tlegen.clinic.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void create_shouldReturnCreatedUser() throws Exception {
        UserResponse userResponse = new UserResponse(
                1L,
                "alex@gmail.com",
                Role.ADMIN,
                true
        );
        when(userService.create(any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                           "email":"alex@gmail.com",
                                           "password":"Qwerty123",
                                           "role":"ADMIN",
                                           "active":true
                                        }
                                        """)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.active").value(true));

        verify(userService)
                .create(any(UserRequest.class));
    }

    @Test
    void create_shouldReturnBadRequestWhenPasswordTooShort() throws Exception {
        mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                   "email":"alex@gmail.com",
                                   "password":"1234",
                                   "role":"ADMIN",
                                   "active":true
                                }
                                """)
        ).andExpect(status().isBadRequest());
        verify(userService, never()).create(any(UserRequest.class));
    }

    @Test
    void create_shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        when(userService.create(any(UserRequest.class))).thenThrow(new UserAlreadyExistsException("User already exists"));
        mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                   "email":"alex@gmail.com",
                                   "password":"Qwerty123",
                                   "role":"ADMIN",
                                   "active":true
                                }
                                """)
        ).andExpect(status().isConflict());
        verify(userService).create(any(UserRequest.class));
    }

    @Test
    void getAll_shouldReturnUsers() throws Exception {
        UserResponse firstUserResponse = new UserResponse(
                1L,
                "alex@gmail.com",
                Role.ADMIN,
                true
        );
        UserResponse secondUserResponse = new UserResponse(
                2L,
                "maks@gmail.com",
                Role.DOCTOR,
                true
        );

        when(userService.findAll()).thenReturn(List.of(firstUserResponse, secondUserResponse));
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value(firstUserResponse.getEmail()))
                .andExpect(jsonPath("$[0].role").value("ADMIN"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value(secondUserResponse.getEmail()))
                .andExpect(jsonPath("$[1].role").value("DOCTOR"))
                .andExpect(jsonPath("$[1].active").value(true));
        verify(userService).findAll();
    }

    @Test
    void getById_shouldReturnUser() throws Exception {
        UserResponse userResponse = new UserResponse(
                1L,
                "alex@gmail.com",
                Role.ADMIN,
                true
        );
        when(userService.findById(1L)).thenReturn(userResponse);
        mockMvc.perform(
                        get("/api/users/{id}", 1L)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.active").value(true));

        verify(userService).findById(1L);
    }

    @Test
    void getById_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        when(userService.findById(999L)).thenThrow(new UserNotFoundException("User not found with id: " + 999L));
        mockMvc.perform(
                        get("/api/users/{id}", 999L)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("User not found with id: 999"));
        verify(userService).findById(999L);
    }

    @Test
    void update_shouldReturnUpdatedUser() throws Exception {
        UserResponse userResponse = new UserResponse(
                1L,
                "alex@gmail.com",
                Role.DOCTOR,
                false
        );
        when(userService.update(eq(1L), any(UserRequest.class)))
                .thenReturn(userResponse);
        mockMvc.perform(
                        put("/api/users/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                           {
                                           "email":"alex@gmail.com",
                                           "password":"Qwerty123",
                                           "role":"DOCTOR",
                                           "active":false
                                           }
                                        """
                                )
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("$.role").value("DOCTOR"))
                .andExpect(jsonPath("$.active").value(false));
        verify(userService).update(eq(1L), any(UserRequest.class));
    }

    @Test
    void update_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        when(userService.update(eq(999L), any(UserRequest.class)))
                .thenThrow(new UserNotFoundException("User not found with id: " + 999L));
        mockMvc.perform(
                        put("/api/users/{id}", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                         {
                                        "email":"alex@gmail.com",
                                         "password":"Qwerty123",
                                         "role":"DOCTOR",
                                         "active":false
                                                     }
                                        """
                                )
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found with id: 999"));

        verify(userService).update(eq(999L), any(UserRequest.class));
    }

    @Test
    void update_shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        when(userService.update(eq(1L), any(UserRequest.class))).thenThrow(new UserAlreadyExistsException("User already exists"));
        mockMvc.perform(
                        put("/api/users/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                         {
                                        "email":"alex@gmail.com",
                                         "password":"Qwerty123",
                                         "role":"DOCTOR",
                                         "active":false
                                                     }
                                        """)
                ).andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("User already exists"));
        verify(userService).update(eq(1L), any(UserRequest.class));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(
                delete("/api/users/{id}", 1L)
        ).andExpect(status().isNoContent());
        verify(userService).delete(1L);
    }

    @Test
    void delete_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        doThrow(new UserNotFoundException("User not found with id: " + 1L)).when(userService).delete(1L);
        mockMvc.perform(
                        delete("/api/users/{id}", 1L)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found with id: 1"));
        verify(userService).delete(1L);
    }
}
