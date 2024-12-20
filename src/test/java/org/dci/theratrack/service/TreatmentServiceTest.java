package org.dci.theratrack.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.dci.theratrack.entity.Treatment;
import org.dci.theratrack.exceptions.InvalidRequestException;
import org.dci.theratrack.exceptions.ResourceNotFoundException;
import org.dci.theratrack.repository.TreatmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class TreatmentServiceTest {

  @Mock
  private TreatmentRepository treatmentRepository;

  @InjectMocks
  private TreatmentService treatmentService;

  private Treatment treatment;

  @BeforeEach
  void setUp() {
    treatment = new Treatment();
    treatment.setId(1L);
    treatment.setName("Insulin");
    treatment.setDescription("Diabetes treatment");
  }

//  @Test
//  void testAddTreatment() {
//    when(treatmentRepository.save(any(Treatment.class))).thenReturn(treatment);
//
//    Treatment savedTreatment = treatmentService.addTreatment(treatment);
//
//    assertNotNull(savedTreatment);
//    assertEquals(treatment.getId(), savedTreatment.getId());
//    assertEquals(treatment.getName(), savedTreatment.getName());
//    verify(treatmentRepository, times(1)).save(treatment);
//  }

  @Test
  void testGetAllTreatments() {
    List<Treatment> treatments = List.of(treatment);

    when(treatmentRepository.findAll()).thenReturn(treatments);

    List<Treatment> result = treatmentService.getAllTreatments();

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(treatmentRepository, times(1)).findAll();
  }

  @Test
  void testGetAllTreatmentsEmptyList() {
    when(treatmentRepository.findAll()).thenReturn(Collections.emptyList());

    List<Treatment> result = treatmentService.getAllTreatments();

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(treatmentRepository, times(1)).findAll();
  }

  @Test
  void testGetTreatment() {
    when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));

    Treatment result = treatmentService.getTreatment(1L);

    assertNotNull(result);
    assertEquals(treatment.getId(), result.getId());
    verify(treatmentRepository, times(1)).findById(1L);
  }

  @Test
  void testUpdateTreatment() {
    treatment.setDescription("Updated description");

    when(treatmentRepository.existsById(treatment.getId())).thenReturn(true);
    when(treatmentRepository.save(treatment)).thenReturn(treatment);

    Treatment result = treatmentService.updateTreatment(treatment);

    assertNotNull(result);
    assertEquals("Updated description", result.getDescription());
    verify(treatmentRepository, times(1)).save(treatment);
  }

  @Test
  void testDeleteTreatment() {
    when(treatmentRepository.existsById(1L)).thenReturn(true);
    doNothing().when(treatmentRepository).deleteById(1L);

    treatmentService.deleteTreatment(1L);

    verify(treatmentRepository, times(1)).deleteById(1L);
  }

  @Test
  void shouldThrowResourceNotFoundExceptionWhenTreatmentIdDoesNotExist() {
    // Given
    Long nonExistentId = 999L;
    when(treatmentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      treatmentService.getTreatment(nonExistentId);
    });

    // Assert
    assertEquals("Treatment not found with ID: " + nonExistentId, exception.getMessage());
    verify(treatmentRepository, times(1)).findById(nonExistentId);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenTreatmentIdIsNull() {
    // When & Then
    assertThrows(IllegalArgumentException.class, () -> {
      treatmentService.getTreatment(null);
    });

    // Verify no interactions with the repository
    verifyNoInteractions(treatmentRepository);
  }

  @Test
  void shouldThrowInvalidRequestExceptionWhenTreatmentIsNull() {
    // When & Then
    InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
      treatmentService.updateTreatment(null);
    });

    // Assert
    assertEquals("Treatment cannot be null.", exception.getMessage());

    // Verify no interactions with the repository
    verifyNoInteractions(treatmentRepository);
  }

  @Test
  void deleteTreatmentShouldThrowResourceNotFoundExceptionWhenTreatmentIdDoesNotExist() {
    // Given
    Long nonExistentId = 999L;
    when(treatmentRepository.existsById(nonExistentId)).thenReturn(false);

    // When & Then
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      treatmentService.deleteTreatment(nonExistentId);
    });

    // Assert
    assertEquals("Treatment not found with ID: " + nonExistentId, exception.getMessage());
    verify(treatmentRepository, times(1)).existsById(nonExistentId);
    verify(treatmentRepository, never()).deleteById(nonExistentId);
  }
}

