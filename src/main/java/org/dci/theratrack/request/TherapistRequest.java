package org.dci.theratrack.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Data;
import org.dci.theratrack.entity.Therapist;
import org.dci.theratrack.entity.User;
@Data
public class TherapistRequest {
  @NotBlank
  private String name;
  @NotBlank
  private String surname;
  @NotBlank(message = "Email must not be blank")
  @Email(message = "Email should be valid")
  private String email;
  @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must match the format: +1234567890")
  private String phone;
  private String gender;
  private String address;
  @NotNull
  private LocalDate birthdate;
}

