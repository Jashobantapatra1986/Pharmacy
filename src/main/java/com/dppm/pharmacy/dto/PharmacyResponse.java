package com.dppm.pharmacy.dto;

import com.dppm.pharmacy.entity.Pharmacy;
import java.time.LocalDateTime;

public record PharmacyResponse(
        Long id, String name, String licenseNumber, String email, String phone,
        String addressLine1, String addressLine2, String city, String state,
        String postalCode, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {

    public static PharmacyResponse from(Pharmacy pharmacy) {
        return new PharmacyResponse(pharmacy.id(), pharmacy.name(), pharmacy.licenseNumber(),
                pharmacy.email(), pharmacy.phone(), pharmacy.addressLine1(), pharmacy.addressLine2(),
                pharmacy.city(), pharmacy.state(), pharmacy.postalCode(), pharmacy.active(),
                pharmacy.createdAt(), pharmacy.updatedAt());
    }
}
