package com.dppm.pharmacy.service;

import com.dppm.pharmacy.dto.PharmacyRequest;
import com.dppm.pharmacy.dto.PharmacyUserRequest;
import com.dppm.pharmacy.entity.Pharmacy;
import com.dppm.pharmacy.entity.PharmacyPrescription;
import com.dppm.pharmacy.entity.PharmacyUser;
import com.dppm.pharmacy.exception.DuplicateResourceException;
import com.dppm.pharmacy.exception.ResourceNotFoundException;
import com.dppm.pharmacy.repository.PharmacyPrescriptionRepository;
import com.dppm.pharmacy.repository.PharmacyRepository;
import com.dppm.pharmacy.repository.PharmacyUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PharmacyServiceImplTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2024, 6, 15, 10, 30);

    @Mock
    private PharmacyRepository pharmacyRepository;

    @Mock
    private PharmacyUserRepository pharmacyUserRepository;

    @Mock
    private PharmacyPrescriptionRepository prescriptionRepository;

    @InjectMocks
    private PharmacyServiceImpl pharmacyService;

    private Pharmacy pharmacy;
    private PharmacyRequest pharmacyRequest;

    @BeforeEach
    void setUp() {
        pharmacy = samplePharmacy(1L, true);
        pharmacyRequest = samplePharmacyRequest();
    }

    @Test
    void register_savesPharmacyWhenLicenseAndEmailAreUnique() {
        when(pharmacyRepository.existsByLicenseNumber(pharmacyRequest.licenseNumber())).thenReturn(Mono.just(false));
        when(pharmacyRepository.existsByEmail(pharmacyRequest.email())).thenReturn(Mono.just(false));
        when(pharmacyRepository.save(any(Pharmacy.class))).thenReturn(Mono.just(pharmacy));

        StepVerifier.create(pharmacyService.register(pharmacyRequest))
                .assertNext(response -> {
                    assertThat(response.id()).isEqualTo(1L);
                    assertThat(response.name()).isEqualTo(pharmacyRequest.name());
                    assertThat(response.licenseNumber()).isEqualTo(pharmacyRequest.licenseNumber());
                    assertThat(response.email()).isEqualTo(pharmacyRequest.email());
                    assertThat(response.active()).isTrue();
                })
                .verifyComplete();

        verify(pharmacyRepository).save(any(Pharmacy.class));
    }

    @Test
    void register_failsWhenLicenseNumberAlreadyExists() {
        when(pharmacyRepository.existsByLicenseNumber(pharmacyRequest.licenseNumber())).thenReturn(Mono.just(true));
        when(pharmacyRepository.existsByEmail(pharmacyRequest.email())).thenReturn(Mono.just(false));

        StepVerifier.create(pharmacyService.register(pharmacyRequest))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DuplicateResourceException.class);
                    assertThat(error.getMessage()).isEqualTo("License number is already registered");
                })
                .verify();

        verify(pharmacyRepository, never()).save(any(Pharmacy.class));
    }

    @Test
    void register_failsWhenEmailAlreadyExists() {
        when(pharmacyRepository.existsByLicenseNumber(pharmacyRequest.licenseNumber())).thenReturn(Mono.just(false));
        when(pharmacyRepository.existsByEmail(pharmacyRequest.email())).thenReturn(Mono.just(true));

        StepVerifier.create(pharmacyService.register(pharmacyRequest))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DuplicateResourceException.class);
                    assertThat(error.getMessage()).isEqualTo("Email is already registered");
                })
                .verify();

        verify(pharmacyRepository, never()).save(any(Pharmacy.class));
    }

    @Test
    void getById_returnsPharmacyWhenFound() {
        when(pharmacyRepository.findById(1L)).thenReturn(Mono.just(pharmacy));

        StepVerifier.create(pharmacyService.getById(1L))
                .assertNext(response -> assertThat(response.id()).isEqualTo(1L))
                .verifyComplete();
    }

    @Test
    void getById_failsWhenPharmacyNotFound() {
        when(pharmacyRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(pharmacyService.getById(99L))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void updateProfile_updatesExistingPharmacy() {
        Pharmacy updated = samplePharmacy(1L, true);
        when(pharmacyRepository.findById(1L)).thenReturn(Mono.just(pharmacy));
        when(pharmacyRepository.save(any(Pharmacy.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(pharmacyService.updateProfile(1L, pharmacyRequest))
                .assertNext(response -> assertThat(response.id()).isEqualTo(1L))
                .verifyComplete();

        ArgumentCaptor<Pharmacy> captor = ArgumentCaptor.forClass(Pharmacy.class);
        verify(pharmacyRepository).save(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(1L);
        assertThat(captor.getValue().name()).isEqualTo(pharmacyRequest.name());
    }

    @Test
    void setActive_updatesActiveFlag() {
        Pharmacy deactivated = samplePharmacy(1L, false);
        when(pharmacyRepository.findById(1L)).thenReturn(Mono.just(pharmacy));
        when(pharmacyRepository.save(any(Pharmacy.class))).thenReturn(Mono.just(deactivated));

        StepVerifier.create(pharmacyService.setActive(1L, false))
                .assertNext(response -> assertThat(response.active()).isFalse())
                .verifyComplete();
    }

    @Test
    void search_withBlankQueryReturnsAllPharmacies() {
        when(pharmacyRepository.findAll()).thenReturn(Flux.just(pharmacy));

        StepVerifier.create(pharmacyService.search("  "))
                .assertNext(response -> assertThat(response.id()).isEqualTo(1L))
                .verifyComplete();

        verify(pharmacyRepository).findAll();
        verify(pharmacyRepository, never()).findByNameContainingIgnoreCaseOrCityContainingIgnoreCase(anyString(), anyString());
    }

    @Test
    void search_withQueryFiltersByNameOrCity() {
        when(pharmacyRepository.findByNameContainingIgnoreCaseOrCityContainingIgnoreCase("spring", "spring"))
                .thenReturn(Flux.just(pharmacy));

        StepVerifier.create(pharmacyService.search(" spring "))
                .assertNext(response -> assertThat(response.city()).isEqualTo("Springfield"))
                .verifyComplete();
    }

    @Test
    void getUsers_returnsUsersForPharmacy() {
        PharmacyUser user = samplePharmacyUser(10L, 1L, 100L);
        when(pharmacyRepository.findById(1L)).thenReturn(Mono.just(pharmacy));
        when(pharmacyUserRepository.findByPharmacyId(1L)).thenReturn(Flux.just(user));

        StepVerifier.create(pharmacyService.getUsers(1L))
                .assertNext(response -> {
                    assertThat(response.userId()).isEqualTo(100L);
                    assertThat(response.pharmacyId()).isEqualTo(1L);
                })
                .verifyComplete();
    }

    @Test
    void addUser_savesNewUserWhenNotAlreadyAssigned() {
        PharmacyUserRequest request = new PharmacyUserRequest(100L, "PHARMACIST");
        PharmacyUser saved = samplePharmacyUser(10L, 1L, 100L);
        when(pharmacyRepository.findById(1L)).thenReturn(Mono.just(pharmacy));
        when(pharmacyUserRepository.findByPharmacyIdAndUserId(1L, 100L)).thenReturn(Mono.empty());
        when(pharmacyUserRepository.save(any(PharmacyUser.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(pharmacyService.addUser(1L, request))
                .assertNext(response -> {
                    assertThat(response.userId()).isEqualTo(100L);
                    assertThat(response.role()).isEqualTo("PHARMACIST");
                })
                .verifyComplete();
    }

    @Test
    void addUser_failsWhenUserAlreadyAssigned() {
        PharmacyUserRequest request = new PharmacyUserRequest(100L, "PHARMACIST");
        PharmacyUser existing = samplePharmacyUser(10L, 1L, 100L);
        when(pharmacyRepository.findById(1L)).thenReturn(Mono.just(pharmacy));
        when(pharmacyUserRepository.findByPharmacyIdAndUserId(1L, 100L)).thenReturn(Mono.just(existing));

        StepVerifier.create(pharmacyService.addUser(1L, request))
                .expectError(DuplicateResourceException.class)
                .verify();

        verify(pharmacyUserRepository, never()).save(any(PharmacyUser.class));
    }

    @Test
    void updateUser_updatesRoleWhenUserExists() {
        PharmacyUserRequest request = new PharmacyUserRequest(100L, "MANAGER");
        PharmacyUser existing = samplePharmacyUser(10L, 1L, 100L);
        PharmacyUser updated = new PharmacyUser(10L, 1L, 100L, "MANAGER", true, NOW, NOW);
        when(pharmacyRepository.findById(1L)).thenReturn(Mono.just(pharmacy));
        when(pharmacyUserRepository.findByPharmacyIdAndUserId(1L, 100L)).thenReturn(Mono.just(existing));
        when(pharmacyUserRepository.save(any(PharmacyUser.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(pharmacyService.updateUser(1L, 100L, request))
                .assertNext(response -> assertThat(response.role()).isEqualTo("MANAGER"))
                .verifyComplete();
    }

    @Test
    void updateUser_failsWhenUserNotFound() {
        PharmacyUserRequest request = new PharmacyUserRequest(100L, "MANAGER");
        when(pharmacyRepository.findById(1L)).thenReturn(Mono.just(pharmacy));
        when(pharmacyUserRepository.findByPharmacyIdAndUserId(1L, 100L)).thenReturn(Mono.empty());

        StepVerifier.create(pharmacyService.updateUser(1L, 100L, request))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void removeUser_deletesExistingUser() {
        PharmacyUser existing = samplePharmacyUser(10L, 1L, 100L);
        when(pharmacyRepository.findById(1L)).thenReturn(Mono.just(pharmacy));
        when(pharmacyUserRepository.findByPharmacyIdAndUserId(1L, 100L)).thenReturn(Mono.just(existing));
        when(pharmacyUserRepository.delete(existing)).thenReturn(Mono.empty());

        StepVerifier.create(pharmacyService.removeUser(1L, 100L))
                .verifyComplete();

        verify(pharmacyUserRepository).delete(existing);
    }

    @Test
    void removeUser_failsWhenUserNotFound() {
        when(pharmacyRepository.findById(1L)).thenReturn(Mono.just(pharmacy));
        when(pharmacyUserRepository.findByPharmacyIdAndUserId(1L, 100L)).thenReturn(Mono.empty());

        StepVerifier.create(pharmacyService.removeUser(1L, 100L))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void getPrescriptionHistory_returnsPrescriptionsOrderedByCreatedAt() {
        PharmacyPrescription prescription = new PharmacyPrescription(
                5L, 1L, 200L, 300L, "DISPENSED", NOW, NOW);
        when(pharmacyRepository.findById(1L)).thenReturn(Mono.just(pharmacy));
        when(prescriptionRepository.findByPharmacyIdOrderByCreatedAtDesc(1L)).thenReturn(Flux.just(prescription));

        StepVerifier.create(pharmacyService.getPrescriptionHistory(1L))
                .assertNext(response -> {
                    assertThat(response.prescriptionId()).isEqualTo(200L);
                    assertThat(response.patientId()).isEqualTo(300L);
                    assertThat(response.status()).isEqualTo("DISPENSED");
                })
                .verifyComplete();

        verify(prescriptionRepository).findByPharmacyIdOrderByCreatedAtDesc(eq(1L));
    }

    private static Pharmacy samplePharmacy(Long id, boolean active) {
        return new Pharmacy(id, "Test Pharmacy", "LIC-001", "test@pharmacy.com", "555-0100",
                "123 Main St", null, "Springfield", "IL", "62701", active, NOW, NOW, 0L, 0L);
    }

    private static PharmacyRequest samplePharmacyRequest() {
        return new PharmacyRequest("Test Pharmacy", "LIC-001", "test@pharmacy.com", "555-0100",
                "123 Main St", null, "Springfield", "IL", "62701");
    }

    private static PharmacyUser samplePharmacyUser(Long id, Long pharmacyId, Long userId) {
        return new PharmacyUser(id, pharmacyId, userId, "PHARMACIST", true, NOW, NOW);
    }
}
