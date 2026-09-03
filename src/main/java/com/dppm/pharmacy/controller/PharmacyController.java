package com.dppm.pharmacy.controller;

import com.dppm.pharmacy.dto.*;
import com.dppm.pharmacy.service.PharmacyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/pharmacies")
public class PharmacyController {
    private final PharmacyService pharmacyService;

    public PharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @PostMapping
    public Mono<ResponseEntity<PharmacyResponse>> register(@Valid @RequestBody PharmacyRequest request) {
        return pharmacyService.register(request).map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping("/{pharmacyId}")
    public Mono<PharmacyResponse> get(@PathVariable Long pharmacyId) {
        return pharmacyService.getById(pharmacyId);
    }

    @PutMapping("/{pharmacyId}")
    public Mono<PharmacyResponse> update(@PathVariable Long pharmacyId, @Valid @RequestBody PharmacyRequest request) {
        return pharmacyService.updateProfile(pharmacyId, request);
    }

    @PatchMapping("/{pharmacyId}/status")
    public Mono<PharmacyResponse> setStatus(@PathVariable Long pharmacyId, @RequestParam boolean active) {
        return pharmacyService.setActive(pharmacyId, active);
    }

    @GetMapping
    public Flux<PharmacyResponse> search(@RequestParam(required = false) String query) {
        return pharmacyService.search(query);
    }

    @GetMapping("/{pharmacyId}/users")
    public Flux<PharmacyUserResponse> users(@PathVariable Long pharmacyId) {
        return pharmacyService.getUsers(pharmacyId);
    }

    @PostMapping("/{pharmacyId}/users")
    public Mono<ResponseEntity<PharmacyUserResponse>> addUser(@PathVariable Long pharmacyId,
                                                                @Valid @RequestBody PharmacyUserRequest request) {
        return pharmacyService.addUser(pharmacyId, request).map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PutMapping("/{pharmacyId}/users/{userId}")
    public Mono<PharmacyUserResponse> updateUser(@PathVariable Long pharmacyId, @PathVariable Long userId,
                                                  @Valid @RequestBody PharmacyUserRequest request) {
        return pharmacyService.updateUser(pharmacyId, userId, request);
    }

    @DeleteMapping("/{pharmacyId}/users/{userId}")
    public Mono<ResponseEntity<Void>> removeUser(@PathVariable Long pharmacyId, @PathVariable Long userId) {
        return pharmacyService.removeUser(pharmacyId, userId).thenReturn(ResponseEntity.noContent().build());
    }

    @GetMapping("/{pharmacyId}/prescriptions/history")
    public Flux<PrescriptionHistoryResponse> prescriptionHistory(@PathVariable Long pharmacyId) {
        return pharmacyService.getPrescriptionHistory(pharmacyId);
    }
}
