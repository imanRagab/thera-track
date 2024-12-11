package org.dci.theratrack.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dci.theratrack.enums.UserRole;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  @NotBlank
  @Size(min = 3, max = 50)
  private String username;



  @Column(nullable = false)
  @Size(min = 8, message = "Password must be at least 8 characters long.")
  @NotBlank
  private String password;


  @Enumerated(EnumType.ORDINAL)
  @Column(nullable = false)
  private UserRole userRole;


  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonManagedReference
  private Therapist therapist;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonManagedReference
  private Patient patient;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public @Size(min = 8, message = "Password must be at least 8 characters long.") String getPassword() {
    return password;
  }

  public void setPassword(@Size(min = 8, message = "Password must be at least 8 characters long.") String password) {
    this.password = password;
  }

  public UserRole getUserRole() {
    return userRole;
  }

  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Therapist getTherapist() {
    return therapist;
  }

  public void setTherapist(Therapist therapist) {
    this.therapist = therapist;
  }

  public Patient getPatient() {
    return patient;
  }

  public void setPatient(Patient patient) {
    this.patient = patient;
  }

}

