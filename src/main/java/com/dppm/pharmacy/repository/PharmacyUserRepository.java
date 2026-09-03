package com.dppm.pharmacy.repository;

import com.dppm.pharmacy.entity.PharmacyUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PharmacyUserRepository extends ReactiveCrudRepository<PharmacyUser, Long> {
    Flux<PharmacyUser> findByPharmacyId(Long pharmacyId);
    Mono<PharmacyUser> findByPharmacyIdAndUserId(Long pharmacyId, Long userId);
}
