package com.dppm.pharmacy.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("pharmacy_prescriptions")
public record PharmacyPrescription(
        @Id Long id,
        Long pharmacyId,
        Long prescriptionId,
        Long patientId,
        String status,
        LocalDateTime dispensedAt,
        LocalDateTime createdAt) {
}
