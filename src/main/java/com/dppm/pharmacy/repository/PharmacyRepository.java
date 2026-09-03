package com.dppm.pharmacy.repository;

import com.dppm.pharmacy.entity.Pharmacy;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PharmacyRepository extends ReactiveCrudRepository<Pharmacy, Long> {
    Mono<Boolean> existsByLicenseNumber(String licenseNumber);
    Mono<Boolean> existsByEmail(String email);
    Flux<Pharmacy> findByNameContainingIgnoreCaseOrCityContainingIgnoreCase(String name, String city);
}
