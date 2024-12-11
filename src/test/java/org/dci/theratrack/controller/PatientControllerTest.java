package org.dci.theratrack.controller;

import jakarta.persistence.EntityNotFoundException;
import org.dci.theratrack.config.TestSecurityConfig;
import org.dci.theratrack.entity.Patient;
import org.dci.theratrack.exceptions.GlobalExceptionHandler;
import org.dci.theratrack.exceptions.ResourceNotFoundException;
import org.dci.theratrack.repository.PatientRepository;
import org.dci.theratrack.request.PatientRequest;
import org.dci.theratrack.service.AppointmentService;
import org.dci.theratrack.service.PatientService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {TestSecurityConfig.class, PatientController.class})
@WebMvcTest(PatientController.class)
@Import(GlobalExceptionHandler.class)
class PatientControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PatientService patientService;

  @MockBean
  private PatientRepository patientRepository;

  @MockBean
  private AppointmentService appointmentService;

  @BeforeEach
  void setup() {
    Patient patient = new Patient();
    patient.setId(1L);
    patient.setName("John");
    patient.setSurname("Doe");
    patientRepository.save(patient);
  }

  @Test
  void testGetAllPatients() throws Exception {
    Patient patient = new Patient();
    patient.setId(1L);
    patient.setName("John");
    patient.setSurname("Doe");

    when(patientService.getAllPatients()).thenReturn(Arrays.asList(patient));

    mockMvc.perform(get("/api/patients"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("John"))
        .andExpect(jsonPath("$[0].surname").value("Doe"));

    verify(patientService, times(1)).getAllPatients();
  }

  @Test
  void testGetPatientById() throws Exception {
    Patient patient = new Patient();
    patient.setId(1L);
    patient.setName("John");
    patient.setSurname("Doe");

    when(patientService.getPatientById(1L)).thenReturn(patient);

    mockMvc.perform(get("/api/patients/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("John"))
        .andExpect(jsonPath("$.surname").value("Doe"));

    verify(patientService, times(1)).getPatientById(1L);
  }

  @Test
  void testGetPatientById_ServiceFailure() throws Exception {
    // Simulate an exception thrown by the service layer
    when(patientService.getPatientById(1L)).thenThrow(new RuntimeException("Database error"));

    // Perform the GET request and assert that the response status is 500 Internal Server Error
    mockMvc.perform(get("/api/patients/1"))
        .andExpect(status().isInternalServerError());

    // Verify that the service method was called
    verify(patientService, times(1)).getPatientById(1L);
  }

  @Test
  void testGetPatientById_InvalidIdFormat() throws Exception {
    // Perform the GET request with an invalid ID (non-numeric)
    mockMvc.perform(get("/api/patients/abc")) // Invalid ID format
        .andExpect(status().isBadRequest());

    // Verify that the service method was not called
    verify(patientService, times(0)).getPatientById(anyLong());
  }

  @Test
  void testGetPatientById_MethodNotAllowed() throws Exception {
    // Perform a POST request instead of GET
    mockMvc.perform(post("/api/patients/1"))
        .andExpect(status().isMethodNotAllowed());

    // Verify that the service method was not called
    verify(patientService, times(0)).getPatientById(anyLong());
  }

  @Test
  void testUpdatePatient() throws Exception {
    Patient patient = new Patient();
    patient.setId(1L);
    patient.setName("Updated Name");
    patient.setSurname("Updated Surname");

    when(patientService.updatePatient(eq(1L), any(Patient.class))).thenReturn(patient);

    String patientJson = "{ \"name\": \"Updated Name\", \"surname\": \"Updated Surname\" }";

    mockMvc.perform(put("/api/patients/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(patientJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Updated Name"))
        .andExpect(jsonPath("$.surname").value("Updated Surname"));

    verify(patientService, times(1)).updatePatient(eq(1L), any(Patient.class));
  }

  @Test
  void testUpdatePatient_NotFound() throws Exception {
    // Simulate the service throwing an exception for a non-existent patient
    when(patientService.updatePatient(eq(1L), any(Patient.class)))
        .thenThrow(new EntityNotFoundException("Patient not found"));

    String patientJson = "{ \"name\": \"Updated Name\", \"surname\": \"Updated Surname\" }";

    mockMvc.perform(put("/api/patients/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(patientJson))
        .andExpect(status().isNotFound())
        .andExpect(content().string("{\"details\":\"Patient not found\",\"error\":\"Entity not found\"}"));

    verify(patientService, times(1)).updatePatient(eq(1L), any(Patient.class));
  }

  @Test
  void testUpdatePatient_InvalidJson() throws Exception {
    String invalidJson = "{ \"name\": \"Updated Name\", \"surname\": }"; // Malformed JSON

    mockMvc.perform(put("/api/patients/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest());

    verify(patientService, times(0)).updatePatient(anyLong(), any(Patient.class));
  }

  @Test
  void testUpdatePatient_InvalidInput() throws Exception {
    String invalidPatientJson = "{ \"name\": \"\" }"; // Missing surname and name is empty

    mockMvc.perform(put("/api/patients/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidPatientJson))
        .andExpect(status().isBadRequest());

    verify(patientService, times(0)).updatePatient(anyLong(), any(Patient.class));
  }

  @Test
  void testDeletePatient() throws Exception {
    doNothing().when(patientService).deletePatient(1L);

    mockMvc.perform(delete("/api/patients/1"))
        .andExpect(status().isNoContent());

    verify(patientService, times(1)).deletePatient(1L);
  }

  @Test
  void testDeletePatient_NotFound() throws Exception {
    // Simulate the service throwing a ResourceNotFoundException
    doThrow(new ResourceNotFoundException("Patient not found")).when(patientService).deletePatient(1L);

    // Perform the DELETE request and expect 404 Not Found status
    mockMvc.perform(delete("/api/patients/1"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Patient not found"));

    // Verify that the service method was called
    verify(patientService, times(1)).deletePatient(1L);
  }

  @Test
  void testDeletePatient_ServiceFailure() throws Exception {
    // Simulate the service throwing a RuntimeException
    doThrow(new RuntimeException("Database error")).when(patientService).deletePatient(1L);

    // Perform the DELETE request and expect 500 Internal Server Error status
    mockMvc.perform(delete("/api/patients/1"))
        .andExpect(status().isInternalServerError());

    // Verify that the service method was called
    verify(patientService, times(1)).deletePatient(1L);
  }

  @Test
  void testCreatePatient() throws Exception {
    Patient patient = new Patient();
    patient.setId(1L);
    patient.setName("John");
    patient.setSurname("Doe");

    when(patientService.createPatient(any(PatientRequest.class))).thenReturn(patient);

    String patientJson = "{ \"name\": \"John\", \"surname\": \"Doe\" }";

    mockMvc.perform(post("/api/patients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(patientJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("John"))
        .andExpect(jsonPath("$.surname").value("Doe"));

    verify(patientService, times(1)).createPatient(any(PatientRequest.class));
  }

  @Test
  void testCreatePatient_InvalidJson() throws Exception {
    // Malformed JSON
    String patientJson = "{ \"name\": \"John\", \"surname\": \"Doe\" "; // Missing closing brace

    // Perform the POST request and expect 400 Bad Request status
    mockMvc.perform(post("/api/patients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(patientJson))
        .andExpect(status().isBadRequest());

    // Verify that the service method was not called
    verify(patientService, times(0)).createPatient(any(PatientRequest.class));
  }

  @Test
  void testCreatePatient_ServiceFailure() throws Exception {
    // Simulate the service throwing a RuntimeException
    when(patientService.createPatient(any(PatientRequest.class)))
        .thenThrow(new RuntimeException("Database error"));

    String patientJson = "{ \"name\": \"John\", \"surname\": \"Doe\" }";

    // Perform the POST request and expect 500 Internal Server Error status
    mockMvc.perform(post("/api/patients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(patientJson))
        .andExpect(status().isInternalServerError());

    // Verify that the service method was called once
    verify(patientService, times(1)).createPatient(any(PatientRequest.class));
  }


}

