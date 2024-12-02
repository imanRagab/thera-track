package org.dci.theratrack.controller;

import org.dci.theratrack.config.TestSecurityConfig;
import org.dci.theratrack.request.LoginRequest;
import org.dci.theratrack.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = {TestSecurityConfig.class, AuthController.class})
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private LoginRequest validRequest;
    private LoginRequest invalidRequest;

    @BeforeEach
    void setUp() {
        validRequest = new LoginRequest();
        validRequest.setUsername("user");
        validRequest.setPassword("password");

        invalidRequest = new LoginRequest();
        invalidRequest.setUsername("invalidUser");
        invalidRequest.setPassword("wrongPassword");
    }

    @Test
    void testLoginSuccess() throws Exception {
        // Mocking AuthService behavior for successful authentication
        when(authService.authenticate(validRequest.getUsername(), validRequest.getPassword()))
            .thenReturn("mockedToken");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"user\", \"password\": \"password\"}"))
            .andExpect(status().isOk())
            .andExpect(content().string("mockedToken"));
    }

    @Test
    void testLoginFailure() throws Exception {
        // Mocking AuthService to throw an exception for failed authentication
        when(authService.authenticate(invalidRequest.getUsername(), invalidRequest.getPassword()))
            .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"invalidUser\", \"password\": \"wrongPassword\"}"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().string("Invalid credentials"));
    }

}
