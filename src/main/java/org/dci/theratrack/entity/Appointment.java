package org.dci.theratrack.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dci.theratrack.enums.AppointmentStatus;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="appointments")
public class Appointment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull(message = "Date and time cannot be null")
  @Column(nullable = false, name = "date_time")
  private LocalDateTime dateTime;

  @NotNull(message = "Duration cannot be null")
  @Min(1)  // Session duration must be positive
  @Column(nullable = false)
  private Integer sessionDuration;

  @Enumerated(EnumType.ORDINAL) // Store as a string in the database
  @Column(nullable = false)
  private AppointmentStatus status;

  @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonManagedReference // Avoid infinite recursion during serialization
  private List<Treatment> treatments;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "therapist_id", nullable = false)
  @JsonIgnore
  private Therapist therapist;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "patient_id", nullable = false)
  @JsonIgnore
  private Patient patient;

  @Column(length = 2000)
  private String additionalNotes;

  public void setNotes(String notes) {
    this.additionalNotes = notes;
  }





}
