package org.dci.theratrack.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.dci.theratrack.entity.Appointment;
import org.dci.theratrack.entity.Patient;
import org.dci.theratrack.entity.Therapist;
import org.dci.theratrack.enums.AppointmentStatus;
import org.dci.theratrack.exceptions.InvalidRequestException;
import org.dci.theratrack.exceptions.ResourceNotFoundException;
import org.dci.theratrack.repository.AppointmentRepository;
import org.dci.theratrack.repository.PatientRepository;
import org.dci.theratrack.repository.TherapistRepository;
import org.dci.theratrack.request.AppointmentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

  @Mock
  private AppointmentRepository appointmentRepository;

  @Mock
  private PatientRepository patientRepository;

  @Mock
  private TherapistRepository therapistRepository;

  @InjectMocks
  private AppointmentService appointmentService;

  private AppointmentRequest appointmentRequest;
  private Appointment appointment;
  private Patient patient;
  private Therapist therapist;

  @BeforeEach
  void setUp() {
    appointmentRequest = new AppointmentRequest();
    appointment = new Appointment();
    appointment.setId(1L);
    appointment.setDateTime(LocalDateTime.of(2023, 12, 1, 10, 0)); // Correct LocalDateTime format
    appointment.setSessionDuration(60);
    appointment.setStatus(AppointmentStatus.PENDING);

    // Initialize the Patient object
    patient = new Patient();
    patient.setId(1L);
    patient.setName("John Doe");

    // Initialize the Therapist object
    therapist = new Therapist();
    therapist.setId(1L);
    therapist.setName("Jane Smith");

    // Assign Therapist and Patient to the appointment
    appointment.setTherapist(therapist);
    appointment.setPatient(patient);

    // Add Appointment, Patient, and Therapist to the AppointmentRequest object
    appointmentRequest.setAppointment(appointment);
    appointmentRequest.setPatient(patient);
    appointmentRequest.setTherapist(therapist);
  }

  @Test
  void testCreateAppointment() {
    // Mock the repository calls
    when(patientRepository.findById(appointmentRequest.getPatient().getId()))
        .thenReturn(Optional.of(patient));

    when(therapistRepository.findById(appointmentRequest.getTherapist().getId()))
        .thenReturn(Optional.of(therapist));

    when(appointmentRepository.save(appointment)).thenReturn(appointment);

    // Call the method under test
    Appointment result = appointmentService.createAppointment(appointmentRequest);

    // Verify the results
    assertNotNull(result);
    assertEquals(appointment.getId(), result.getId());
    assertEquals(appointment.getPatient().getId(), result.getPatient().getId());
    assertEquals(appointment.getTherapist().getId(), result.getTherapist().getId());
    assertEquals(AppointmentStatus.PENDING, result.getStatus()); // Ensure the default status is set

    // Verify interactions with mocks
    verify(patientRepository, times(1)).findById(appointmentRequest.getPatient().getId());
    verify(therapistRepository, times(1)).findById(appointmentRequest.getTherapist().getId());
    verify(appointmentRepository, times(1)).save(appointment);
  }

  @Test
  void testGetAllAppointments() {
    List<Appointment> appointments = List.of(appointment);

    when(appointmentRepository.findAll()).thenReturn(appointments);

    List<Appointment> result = appointmentService.getAllAppointments();

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(appointmentRepository, times(1)).findAll();
  }

  @Test
  void testGetAllAppointmentsEmptyList() {
    when(appointmentRepository.findAll()).thenReturn(Collections.emptyList());

    List<Appointment> result = appointmentService.getAllAppointments();

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(appointmentRepository, times(1)).findAll();
  }

  @Test
  void testGetAppointment() {
    when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

    Appointment result = appointmentService.getAppointment(1L);

    assertNotNull(result, "The appointment should not be null");
    assertEquals(appointment.getId(), result.getId(), "The appointment ID should match");
    assertEquals(appointment.getDateTime(), result.getDateTime(),
        "The appointment dateTime should match");
    assertEquals(appointment.getSessionDuration(), result.getSessionDuration(),
        "The appointment duration should match");
    assertEquals(appointment.getStatus(), result.getStatus(),
        "The appointment status should match");
    assertEquals(appointment.getPatient().getId(), result.getPatient().getId(),
        "The appointment patient should match");
    assertEquals(appointment.getTherapist().getId(), result.getTherapist().getId(),
        "The appointment therapist should match");

    verify(appointmentRepository, times(1)).findById(1L);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenAppointmentIdIsNull() {
    // When & Then
    assertThrows(InvalidRequestException.class, () -> {
      appointmentService.getAppointment(null);
    });

    // Verify no interactions with the repository
    verifyNoInteractions(appointmentRepository);
  }

  @Test
  void testGetAppointmentsByPatient() {
    List<Appointment> appointments = List.of(appointment);

    when(appointmentRepository.getAppointmentsByPatientId(1L)).thenReturn(appointments);

    List<Appointment> result = appointmentService.getAppointmentsByPatientId(1L);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(appointmentRepository, times(1)).getAppointmentsByPatientId(1L);
  }

  @Test
  void shouldThrowInvalidRequestExceptionWhenAppointmentIdIsNull() {
    // When & Then
    InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
      appointmentService.getAppointment(null);
    });

    // Assert
    assertEquals("Appointment id cannot be null.", exception.getMessage());

    // Verify no interactions with the repository
    verifyNoInteractions(appointmentRepository);
  }

  @Test
  void shouldThrowResourceNotFoundExceptionWhenAppointmentIdDoesNotExist() {
    // Given
    Long nonExistentId = 999L;
    when(appointmentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      appointmentService.getAppointment(nonExistentId);
    });

    // Assert
    assertEquals("Appointment not found with ID: " + nonExistentId, exception.getMessage());
    verify(appointmentRepository, times(1)).findById(nonExistentId);
  }

  @Test
  void testGetAppointmentsByPatientNoAppointments(){
    when(appointmentRepository.getAppointmentsByPatientId(1L)).thenReturn(Collections.emptyList());

    List<Appointment> result = appointmentService.getAppointmentsByPatientId(1L);

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(appointmentRepository, times(1)).getAppointmentsByPatientId(1L);
  }

  @Test
  void testUpdateAppointment() {
    // Mock the repository calls to find the existing appointment
    when(appointmentRepository.findById(appointment.getId()))
        .thenReturn(Optional.of(appointment));

    // Mock patient and therapist repository calls if their IDs are present in updatedAppointment
    when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
    when(therapistRepository.findById(therapist.getId())).thenReturn(Optional.of(therapist));

    // Mock the save operation
    when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

    // Create a new updatedAppointment object
    Appointment updatedAppointment = new Appointment();
    updatedAppointment.setDateTime(LocalDateTime.of(2023, 12, 2, 14, 0)); // Change date/time
    updatedAppointment.setSessionDuration(90); // Change duration
    updatedAppointment.setStatus(AppointmentStatus.CONFIRMED); // Change status

    Patient updatedPatient = new Patient();
    updatedPatient.setId(patient.getId()); // Reuse the existing patient ID
    updatedAppointment.setPatient(updatedPatient);

    Therapist updatedTherapist = new Therapist();
    updatedTherapist.setId(therapist.getId()); // Reuse the existing therapist ID
    updatedAppointment.setTherapist(updatedTherapist);

    // Call the method under test
    Appointment result = appointmentService.updateAppointment(appointment.getId(), updatedAppointment);

    // Verify the results
    assertNotNull(result);
    assertEquals(updatedAppointment.getDateTime(), result.getDateTime());
    assertEquals(updatedAppointment.getSessionDuration(), result.getSessionDuration());
    assertEquals(updatedAppointment.getStatus(), result.getStatus());
    assertEquals(patient.getId(), result.getPatient().getId());
    assertEquals(therapist.getId(), result.getTherapist().getId());

    // Verify interactions with mocks
    verify(appointmentRepository, times(1)).findById(appointment.getId());
    verify(patientRepository, times(1)).findById(patient.getId());
    verify(therapistRepository, times(1)).findById(therapist.getId());
    verify(appointmentRepository, times(1)).save(any(Appointment.class));
  }

  @Test
  void testUpdateAppointment_NullUpdatedAppointment() {
    // Call the method with a null updatedAppointment and expect an exception
    InvalidRequestException exception = assertThrows(
        InvalidRequestException.class,
        () -> appointmentService.updateAppointment(appointment.getId(), null)
    );

    assertEquals("Updated appointment cannot be null.", exception.getMessage());
    verifyNoInteractions(appointmentRepository, patientRepository, therapistRepository);
  }

  @Test
  void testUpdateAppointment_AppointmentNotFound() {
    // Mock repository call to return empty for the appointment ID
    when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.empty());

    // Call the method and expect a ResourceNotFoundException
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> appointmentService.updateAppointment(appointment.getId(), appointment)
    );

    assertEquals("Appointment not found with ID: " + appointment.getId(), exception.getMessage());
    verify(appointmentRepository, times(1)).findById(appointment.getId());
    verifyNoInteractions(patientRepository, therapistRepository);
  }

  @Test
  void testUpdateAppointment_PatientNotFound() {
    // Mock repository calls for the existing appointment
    when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));

    // Mock patient repository to return empty for the patient ID
    when(patientRepository.findById(patient.getId())).thenReturn(Optional.empty());

    // Create updatedAppointment with a patient ID that doesn't exist
    Appointment updatedAppointment = new Appointment();
    updatedAppointment.setPatient(new Patient());
    updatedAppointment.getPatient().setId(patient.getId()); // Non-existent ID

    // Call the method and expect a ResourceNotFoundException
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> appointmentService.updateAppointment(appointment.getId(), updatedAppointment)
    );

    assertEquals("Patient not found with ID: " + patient.getId(), exception.getMessage());
    verify(appointmentRepository, times(1)).findById(appointment.getId());
    verify(patientRepository, times(1)).findById(patient.getId());
    verifyNoInteractions(therapistRepository);
  }

  @Test
  void testUpdateAppointment_TherapistNotFound() {
    // Mock repository calls for the existing appointment
    when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));

    // Mock therapist repository to return empty for the therapist ID
    when(therapistRepository.findById(therapist.getId())).thenReturn(Optional.empty());

    // Create updatedAppointment with a therapist ID that doesn't exist
    Appointment updatedAppointment = new Appointment();
    updatedAppointment.setTherapist(new Therapist());
    updatedAppointment.getTherapist().setId(therapist.getId()); // Non-existent ID

    // Call the method and expect a ResourceNotFoundException
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> appointmentService.updateAppointment(appointment.getId(), updatedAppointment)
    );

    assertEquals("Therapist not found with ID: " + therapist.getId(), exception.getMessage());
    verify(appointmentRepository, times(1)).findById(appointment.getId());
    verify(therapistRepository, times(1)).findById(therapist.getId());
    verifyNoInteractions(patientRepository);
  }

  @Test
  void testDeleteAppointment() {

    when(appointmentRepository.existsById(1L)).thenReturn(true);
    doNothing().when(appointmentRepository).deleteById(1L);

    appointmentService.deleteAppointment(1L);

    verify(appointmentRepository, times(1)).deleteById(1L);
  }

  @Test
  void testDeleteAppointmentNotFound() {
    // Arrange: Simulate the appointment ID does not exist
    Long appointmentId = 1L;
    when(appointmentRepository.existsById(appointmentId)).thenReturn(false);

    // Act & Assert: Expect a ResourceNotFoundException
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> appointmentService.deleteAppointment(appointmentId)
    );

    // Verify the exception message
    assertEquals("Appointment not found with ID: " + appointmentId, exception.getMessage());

    // Verify repository interaction
    verify(appointmentRepository, times(1)).existsById(appointmentId);
    verify(appointmentRepository, never()).deleteById(any());
  }

  @Test
  void testDeleteAppointmentInvalidRequestNullId() {
    // Act & Assert: Expect an exception for null appointment ID
    InvalidRequestException exception = assertThrows(
        InvalidRequestException.class,
        () -> appointmentService.deleteAppointment(null)
    );

    // Verify the exception message
    assertEquals("Appointment ID cannot be null.", exception.getMessage());

    // Verify no repository interaction
    verifyNoInteractions(appointmentRepository);
  }
}