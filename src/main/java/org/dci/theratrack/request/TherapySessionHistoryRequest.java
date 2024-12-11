package org.dci.theratrack.request;

import jakarta.validation.Valid;
import java.time.LocalDateTime;

public class TherapySessionHistoryRequest {
  @Valid
  private Long id;

  @Valid
  private LocalDateTime dateTime;

  @Valid
  private Integer sessionDuration;

  @Valid
  private String therapistName;

  @Valid
  private String notes;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  public Integer getSessionDuration() {
    return sessionDuration;
  }

  public void setSessionDuration(Integer sessionDuration) {
    this.sessionDuration = sessionDuration;
  }

  public String getTherapistName() {
    return therapistName;
  }

  public void setTherapistName(String therapistName) {
    this.therapistName = therapistName;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
