package org.dci.theratrack.service;

import java.util.List;

import org.dci.theratrack.dto.AppointmentDTO;
import org.dci.theratrack.dto.PatientDTO;
import org.dci.theratrack.dto.TherapistDTO;
import org.dci.theratrack.entity.Appointment;
import org.dci.theratrack.entity.Patient;
import org.dci.theratrack.entity.Therapist;
import org.dci.theratrack.entity.Treatment;
import org.dci.theratrack.enums.AppointmentStatus;
import org.dci.theratrack.exceptions.InvalidRequestException;
import org.dci.theratrack.exceptions.ResourceNotFoundException;
import org.dci.theratrack.repository.AppointmentRepository;
import org.dci.theratrack.repository.PatientRepository;
import org.dci.theratrack.repository.TherapistRepository;
import org.dci.theratrack.repository.TreatmentRepository;
import org.dci.theratrack.request.AppointmentRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AppointmentService {

  @Autowired
  private AppointmentRepository appointmentRepository;

  @Autowired
  TherapistRepository therapistRepository;

  @Autowired
  PatientRepository patientRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  TreatmentRepository treatmentRepository;


  /**
   * Creates a new appointment.
   *
   * @param request the appointment to create
   * @return the created appointment
   */
  public Appointment createAppointment(AppointmentRequest request) {
    Appointment appointment = request.getAppointment();
    if (appointment == null) {
      throw new InvalidRequestException("Appointment details cannot be null.");
    }

    Patient patient = patientRepository.findById(request.getPatient().getId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Patient not found with ID: " + request.getPatient().getId()));
    appointment.setPatient(patient);

    Therapist therapist = therapistRepository.findById(request.getTherapist().getId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Therapist not found with ID: " + request.getTherapist().getId()));
    appointment.setTherapist(therapist);

    if (appointment.getStatus() == null) {
      appointment.setStatus(AppointmentStatus.PENDING);
    }
    return appointmentRepository.save(appointment);
  }


  /**
   * Retrieves an appointment by its ID.
   *
   * @param appointmentId the ID of the appointment
   * @return the appointment
   * @throws ResourceNotFoundException if the appointment is not found
   */
  public Appointment getAppointment(Long appointmentId) {
    if (appointmentId == null) {
      throw new InvalidRequestException("Appointment id cannot be null.");
    }
    return appointmentRepository.findById(appointmentId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));
  }
  // Method to fetch appointment by ID and map to DTO
  public AppointmentDTO getAppointmentById(Long id) {
    Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

    return mapToDTO(appointment);
  }


  /**
   * Retrieves appointments for a specific patient.
   *
   * @param patientId the patient whose appointments are to be retrieved
   * @return a list of appointments
   * @throws InvalidRequestException if the patient is null or has no ID
   */
  public List<Appointment> getAppointmentsByPatientId(Long patientId) {
    return appointmentRepository.getAppointmentsByPatientId(patientId);
  }

  public List<Appointment> getAllAppointments() {
    return appointmentRepository.findAll();
  }

  public Page<Appointment> getPaginatedAppointments(Pageable pageable) {
    return appointmentRepository.findAll(pageable);
  }

  /**
   * Updates an existing appointment.
   *
   * @param updatedAppointment the appointment to update
   * @return the updated appointment
   * @throws ResourceNotFoundException if the appointment is not found
   */
  public Appointment updateAppointment(Long appointmentId, Appointment updatedAppointment) {
    if (updatedAppointment == null) {
      throw new InvalidRequestException("Updated appointment cannot be null.");
    }

    // Retrieve the existing appointment
    Appointment existingAppointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

    modelMapper.map(updatedAppointment, existingAppointment);

    // Handle associations explicitly if needed
    if (updatedAppointment.getPatient() != null
        && updatedAppointment.getPatient().getId() != null) {
      Patient patient = patientRepository.findById(updatedAppointment.getPatient().getId())
          .orElseThrow(() -> new ResourceNotFoundException(
              "Patient not found with ID: " + updatedAppointment.getPatient().getId()));
      existingAppointment.setPatient(patient);
    }

    if (updatedAppointment.getTherapist() != null
        && updatedAppointment.getTherapist().getId() != null) {
      Therapist therapist = therapistRepository.findById(updatedAppointment.getTherapist().getId())
          .orElseThrow(() -> new ResourceNotFoundException(
              "Therapist not found with ID: " + updatedAppointment.getTherapist().getId()));
      existingAppointment.setTherapist(therapist);
    }

    // Save and return the updated appointment
    return appointmentRepository.save(existingAppointment);
  }


  /**
   * Deletes an appointment by its ID.
   *
   * @param appointmentId the ID of the appointment to delete
   * @throws ResourceNotFoundException if the appointment is not found
   */
  public void deleteAppointment(Long appointmentId) {
    if (appointmentId == null) {
      throw new InvalidRequestException("Appointment ID cannot be null.");
    }

    if (!appointmentRepository.existsById(appointmentId)) {
      throw new ResourceNotFoundException("Appointment not found with ID: " + appointmentId);
    }
    appointmentRepository.deleteById(appointmentId);
  }

  public Treatment addTreatmentToAppointment(Long appointmentId, Treatment treatmentRequest) {
    if (treatmentRequest == null) {
      throw new InvalidRequestException("Treatment details cannot be null.");
    }

    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

    // Associate the treatment with the appointment
    treatmentRequest.setAppointment(appointment);

    return treatmentRepository.save(treatmentRequest);
  }

  public Appointment updateAppointmentNotes(Long appointmentId, String notes) {
    if (appointmentId == null || notes == null) {
      throw new InvalidRequestException("Appointment ID and notes cannot be null.");
    }

    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

    appointment.setAdditionalNotes(notes);

    return appointmentRepository.save(appointment);
  }

  // Utility method to map an Appointment entity to AppointmentDTO
  private AppointmentDTO mapToDTO(Appointment appointment) {
    AppointmentDTO dto = new AppointmentDTO();
    dto.setId(appointment.getId());
    dto.setDateTime(appointment.getDateTime());
    dto.setSessionDuration(appointment.getSessionDuration());
    dto.setStatus(appointment.getStatus().toString());
    dto.setAdditionalNotes(appointment.getAdditionalNotes());

    TherapistDTO therapistDTO = new TherapistDTO();
    therapistDTO.setId(appointment.getTherapist().getId());
    therapistDTO.setName(appointment.getTherapist().getName());
    dto.setTherapist(therapistDTO);

    PatientDTO patientDTO = new PatientDTO();
    patientDTO.setId(appointment.getPatient().getId());
    patientDTO.setName(appointment.getPatient().getName());
    dto.setPatient(patientDTO);

    return dto;
  }
}
