package kz.tlegen.clinic.controller;

import kz.tlegen.clinic.dto.auth.AuthResponse;
import kz.tlegen.clinic.dto.auth.LoginRequest;
import kz.tlegen.clinic.dto.auth.RegisterRequest;
import kz.tlegen.clinic.dto.user.UserResponse;
import kz.tlegen.clinic.entity.Role;
import kz.tlegen.clinic.exception.InvalidCredentialsException;
import kz.tlegen.clinic.exception.UserAlreadyExistsException;
import kz.tlegen.clinic.security.JwtAuthenticationFilter;
import kz.tlegen.clinic.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void post_shouldRegisterUser() throws Exception {
        UserResponse user = new UserResponse(
                1L,
                "alex@gmail.com",
                Role.PATIENT,
                true
        );
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(user);
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "email":"alex@gmail.com",
                                        "password":"Qwerty1234"
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.role").value("PATIENT"))
                .andExpect(jsonPath("$.active").value(true));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void register_shouldReturnBadRequestWhenPasswordTooShort() throws Exception {
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "email":"alex@gmail.com",
                                        "password":"1234"
                                        }
                                        """))
                .andExpect(status().isBadRequest());
        verify(authService, never()).register(any());
    }

    @Test
    void register_shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenThrow(
                new UserAlreadyExistsException("User already exists")
        );
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "email":"alex@gmail.com",
                                        "password":"Qwerty1234"
                                        }
                                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("User already exists"));

        verify(authService)
                .register(any(RegisterRequest.class));
    }

    @Test
    void login_shouldReturnOk() throws Exception {
        AuthResponse authResponse = new AuthResponse(
                "jwt-token"
        );
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                          {
                                          "email": "alex@gmail.com",
                                          "password": "Qwerty1234"
                                                   }
                                        """)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void login_shouldReturnUnauthorizedWhenCredentialsInvalid() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenThrow(
                new InvalidCredentialsException("Invalid email or password")
        );
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                              {
                                                  "email": "alex@gmail.com",
                                                  "password": "Qwerty1234"
                                                           }
                                        """)
                ).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void login_shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "email": "not-email",
                                          "password": "Qwerty123"
                                          }
                                        """
                                )
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        verify(authService, never()).login(any());
    }

}
