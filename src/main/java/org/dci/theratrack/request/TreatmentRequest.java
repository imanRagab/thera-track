package org.dci.theratrack.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.dci.theratrack.enums.DifficultyLevel;
import org.dci.theratrack.enums.TreatmentStatus;

public class TreatmentRequest {

  private Long id;

  @NotBlank
  private String name;


  private String description;

  @Min(1)
  private Integer duration;

  private DifficultyLevel difficultyLevel;

  private TreatmentStatus treatmentStatus;

  private String notes;

  private Long diagnosisId; // Assuming the diagnosis is optional and can be set separately

  public Long getDiagnosisId() {
    return diagnosisId;
  }

  public void setDiagnosisId(Long diagnosisId) {
    this.diagnosisId = diagnosisId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public DifficultyLevel getDifficultyLevel() {
    return difficultyLevel;
  }

  public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
    this.difficultyLevel = difficultyLevel;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public TreatmentStatus getTreatmentStatus() {
    return treatmentStatus;
  }

  public void setTreatmentStatus(TreatmentStatus treatmentStatus) {
    this.treatmentStatus = treatmentStatus;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
