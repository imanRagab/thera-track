package org.dci.theratrack.controller;

import jakarta.validation.Valid;
import org.dci.theratrack.entity.Patient;
import org.dci.theratrack.request.PatientRequest;
import org.dci.theratrack.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

  @Autowired
  private PatientService patientService;

  @PostMapping
  public ResponseEntity<Patient> createPatient(@RequestBody @Valid PatientRequest request) {
    return ResponseEntity.ok(patientService.createPatient(request));
  }

  @GetMapping
  public List<Patient> getAllPatients() {
    return patientService.getAllPatients();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
    Patient therapist = patientService.getPatientById(id);
    return ResponseEntity.ok(therapist);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody @Valid Patient updatedPatient) {
    return ResponseEntity.ok(patientService.updatePatient(id, updatedPatient));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
    patientService.deletePatient(id);
    return ResponseEntity.noContent().build();
  }
}