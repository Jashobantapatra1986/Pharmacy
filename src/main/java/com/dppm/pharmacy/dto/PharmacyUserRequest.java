package com.dppm.pharmacy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PharmacyUserRequest(
        @NotNull Long userId,
        @NotBlank @Size(max = 50) String role) {
}
