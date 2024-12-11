package org.dci.theratrack.controller;

import org.dci.theratrack.repository.AppointmentRepository;
import org.dci.theratrack.repository.PatientRepository;
import org.dci.theratrack.repository.TherapistRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PatientRepository patientRepository;
    private final TherapistRepository therapistRepository;
    private final AppointmentRepository appointmentRepository;

    public DashboardController(PatientRepository patientRepository,
                               TherapistRepository therapistRepository,
                               AppointmentRepository appointmentRepository) {
        this.patientRepository = patientRepository;
        this.therapistRepository = therapistRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> dashboardData = new HashMap<>();

        // Aggregate data
        dashboardData.put("patientsCount", patientRepository.count());
        dashboardData.put("therapistsCount", therapistRepository.count());
        dashboardData.put("appointmentsCount", appointmentRepository.count());

        // Example: Upcoming Appointments
        dashboardData.put("upcomingAppointments", appointmentRepository.findTop5ByOrderByDateTimeAsc());

        return ResponseEntity.ok(dashboardData);
    }
}

