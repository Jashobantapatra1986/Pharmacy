package com.dppm.pharmacy.service;

import com.dppm.pharmacy.dto.PharmacyRequest;
import com.dppm.pharmacy.dto.PharmacyResponse;
import com.dppm.pharmacy.dto.PharmacyUserRequest;
import com.dppm.pharmacy.dto.PharmacyUserResponse;
import com.dppm.pharmacy.dto.PrescriptionHistoryResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** Business operations for pharmacy registration, administration and history. */
public interface PharmacyService {
    Mono<PharmacyResponse> register(PharmacyRequest request);
    Mono<PharmacyResponse> getById(Long pharmacyId);
    Mono<PharmacyResponse> updateProfile(Long pharmacyId, PharmacyRequest request);
    Mono<PharmacyResponse> setActive(Long pharmacyId, boolean active);
    Flux<PharmacyResponse> search(String query);
    Flux<PharmacyUserResponse> getUsers(Long pharmacyId);
    Mono<PharmacyUserResponse> addUser(Long pharmacyId, PharmacyUserRequest request);
    Mono<PharmacyUserResponse> updateUser(Long pharmacyId, Long userId, PharmacyUserRequest request);
    Mono<Void> removeUser(Long pharmacyId, Long userId);
    Flux<PrescriptionHistoryResponse> getPrescriptionHistory(Long pharmacyId);
}
