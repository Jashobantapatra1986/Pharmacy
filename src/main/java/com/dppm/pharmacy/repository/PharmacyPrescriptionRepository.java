package com.dppm.pharmacy.repository;

import com.dppm.pharmacy.entity.PharmacyPrescription;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PharmacyPrescriptionRepository extends ReactiveCrudRepository<PharmacyPrescription, Long> {
    Flux<PharmacyPrescription> findByPharmacyIdOrderByCreatedAtDesc(Long pharmacyId);
}
