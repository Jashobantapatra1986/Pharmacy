package com.dppm.pharmacy.service;

import com.dppm.pharmacy.dto.PharmacyRequest;
import com.dppm.pharmacy.dto.PharmacyResponse;
import com.dppm.pharmacy.dto.PharmacyUserRequest;
import com.dppm.pharmacy.dto.PharmacyUserResponse;
import com.dppm.pharmacy.dto.PrescriptionHistoryResponse;
import com.dppm.pharmacy.entity.Pharmacy;
import com.dppm.pharmacy.entity.PharmacyUser;
import com.dppm.pharmacy.exception.DuplicateResourceException;
import com.dppm.pharmacy.exception.ResourceNotFoundException;
import com.dppm.pharmacy.repository.PharmacyPrescriptionRepository;
import com.dppm.pharmacy.repository.PharmacyRepository;
import com.dppm.pharmacy.repository.PharmacyUserRepository;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PharmacyServiceImpl implements PharmacyService {
    private final PharmacyRepository pharmacyRepository;
    private final PharmacyUserRepository pharmacyUserRepository;
    private final PharmacyPrescriptionRepository prescriptionRepository;

    public PharmacyServiceImpl(PharmacyRepository pharmacyRepository,
                               PharmacyUserRepository pharmacyUserRepository,
                               PharmacyPrescriptionRepository prescriptionRepository) {
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacyUserRepository = pharmacyUserRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @Override
    public Mono<PharmacyResponse> register(PharmacyRequest request) {
        return Mono.zip(pharmacyRepository.existsByLicenseNumber(request.licenseNumber()),
                        pharmacyRepository.existsByEmail(request.email()))
                .flatMap(existing -> {
                    if (existing.getT1()) return Mono.error(new DuplicateResourceException("License number is already registered"));
                    if (existing.getT2()) return Mono.error(new DuplicateResourceException("Email is already registered"));
                    return pharmacyRepository.save(toPharmacy(null, request, true, null)).map(PharmacyResponse::from);
                });
    }

    @Override
    public Mono<PharmacyResponse> getById(Long pharmacyId) {
        return getPharmacy(pharmacyId).map(PharmacyResponse::from);
    }

    @Override
    public Mono<PharmacyResponse> updateProfile(Long pharmacyId, PharmacyRequest request) {
        return getPharmacy(pharmacyId)
                .flatMap(current -> pharmacyRepository.save(toPharmacy(current, request, current.active(), current.createdAt())))
                .map(PharmacyResponse::from);
    }

    @Override
    public Mono<PharmacyResponse> setActive(Long pharmacyId, boolean active) {
        return getPharmacy(pharmacyId)
                .flatMap(current -> pharmacyRepository.save(new Pharmacy(current.id(), current.name(), current.licenseNumber(),
                        current.email(), current.phone(), current.addressLine1(), current.addressLine2(), current.city(),
                        current.state(), current.postalCode(), active, current.createdAt(), LocalDateTime.now(),
                        current.createdBy(), current.updatedBy())))
                .map(PharmacyResponse::from);
    }

    @Override
    public Flux<PharmacyResponse> search(String query) {
        if (query == null || query.isBlank()) return pharmacyRepository.findAll().map(PharmacyResponse::from);
        String term = query.trim();
        return pharmacyRepository.findByNameContainingIgnoreCaseOrCityContainingIgnoreCase(term, term)
                .map(PharmacyResponse::from);
    }

    @Override
    public Flux<PharmacyUserResponse> getUsers(Long pharmacyId) {
        return getPharmacy(pharmacyId).flatMapMany(p -> pharmacyUserRepository.findByPharmacyId(pharmacyId))
                .map(PharmacyUserResponse::from);
    }

    @Override
    public Mono<PharmacyUserResponse> addUser(Long pharmacyId, PharmacyUserRequest request) {
        return getPharmacy(pharmacyId).then(pharmacyUserRepository.findByPharmacyIdAndUserId(pharmacyId, request.userId())
                .flatMap(found -> Mono.<PharmacyUserResponse>error(new DuplicateResourceException("User is already assigned to this pharmacy")))
                .switchIfEmpty(Mono.defer(() -> pharmacyUserRepository.save(new PharmacyUser(null, pharmacyId, request.userId(),
                        request.role(), true, null, null)).map(PharmacyUserResponse::from))));
    }

    @Override
    public Mono<PharmacyUserResponse> updateUser(Long pharmacyId, Long userId, PharmacyUserRequest request) {
        return getPharmacy(pharmacyId).then(pharmacyUserRepository.findByPharmacyIdAndUserId(pharmacyId, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Pharmacy user " + userId + " was not found")))
                .flatMap(current -> pharmacyUserRepository.save(new PharmacyUser(current.id(), pharmacyId, current.userId(),
                        request.role(), current.active(), current.createdAt(), LocalDateTime.now())))
                .map(PharmacyUserResponse::from));
    }

    @Override
    public Mono<Void> removeUser(Long pharmacyId, Long userId) {
        return getPharmacy(pharmacyId).then(pharmacyUserRepository.findByPharmacyIdAndUserId(pharmacyId, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Pharmacy user " + userId + " was not found")))
                .flatMap(pharmacyUserRepository::delete));
    }

    @Override
    public Flux<PrescriptionHistoryResponse> getPrescriptionHistory(Long pharmacyId) {
        return getPharmacy(pharmacyId).flatMapMany(p -> prescriptionRepository.findByPharmacyIdOrderByCreatedAtDesc(pharmacyId))
                .map(PrescriptionHistoryResponse::from);
    }

    private Mono<Pharmacy> getPharmacy(Long id) {
        return pharmacyRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Pharmacy " + id + " was not found")));
    }

    private Pharmacy toPharmacy(Pharmacy current, PharmacyRequest request, boolean active, LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        return new Pharmacy(current == null ? null : current.id(), request.name(), request.licenseNumber(), request.email(),
                request.phone(), request.addressLine1(), request.addressLine2(), request.city(), request.state(),
                request.postalCode(), active, createdAt == null ? now : createdAt, now,
                current == null ? 0L : current.createdBy(), current == null ? 0L : current.updatedBy());
    }
}
