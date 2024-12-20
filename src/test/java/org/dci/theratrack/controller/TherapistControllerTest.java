package org.dci.theratrack.controller;

import org.dci.theratrack.config.TestSecurityConfig;
import org.dci.theratrack.entity.Therapist;
import org.dci.theratrack.exceptions.GlobalExceptionHandler;
import org.dci.theratrack.exceptions.ResourceNotFoundException;
import org.dci.theratrack.repository.TherapistRepository;
import org.dci.theratrack.request.TherapistRequest;
import org.dci.theratrack.service.TherapistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = {TestSecurityConfig.class, TherapistController.class})
@WebMvcTest(TherapistController.class)
@Import(GlobalExceptionHandler.class)
class TherapistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TherapistService therapistService;

    @MockBean
    private TherapistRepository therapistRepository;

    @BeforeEach
    void setup() {
        Therapist therapist = new Therapist();
        therapist.setId(1L);
        therapist.setName("John");
        therapist.setSurname("Doe");
        therapistRepository.save(therapist);
    }

    @Test
    void testGetAllTherapists() throws Exception {
        Therapist therapist = new Therapist();
        therapist.setId(1L);
        therapist.setName("John");
        therapist.setSurname("Doe");

        when(therapistService.getAllTherapists()).thenReturn(Arrays.asList(therapist));

        mockMvc.perform(get("/api/therapists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].surname").value("Doe"));

        verify(therapistService, times(1)).getAllTherapists();
    }

    @Test
    void testGetTherapistById() throws Exception {
        Therapist therapist = new Therapist();
        therapist.setId(1L);
        therapist.setName("John");
        therapist.setSurname("Doe");

        when(therapistService.getTherapistById(1L)).thenReturn(therapist);

        mockMvc.perform(get("/api/therapists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"));

        verify(therapistService, times(1)).getTherapistById(1L);
    }

    @Test
    void testGetTherapistById_NotFound() throws Exception {
        // Simulate the service throwing a ResourceNotFoundException
        when(therapistService.getTherapistById(1L)).thenThrow(new ResourceNotFoundException("Therapist not found"));

        // Perform the GET request and expect 404 Not Found status
        mockMvc.perform(get("/api/therapists/1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Therapist not found"));

        // Verify that the service method was called once
        verify(therapistService, times(1)).getTherapistById(1L);
    }

    @Test
    void testGetTherapistById_InvalidIdFormat() throws Exception {
        // Perform the GET request with an invalid ID format and expect 400 Bad Request status
        mockMvc.perform(get("/api/therapists/invalid-id"))
            .andExpect(status().isBadRequest());

        // Verify that the service method was not called
        verify(therapistService, times(0)).getTherapistById(anyLong());
    }

    @Test
    void testGetTherapistById_ServiceFailure() throws Exception {
        // Simulate the service throwing a RuntimeException
        when(therapistService.getTherapistById(1L)).thenThrow(new RuntimeException("Database error"));

        // Perform the GET request and expect 500 Internal Server Error status
        mockMvc.perform(get("/api/therapists/1"))
            .andExpect(status().isInternalServerError());

        // Verify that the service method was called once
        verify(therapistService, times(1)).getTherapistById(1L);
    }


    @Test
    void testUpdateTherapist() throws Exception {
        Therapist therapist = new Therapist();
        therapist.setId(1L);
        therapist.setName("Updated Name");
        therapist.setSurname("Updated Surname");

        when(therapistService.updateTherapist(eq(1L), any(Therapist.class))).thenReturn(therapist);

        String therapistJson = "{ \"name\": \"Updated Name\", \"surname\": \"Updated Surname\" }";

        mockMvc.perform(put("/api/therapists/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(therapistJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.surname").value("Updated Surname"));

        verify(therapistService, times(1)).updateTherapist(eq(1L), any(Therapist.class));
    }

    @Test
    void testUpdateTherapist_NotFound() throws Exception {
        // Simulate the service throwing a ResourceNotFoundException
        when(therapistService.updateTherapist(eq(1L), any(Therapist.class)))
            .thenThrow(new ResourceNotFoundException("Therapist not found"));

        String therapistJson = "{ \"name\": \"Updated Name\", \"surname\": \"Updated Surname\" }";

        // Perform the PUT request and expect 404 Not Found status
        mockMvc.perform(put("/api/therapists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(therapistJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Therapist not found"));

        // Verify that the service method was called
        verify(therapistService, times(1)).updateTherapist(eq(1L), any(Therapist.class));
    }

    @Test
    void testUpdateTherapist_InvalidIdFormat() throws Exception {
        String therapistJson = "{ \"name\": \"Updated Name\", \"surname\": \"Updated Surname\" }";

        // Perform the PUT request with an invalid ID format and expect 400 Bad Request status
        mockMvc.perform(put("/api/therapists/invalid-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(therapistJson))
            .andExpect(status().isBadRequest());

        // Verify that the service method was not called
        verify(therapistService, times(0)).updateTherapist(anyLong(), any(Therapist.class));
    }

    @Test
    void testUpdateTherapist_MissingFields() throws Exception {
        String therapistJson = "{ \"surname\": \"Updated Surname\" }"; // Missing "name" field

        // Perform the PUT request and expect 400 Bad Request status
        mockMvc.perform(put("/api/therapists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(therapistJson))
            .andExpect(status().isBadRequest());

        // Verify that the service method was not called
        verify(therapistService, times(0)).updateTherapist(anyLong(), any(Therapist.class));
    }

    @Test
    void testUpdateTherapist_InvalidJson() throws Exception {
        String invalidJson = "{ \"name\": \"Updated Name\", \"surname\": "; // Malformed JSON

        // Perform the PUT request and expect 400 Bad Request status
        mockMvc.perform(put("/api/therapists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());

        // Verify that the service method was not called
        verify(therapistService, times(0)).updateTherapist(anyLong(), any(Therapist.class));
    }

    @Test
    void testUpdateTherapist_ServiceFailure() throws Exception {
        // Simulate the service throwing a RuntimeException
        when(therapistService.updateTherapist(eq(1L), any(Therapist.class)))
            .thenThrow(new RuntimeException("Database error"));

        String therapistJson = "{ \"name\": \"Updated Name\", \"surname\": \"Updated Surname\" }";

        // Perform the PUT request and expect 500 Internal Server Error status
        mockMvc.perform(put("/api/therapists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(therapistJson))
            .andExpect(status().isInternalServerError());

        // Verify that the service method was called
        verify(therapistService, times(1)).updateTherapist(eq(1L), any(Therapist.class));
    }


    @Test
    void testDeleteTherapist() throws Exception {
        doNothing().when(therapistService).deleteTherapist(1L);

        mockMvc.perform(delete("/api/therapists/1"))
                .andExpect(status().isNoContent());

        verify(therapistService, times(1)).deleteTherapist(1L);
    }

    @Test
    void testDeleteTherapist_NotFound() throws Exception {
        // Simulate the service throwing a ResourceNotFoundException
        doThrow(new ResourceNotFoundException("Therapist not found")).when(therapistService).deleteTherapist(1L);

        // Perform the DELETE request and expect 404 Not Found status
        mockMvc.perform(delete("/api/therapists/1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Therapist not found"));

        // Verify that the service method was called
        verify(therapistService, times(1)).deleteTherapist(1L);
    }

    @Test
    void testDeleteTherapist_InvalidIdFormat() throws Exception {
        // Perform the DELETE request with an invalid ID format and expect 400 Bad Request status
        mockMvc.perform(delete("/api/therapists/invalid-id"))
            .andExpect(status().isBadRequest());

        // Verify that the service method was not called
        verify(therapistService, times(0)).deleteTherapist(anyLong());
    }

    @Test
    void testDeleteTherapist_ServiceFailure() throws Exception {
        // Simulate the service throwing a RuntimeException
        doThrow(new RuntimeException("Database error")).when(therapistService).deleteTherapist(1L);

        // Perform the DELETE request and expect 500 Internal Server Error status
        mockMvc.perform(delete("/api/therapists/1"))
            .andExpect(status().isInternalServerError());

        // Verify that the service method was called
        verify(therapistService, times(1)).deleteTherapist(1L);
    }


    @Test
    void testCreateTherapist() throws Exception {
        Therapist therapist = new Therapist();
        therapist.setId(1L);
        therapist.setName("John");
        therapist.setSurname("Doe");

        when(therapistService.createTherapist(any(TherapistRequest.class))).thenReturn(therapist);

        String therapistJson = "{ \"name\": \"John\", \"surname\": \"Doe\" }";

        mockMvc.perform(post("/api/therapists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(therapistJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"));

        verify(therapistService, times(1)).createTherapist(any(TherapistRequest.class));
    }

    @Test
    void testCreateTherapist_InvalidJson() throws Exception {
        // Invalid JSON format
        String therapistJson = "{ \"name\": \"John\", \"surname\": \"Doe\" ";

        // Perform the POST request and expect 400 Bad Request status
        mockMvc.perform(post("/api/therapists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(therapistJson))
            .andExpect(status().isBadRequest());

        // Verify that the service method was not called
        verify(therapistService, times(0)).createTherapist(any(TherapistRequest.class));
    }

    @Test
    void testCreateTherapist_ServiceFailure() throws Exception {
        // Simulate the service throwing a RuntimeException
        when(therapistService.createTherapist(any(TherapistRequest.class)))
            .thenThrow(new RuntimeException("Database error"));

        String therapistJson = "{ \"name\": \"John\", \"surname\": \"Doe\" }";

        // Perform the POST request and expect 500 Internal Server Error status
        mockMvc.perform(post("/api/therapists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(therapistJson))
            .andExpect(status().isInternalServerError());

        // Verify that the service method was called once
        verify(therapistService, times(1)).createTherapist(any(TherapistRequest.class));
    }



}

