package org.dci.theratrack.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.dci.theratrack.entity.Diagnosis;
import org.dci.theratrack.exceptions.InvalidRequestException;
import org.dci.theratrack.exceptions.ResourceNotFoundException;
import org.dci.theratrack.repository.DiagnosisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DiagnosisServiceTest {

  @Mock
  private DiagnosisRepository diagnosisRepository;

  @InjectMocks
  private DiagnosisService diagnosisService;

  private Diagnosis diagnosis;

  @BeforeEach
  void setUp() {
    diagnosis = new Diagnosis();
    diagnosis.setId(1L);
    diagnosis.setName("Diabetes");
    diagnosis.setDescription(
        "A chronic condition that affects how the body processes blood sugar.");
  }

  @Test
  void testGetDiagnosis() {
    when(diagnosisRepository.findById(1L)).thenReturn(Optional.ofNullable(diagnosis));

    Diagnosis result = diagnosisService.getDiagnosis(1L);

    assertNotNull(result);
    assertEquals(diagnosis.getId(), result.getId());
    assertEquals(diagnosis.getName(), result.getName());
    verify(diagnosisRepository, times(1)).findById(1L);
  }

  @Test
  void testGetDiagnosisNotFound() {
    Long diagnosisId = 1L;

    // Mock behavior
    when(diagnosisRepository.findById(diagnosisId))
        .thenThrow(new ResourceNotFoundException("Diagnosis not found with ID: " + diagnosisId));

    // Assert exception
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> diagnosisService.getDiagnosis(diagnosisId)
    );

    // Verify exception message
    assertEquals("Diagnosis not found with ID: " + diagnosisId, exception.getMessage());
    // Verify repository interaction
    verify(diagnosisRepository, times(1)).findById(diagnosisId);
  }

  @Test
  void testGetAllDiagnoses() {
    List<Diagnosis> diagnoses = List.of(diagnosis);

    when(diagnosisRepository.findAll()).thenReturn(diagnoses);

    List<Diagnosis> result = diagnosisService.getDiagnoses();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(diagnosis.getId(), result.get(0).getId());
    verify(diagnosisRepository, times(1)).findAll();
  }

  @Test
  void testGetDiagnosesEmptyList() {
    when(diagnosisRepository.findAll()).thenReturn(Collections.emptyList());

    List<Diagnosis> result = diagnosisService.getDiagnoses();

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(diagnosisRepository, times(1)).findAll();
  }

  @Test
  void testAddDiagnosis() {
    when(diagnosisRepository.save(diagnosis)).thenReturn(diagnosis);

    Diagnosis result = diagnosisService.addDiagnosis(diagnosis);

    assertNotNull(result);
    assertEquals(diagnosis.getId(), result.getId());
    assertEquals(diagnosis.getName(), result.getName());
    verify(diagnosisRepository, times(1)).save(diagnosis);
  }

  @Test
  void testAddDiagnosisInvalidRequest() {
    // Act & Assert: Call the service method with null and expect an exception
    InvalidRequestException exception = assertThrows(
        InvalidRequestException.class,
        () -> diagnosisService.addDiagnosis(null)
    );

    // Verify the exception message
    assertEquals("Diagnosis cannot be null.", exception.getMessage());

    // Verify no repository interaction occurs
    verifyNoInteractions(diagnosisRepository);
  }

  @Test
  void testUpdateDiagnosis() {
    diagnosis.setName("Updated Diagnosis");

    when(diagnosisRepository.save(diagnosis)).thenReturn(diagnosis);

    Diagnosis result = diagnosisService.updateDiagnosis(diagnosis);

    assertNotNull(result);
    assertEquals("Updated Diagnosis", result.getName());
    verify(diagnosisRepository, times(1)).save(diagnosis);
  }

  @Test
  void testUpdateDiagnosisNotFound() {
    // Arrange: Simulate exception for non-existent diagnosis
    when(diagnosisRepository.save(diagnosis))
        .thenThrow(new ResourceNotFoundException("Diagnosis not found with ID: " + diagnosis.getId()));

    // Act & Assert: Verify exception is thrown
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> diagnosisService.updateDiagnosis(diagnosis)
    );

    // Verify the exception message
    assertEquals("Diagnosis not found with ID: " + diagnosis.getId(), exception.getMessage());

    // Verify repository interaction
    verify(diagnosisRepository, times(1)).save(diagnosis);
  }

  @Test
  void testDeleteDiagnosis() {
    doNothing().when(diagnosisRepository).deleteById(1L);

    diagnosisService.deleteDiagnosis(1L);

    verify(diagnosisRepository, times(1)).deleteById(1L);
  }

  @Test
  void testDeleteDiagnosisNotFound() {
    Long diagnosisId = 1L;

    // Mock exception when deleteById is called for a non-existent ID
    doThrow(new ResourceNotFoundException("Diagnosis not found with ID: " + diagnosisId))
        .when(diagnosisRepository).deleteById(diagnosisId);

    // Assert that the service throws the exception
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> diagnosisService.deleteDiagnosis(diagnosisId)
    );

    // Verify the exception message
    assertEquals("Diagnosis not found with ID: " + diagnosisId, exception.getMessage());

    // Verify repository interaction
    verify(diagnosisRepository, times(1)).deleteById(diagnosisId);
  }
}
