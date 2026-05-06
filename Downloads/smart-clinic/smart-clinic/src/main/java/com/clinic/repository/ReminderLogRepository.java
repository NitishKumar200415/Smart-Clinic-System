package com.clinic.repository;

import com.clinic.model.ReminderLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderLogRepository extends JpaRepository<ReminderLog, Long> {
    List<ReminderLog> findByPrescriptionId(Long prescriptionId);
    List<ReminderLog> findByStatus(String status);
}
