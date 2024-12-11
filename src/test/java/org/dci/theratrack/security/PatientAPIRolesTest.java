package org.dci.theratrack.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dci.theratrack.controller.PatientController;
import org.dci.theratrack.filter.JwtRequestFilter;
import org.dci.theratrack.service.PatientService;
import org.dci.theratrack.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
public class PatientAPIRolesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    @WithMockUser(username = "therapist1", roles = {"THERAPIST"})
    public void therapistCanAccessPatientProfile() throws Exception {
        mockMvc.perform(get("/api/patients/1")) // Replace with your endpoint
                .andExpect(status().isOk()); // Expect HTTP 200 for allowed access
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void adminCanAccessPatientProfile() throws Exception {
        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk()); // Expect HTTP 200 for admin access
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void regularUserCannotAccessPatientProfile() throws Exception {
        // Mock JWT Filter behavior to avoid interference
        doNothing().when(jwtRequestFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void unauthenticatedUserCannotAccessPatientProfile() throws Exception {
        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isUnauthorized()); // Expect HTTP 401 for unauthenticated users
    }
}
