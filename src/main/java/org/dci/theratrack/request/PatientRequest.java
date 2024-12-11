package org.dci.theratrack.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import org.dci.theratrack.entity.Patient;
import org.dci.theratrack.entity.User;

@Data
public class PatientRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    private String email;
    private String phone;
    private String gender;
    private String address;
    @NotNull
    private LocalDate birthdate;
}
