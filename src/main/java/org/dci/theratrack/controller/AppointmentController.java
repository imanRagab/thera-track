package org.dci.theratrack.controller;

import java.util.List;
import java.util.Map;

import org.dci.theratrack.dto.AppointmentDTO;
import org.dci.theratrack.entity.Appointment;
import org.dci.theratrack.entity.Treatment;
import org.dci.theratrack.exceptions.ResourceNotFoundException;
import org.dci.theratrack.request.AppointmentRequest;
import org.dci.theratrack.service.AppointmentService;
import org.dci.theratrack.service.PatientService;
import org.dci.theratrack.service.TreatmentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

  @Autowired
  private AppointmentService appointmentService;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  PatientService patientService;

  @Autowired
  private TreatmentService treatmentService;

  @PostMapping
  public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentRequest request) {
    System.out.println("Received request: " + request);
    return ResponseEntity.ok(appointmentService.createAppointment(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Long id) {
    AppointmentDTO appointmentDTO = appointmentService.getAppointmentById(id);
    return ResponseEntity.ok(appointmentDTO);
  }

  @GetMapping("/patient/{patientId}")
  public ResponseEntity<List<Appointment>> getAppointmentsByPatientId(
      @PathVariable Long patientId) {
    List<Appointment> appointments = appointmentService.getAppointmentsByPatientId(patientId);
    if (appointments.isEmpty()) {
      throw new ResourceNotFoundException(
          "No appointments found for patient with ID: " + patientId);
    }
    return ResponseEntity.ok(appointments);
  }

@GetMapping
public Page<Appointment> getAppointments(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
  Pageable pageable = PageRequest.of(page, size);
  return appointmentService.getPaginatedAppointments(pageable);
}

  @PutMapping("/{appointmentId}")
  public ResponseEntity<Appointment> updateAppointment(@PathVariable Long appointmentId,
      @RequestBody Appointment updatedAppointment) {
    return ResponseEntity.ok(
        appointmentService.updateAppointment(appointmentId, updatedAppointment));

  }

  @DeleteMapping("/{appointmentId}")
  public ResponseEntity<Void> deleteAppointment(@PathVariable Long appointmentId) {
    appointmentService.deleteAppointment(appointmentId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{appointmentId}/treatments")
  public ResponseEntity<Treatment> addTreatmentDetail(
      @PathVariable Long appointmentId,
      @RequestBody Treatment treatmentRequest) {

    Treatment savedTreatment = appointmentService.addTreatmentToAppointment(appointmentId,
        treatmentRequest);

    return ResponseEntity.ok(savedTreatment);
  }


  @PutMapping("/{appointment_id}/notes")
  public ResponseEntity<Appointment> updateAppointmentNotes(
      @PathVariable("appointment_id") Long appointmentId,
      @RequestBody Map<String, String> requestBody) {

    String updatedNotes = requestBody.get("notes");
    if (updatedNotes == null || updatedNotes.trim().isEmpty()) {
      return ResponseEntity.badRequest().body(null);
    }

    Appointment updatedAppointment = appointmentService.updateAppointmentNotes(appointmentId,
        updatedNotes);
    return ResponseEntity.ok(updatedAppointment);
  }

}