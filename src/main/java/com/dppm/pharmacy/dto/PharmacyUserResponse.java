package com.dppm.pharmacy.dto;

import com.dppm.pharmacy.entity.PharmacyUser;
import java.time.LocalDateTime;

public record PharmacyUserResponse(
        Long id, Long pharmacyId, Long userId, String role, boolean active,
        LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static PharmacyUserResponse from(PharmacyUser user) {
        return new PharmacyUserResponse(user.id(), user.pharmacyId(), user.userId(), user.role(),
                user.active(), user.createdAt(), user.updatedAt());
    }
}
