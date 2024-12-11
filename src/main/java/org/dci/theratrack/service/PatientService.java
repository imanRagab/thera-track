package org.dci.theratrack.service;

import java.util.stream.Collectors;
import org.dci.theratrack.config.PasswordConfig;
import org.dci.theratrack.entity.Appointment;
import org.dci.theratrack.entity.Appointment;
import org.dci.theratrack.entity.Patient;
import org.dci.theratrack.entity.User;
import org.dci.theratrack.enums.UserRole;
import org.dci.theratrack.exceptions.InvalidRequestException;
import org.dci.theratrack.exceptions.ResourceNotFoundException;
import org.dci.theratrack.repository.AppointmentRepository;
import org.dci.theratrack.repository.PatientRepository;
import org.dci.theratrack.repository.UserRepository;
import org.dci.theratrack.request.PatientRequest;
import org.dci.theratrack.request.TherapySessionHistoryRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private AppointmentRepository appointmentRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * Creates a new patient.
   *
   * @param request the patient to create
   * @return the created patient
   * @throws InvalidRequestException if the patient is null
   */
  public Patient createPatient(PatientRequest request) {

    if (request == null) {
      throw new InvalidRequestException("Patient cannot be null.");
    }

    String rawPassword = PasswordConfig.generateRandomPassword();
    String encodedPassword = passwordEncoder.encode(rawPassword);

    Patient patient = new Patient();
    patient.setName(request.getName());
    patient.setSurname(request.getSurname());
    patient.setGender(request.getGender());
    patient.setEmail(request.getEmail());
    patient.setAddress(request.getAddress());
    patient.setBirthDate(request.getBirthdate());
    patient.setPhone(request.getPhone());

    User user = new User();
    user.setUsername(request.getEmail());
    user.setUserRole(UserRole.PATIENT);
    user.setPassword(encodedPassword);
    user.setPatient(patient);
    patient.setUser(user);

    userRepository.save(user); // This cascades to save Patient as well

    //send raw Password to patient via email

    return patient;
  }

  public List<Patient> getAllPatients() {
    return patientRepository.findAll();
  }

  /**
   * Retrieves a patient by ID.
   *
   * @param id the ID of the patient
   * @return the patient
   * @throws ResourceNotFoundException if the patient is not found
   */
  public Patient getPatientById(Long id) {
    return patientRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
  }

  /**
   * Updates an existing patient.
   *
   * @param id             the ID of the patient to update
   * @param updatedPatient the updated patient data
   * @return the updated patient
   * @throws ResourceNotFoundException if the patient is not found
   * @throws InvalidRequestException   if the updated patient is null
   */
  public Patient updatePatient(Long id, Patient updatedPatient) {
    if (updatedPatient == null) {
      throw new InvalidRequestException("Updated patient cannot be null.");
    }

    Patient existingPatient = patientRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));

    modelMapper.typeMap(Patient.class, Patient.class)
        .addMappings(mapper -> mapper.skip(Patient::setId));
    modelMapper.map(updatedPatient, existingPatient);

    return patientRepository.save(existingPatient);
  }

  /**
   * Deletes a patient by ID.
   *
   * @param id the ID of the patient to delete
   * @throws ResourceNotFoundException if the patient does not exist
   */
  public void deletePatient(Long id) {
    if (!patientRepository.existsById(id)) {
      throw new ResourceNotFoundException("Patient not found with ID: " + id);
    }

    // Fetch the patient
    Patient patient = patientRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));

    // Unlink the patient from the user
    User user = patient.getUser();
    if (user != null) {
      //user.setPatient(null);
      //userRepository.save(user); // Save to ensure the link is broken
      userRepository.delete(user);
    }
    patientRepository.deleteById(id);
  }


  public List<TherapySessionHistoryRequest> getTherapySessionHistory(Long patientId) {
    List<Appointment> appointments = appointmentRepository.getAppointmentsByPatientId(patientId);

    return appointments.stream()
        .map(appointment -> modelMapper.map(appointment, TherapySessionHistoryRequest.class))
        .collect(Collectors.toList());
  }

}