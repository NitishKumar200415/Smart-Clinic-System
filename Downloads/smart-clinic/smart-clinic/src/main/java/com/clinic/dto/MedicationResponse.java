package com.clinic.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicationResponse {
    private Long id;
    private String name;
    private String dosage;
    private String frequency;
    private String duration;
}
