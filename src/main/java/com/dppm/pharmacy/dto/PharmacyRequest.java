package com.dppm.pharmacy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PharmacyRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 100) String licenseNumber,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(max = 30) String phone,
        @NotBlank @Size(max = 255) String addressLine1,
        @Size(max = 255) String addressLine2,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 100) String state,
        @NotBlank @Size(max = 20) String postalCode) {
}
