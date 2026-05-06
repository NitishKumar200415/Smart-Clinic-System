package com.clinic.service;

import com.clinic.config.ResourceNotFoundException;
import com.clinic.dto.*;
import com.clinic.model.Doctor;
import com.clinic.model.Medication;
import com.clinic.model.Patient;
import com.clinic.model.Prescription;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.PatientRepository;
import com.clinic.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ReminderService reminderService;

    @Transactional
    public PrescriptionResponse createPrescription(PrescriptionRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", request.getPatientId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", request.getDoctorId()));

        // Build prescription shell (medications added below)
        Prescription prescription = Prescription.builder()
                .patient(patient)
                .doctor(doctor)
                .diagnosis(request.getDiagnosis())
                .build();

        // Build and attach medications
        List<Medication> medications = request.getMedications().stream()
                .map(medReq -> Medication.builder()
                        .prescription(prescription)
                        .name(medReq.getName())
                        .dosage(medReq.getDosage())
                        .frequency(medReq.getFrequency())
                        .duration(medReq.getDuration())
                        .build())
                .collect(Collectors.toList());

        prescription.getMedications().addAll(medications);

        Prescription saved = prescriptionRepository.save(prescription);
        log.info("Created prescription id={} for patient id={}", saved.getId(), patient.getId());

        // Trigger reminder scheduling (no scheduler wiring yet — just logs)
        reminderService.scheduleReminders(saved);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionsByPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient", patientId);
        }
        return prescriptionRepository.findByPatientIdWithDetails(patientId)
                .stream()
                .map(PrescriptionService::toResponse)
                .collect(Collectors.toList());
    }

    // ── Mapper ──────────────────────────────────────────────────────────────
    public static PrescriptionResponse toResponse(Prescription p) {
        List<MedicationResponse> meds = p.getMedications().stream()
                .map(m -> MedicationResponse.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .dosage(m.getDosage())
                        .frequency(m.getFrequency())
                        .duration(m.getDuration())
                        .build())
                .collect(Collectors.toList());

        return PrescriptionResponse.builder()
                .id(p.getId())
                .patientId(p.getPatient().getId())
                .patientName(p.getPatient().getName())
                .doctorId(p.getDoctor().getId())
                .doctorName(p.getDoctor().getName())
                .diagnosis(p.getDiagnosis())
                .createdAt(p.getCreatedAt())
                .medications(meds)
                .build();
    }
}
