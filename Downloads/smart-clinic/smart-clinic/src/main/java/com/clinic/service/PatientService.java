package com.clinic.service;

import com.clinic.config.ResourceNotFoundException;
import com.clinic.dto.PatientRequest;
import com.clinic.dto.PatientResponse;
import com.clinic.model.Patient;
import com.clinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional
    public PatientResponse createPatient(PatientRequest request) {
        if (patientRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Patient with phone " + request.getPhone() + " already exists");
        }

        Patient patient = Patient.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .build();

        Patient saved = patientRepository.save(patient);
        log.info("Created patient with id={}", saved.getId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        return toResponse(patient);
    }

    // ── Mapper ──────────────────────────────────────────────────────────────
    public static PatientResponse toResponse(Patient p) {
        return PatientResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .phone(p.getPhone())
                .build();
    }
}
