package com.clinic.service;

import com.clinic.model.Medication;
import com.clinic.model.Prescription;
import com.clinic.model.ReminderLog;
import com.clinic.repository.ReminderLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {

    private final ReminderLogRepository reminderLogRepository;

    /**
     * Called after a prescription is persisted.
     * Creates one PENDING reminder log per medication for Day 1.
     * A real scheduler would expand this across the full duration.
     */
    @Transactional
    public void scheduleReminders(Prescription prescription) {
        List<ReminderLog> reminders = new ArrayList<>();

        for (Medication med : prescription.getMedications()) {
            // Derive a simple first-dose time (now + 1 hour as a placeholder)
            LocalDateTime firstDose = prescription.getCreatedAt().plusHours(1);

            ReminderLog reminder = ReminderLog.builder()
                    .prescription(prescription)
                    .medication(med)
                    .scheduledAt(firstDose)
                    .status("PENDING")
                    .message(buildMessage(prescription, med))
                    .build();

            reminders.add(reminder);
        }

        reminderLogRepository.saveAll(reminders);
        log.info("Scheduled {} reminder(s) for prescription id={}",
                reminders.size(), prescription.getId());
    }

    private String buildMessage(Prescription prescription, Medication med) {
        return String.format("Reminder for %s: Take %s %s — %s for %s",
                prescription.getPatient().getName(),
                med.getName(),
                med.getDosage(),
                med.getFrequency(),
                med.getDuration());
    }
}
