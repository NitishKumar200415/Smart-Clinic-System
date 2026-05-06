package com.clinic.service;

import com.clinic.config.ResourceNotFoundException;
import com.clinic.dto.DoctorRequest;
import com.clinic.dto.DoctorResponse;
import com.clinic.model.Doctor;
import com.clinic.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;

    @Transactional
    public DoctorResponse createDoctor(DoctorRequest request) {
        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Doctor with email " + request.getEmail() + " already exists");
        }

        Doctor doctor = Doctor.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();

        Doctor saved = doctorRepository.save(doctor);
        log.info("Created doctor with id={}", saved.getId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .map(DoctorService::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
    }

    // ── Mapper ──────────────────────────────────────────────────────────────
    public static DoctorResponse toResponse(Doctor d) {
        return DoctorResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .email(d.getEmail())
                .build();
    }
}
