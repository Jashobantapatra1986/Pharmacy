package com.dppm.pharmacy.repository;

import com.dppm.pharmacy.entity.PharmacyPrescription;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PharmacyPrescriptionRepository extends ReactiveCrudRepository<PharmacyPrescription, Long> {
    Flux<PharmacyPrescription> findByPharmacyIdOrderByCreatedAtDesc(Long pharmacyId);

    Mono<PharmacyPrescription> findByPharmacyIdAndPrescriptionId(Long pharmacyId, Long prescriptionId);
}
