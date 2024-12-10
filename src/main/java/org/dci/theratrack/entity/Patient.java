package org.dci.theratrack.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patients",
    uniqueConstraints = @UniqueConstraint(columnNames = {"email", "phone"}))
//ensures that the combination of email and phone is unique
@ToString(exclude = {"therapists","diagnoses", "appointment", "user"})
public class Patient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(max = 255)
  @Column(nullable = false)
  private String name;

  @NotNull
  @Size(max = 255)
  @Column(nullable = false)
  private String surname;

  @Email
  @Column(nullable = false, unique = true)
  private String email;

  @Pattern(regexp = "^\\+?[0-9]{10,15}$")
  @Column(nullable = false, length = 15)
  private String phone;

  @NotNull
  @Column(nullable = false, length = 50)
  private String gender = "Not specified"; // Default value if not set

  @Past // Ensures the birthDate is a past date
  @Column(nullable = false)
  @JsonProperty("birthdate")
  private LocalDate birthDate;

  @Size(max = 500)
  @Column(length = 500)
  private String address;


  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "patients_therapists",
      joinColumns = @JoinColumn(name = "patient_id"),
      inverseJoinColumns = @JoinColumn(name = "therapist_id")
  )
  private List<Therapist> therapists;


  @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Diagnosis> diagnoses;


  @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnore
  private List<Appointment> appointment;

  @OneToOne
  @JoinColumn(name = "user_id", unique = true)
  @JsonBackReference
  private User user;


  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
    if (user != null) {
      user.setPatient(this); // Sync the User entity
    }
  }

}