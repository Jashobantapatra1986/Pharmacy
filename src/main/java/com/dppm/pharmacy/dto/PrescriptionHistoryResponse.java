package com.dppm.pharmacy.dto;

import com.dppm.pharmacy.entity.PharmacyPrescription;
import java.time.LocalDateTime;

public record PrescriptionHistoryResponse(
        Long id, Long prescriptionId, Long patientId, String status,
        LocalDateTime dispensedAt, LocalDateTime createdAt) {
    public static PrescriptionHistoryResponse from(PharmacyPrescription prescription) {
        return new PrescriptionHistoryResponse(prescription.id(), prescription.prescriptionId(),
                prescription.patientId(), prescription.status(), prescription.dispensedAt(),
                prescription.createdAt());
    }
}
