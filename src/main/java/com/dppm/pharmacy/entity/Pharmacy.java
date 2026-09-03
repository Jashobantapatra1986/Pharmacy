package com.dppm.pharmacy.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("pharmacies")
public record Pharmacy(
        @Id Long id,
        String name,
        String licenseNumber,
        String email,
        String phone,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        long createdBy,
        long updatedBy) {
}
