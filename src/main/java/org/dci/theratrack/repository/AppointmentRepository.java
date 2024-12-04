package org.dci.theratrack.repository;

import java.util.List;
import org.dci.theratrack.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

  List<Appointment> getAppointmentsByPatientId(Long patientId);

  List<Appointment> findTop5ByOrderByDateTimeAsc();

  Page<Appointment> findAll(Pageable pageable);

}
