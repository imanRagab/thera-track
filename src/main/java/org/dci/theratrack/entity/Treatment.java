package org.dci.theratrack.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dci.theratrack.enums.DifficultyLevel;
import org.dci.theratrack.enums.TreatmentStatus;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "treatments")
public class Treatment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Treatment name must not be blank")
  @Column(nullable = false)
  private String name;

  @Size(max = 1000)
  private String description;

  @Min(1)  // Enforcing positive duration
  @Column(nullable = false)
  private Integer duration;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DifficultyLevel difficultyLevel;


  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TreatmentStatus treatmentStatus;

  @OneToOne
  @JoinColumn(name = "diagnosis_id", unique = true)
  private Diagnosis diagnosis;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "appointment_id", nullable = false) // Foreign key to appointments table
  @JsonBackReference // Prevents recursion from the other side
  private Appointment appointment;

  @Column(length = 2000)
  private String notes;

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
