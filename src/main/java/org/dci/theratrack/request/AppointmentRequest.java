package org.dci.theratrack.request;


import jakarta.validation.Valid;
import org.dci.theratrack.entity.Appointment;
import org.dci.theratrack.entity.Patient;
import org.dci.theratrack.entity.Therapist;

public class AppointmentRequest {
  @Valid
  private Appointment appointment;

  @Valid
  private Patient patient;

  @Valid
  private Therapist therapist;

  public Appointment getAppointment() {
    return appointment;
  }

  public void setAppointment(Appointment appointment) {
    this.appointment = appointment;
  }

  public Patient getPatient() {
    return patient;
  }

  public void setPatient(Patient patient) {
    this.patient = patient;
  }

  public Therapist getTherapist() {
    return therapist;
  }

  public void setTherapist(Therapist therapist) {
    this.therapist = therapist;
  }
}
