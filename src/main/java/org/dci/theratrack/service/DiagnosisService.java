package org.dci.theratrack.service;

import java.util.List;
import org.dci.theratrack.entity.Diagnosis;
import org.dci.theratrack.exceptions.InvalidRequestException;
import org.dci.theratrack.exceptions.ResourceNotFoundException;
import org.dci.theratrack.repository.DiagnosisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class DiagnosisService {

  @Autowired
  private DiagnosisRepository diagnosisRepository;

  /**
   * Retrieves a diagnosis by its ID.
   *
   * @param diagnosisId the ID of the diagnosis
   * @return the diagnosis
   * @throws ResourceNotFoundException if the diagnosis is not found
   */
  public Diagnosis getDiagnosis(Long diagnosisId) {
    return diagnosisRepository.findById(diagnosisId)
        .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found with ID: " + diagnosisId));
  }

  public List<Diagnosis> getDiagnoses() {
    return diagnosisRepository.findAll();
  }

  /**
   * Adds a new diagnosis.
   *
   * @param diagnosis the diagnosis to add
   * @return the added diagnosis
   * @throws InvalidRequestException if the diagnosis is null
   */
  public Diagnosis addDiagnosis(Diagnosis diagnosis) {
    if (diagnosis == null) {
      throw new InvalidRequestException("Diagnosis cannot be null.");
    }
    return diagnosisRepository.save(diagnosis);
  }

  /**
   * Updates an existing diagnosis.
   *
   * @param diagnosis the diagnosis to update
   * @return the updated diagnosis
   * @throws ResourceNotFoundException if the diagnosis does not exist
   * @throws InvalidRequestException if the diagnosis is null or its ID is null
   */
  public Diagnosis updateDiagnosis(Diagnosis diagnosis) {
    if (diagnosis == null || diagnosis.getId() == null) {
      throw new InvalidRequestException("Diagnosis or diagnosis ID cannot be null.");
    }

    // Check if the diagnosis exists before updating
    try {
      return diagnosisRepository.save(diagnosis);
    } catch (Exception e) {
      throw new ResourceNotFoundException("Diagnosis not found with ID: " + diagnosis.getId());
    }
  }

  /**
   * Deletes a diagnosis by its ID.
   *
   * @param diagnosisId the ID of the diagnosis to delete
   * @throws ResourceNotFoundException if the diagnosis does not exist
   */
  public void deleteDiagnosis(Long diagnosisId) {
    try {
      diagnosisRepository.deleteById(diagnosisId);
    } catch (Exception e) {
      throw new ResourceNotFoundException("Diagnosis not found with ID: " + diagnosisId);
    }
  }
}
