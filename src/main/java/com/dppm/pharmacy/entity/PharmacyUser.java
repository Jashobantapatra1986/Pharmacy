package com.dppm.pharmacy.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("pharmacy_users")
public record PharmacyUser(
        @Id Long id,
        Long pharmacyId,
        Long userId,
        String role,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
