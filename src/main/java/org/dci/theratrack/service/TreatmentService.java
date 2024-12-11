package org.dci.theratrack.service;

import java.util.List;
import org.dci.theratrack.entity.Treatment;
import org.dci.theratrack.exceptions.InvalidRequestException;
import org.dci.theratrack.exceptions.ResourceNotFoundException;
import org.dci.theratrack.repository.TreatmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TreatmentService {

  @Autowired
  private TreatmentRepository treatmentRepository;

  /**
   * Adds a new treatment.
   *
   * @param treatment the treatment to add
   * @return the added treatment
   * @throws InvalidRequestException if the treatment is null
   */
  public Treatment addTreatment(Treatment treatment) {
    if (treatment == null) {
      throw new InvalidRequestException("Treatment cannot be null.");
    }
    return treatmentRepository.save(treatment);
  }

  public List<Treatment> getAllTreatments() {
    return treatmentRepository.findAll();
  }

  /**
   * Retrieves a treatment by its ID.
   *
   * @param id the ID of the treatment
   * @return the treatment
   * @throws ResourceNotFoundException if the treatment is not found
   */
  public Treatment getTreatment(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("Treatment ID cannot be null.");
    }
    return treatmentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with ID: " + id));
  }

  /**
   * Updates an existing treatment.
   *
   * @param treatment the updated treatment data
   * @return the updated treatment
   * @throws InvalidRequestException if the treatment is null
   * @throws ResourceNotFoundException if the treatment does not exist
   */
  public Treatment updateTreatment(Treatment treatment) {
    if (treatment == null) {
      throw new InvalidRequestException("Treatment cannot be null.");
    }

    if (!treatmentRepository.existsById(treatment.getId())) {
      throw new ResourceNotFoundException("Treatment not found with ID: " + treatment.getId());
    }

    return treatmentRepository.save(treatment);
  }

  /**
   * Deletes a treatment by its ID.
   *
   * @param id the ID of the treatment to delete
   * @throws ResourceNotFoundException if the treatment does not exist
   */
  public void deleteTreatment(Long id) {
    if (!treatmentRepository.existsById(id)) {
      throw new ResourceNotFoundException("Treatment not found with ID: " + id);
    }
    treatmentRepository.deleteById(id);
  }

}
