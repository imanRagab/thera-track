package org.dci.theratrack.dto;

import java.time.LocalDateTime;

public class AppointmentDTO {
    private Long id;
    private LocalDateTime dateTime;
    private Integer sessionDuration;
    private String status;
    private String additionalNotes;
    private TherapistDTO therapist;
    private PatientDTO patient;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public TherapistDTO getTherapist() {
        return therapist;
    }

    public void setTherapist(TherapistDTO therapist) {
        this.therapist = therapist;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }
}