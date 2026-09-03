package com.dppm.pharmacy.controller;

import com.dppm.pharmacy.dto.PharmacyRequest;
import com.dppm.pharmacy.dto.PharmacyResponse;
import com.dppm.pharmacy.dto.PharmacyUserRequest;
import com.dppm.pharmacy.dto.PharmacyUserResponse;
import com.dppm.pharmacy.dto.PrescriptionHistoryResponse;
import com.dppm.pharmacy.service.PharmacyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(PharmacyController.class)
class PharmacyControllerTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2024, 6, 15, 10, 30);

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PharmacyService pharmacyService;

    @Test
    void register_returnsCreatedPharmacy() {
        PharmacyResponse response = samplePharmacyResponse(1L, true);
        when(pharmacyService.register(any(PharmacyRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/v1/pharmacies")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(samplePharmacyRequestJson())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test Pharmacy")
                .jsonPath("$.licenseNumber").isEqualTo("LIC-001")
                .jsonPath("$.active").isEqualTo(true);
    }

    @Test
    void get_returnsPharmacyById() {
        PharmacyResponse response = samplePharmacyResponse(1L, true);
        when(pharmacyService.getById(1L)).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/api/v1/pharmacies/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.email").isEqualTo("test@pharmacy.com");
    }

    @Test
    void update_updatesPharmacyProfile() {
        PharmacyResponse response = samplePharmacyResponse(1L, true);
        when(pharmacyService.updateProfile(eq(1L), any(PharmacyRequest.class))).thenReturn(Mono.just(response));

        webTestClient.put()
                .uri("/api/v1/pharmacies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(samplePharmacyRequestJson())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.city").isEqualTo("Springfield");
    }

    @Test
    void setStatus_updatesActiveFlag() {
        PharmacyResponse response = samplePharmacyResponse(1L, false);
        when(pharmacyService.setActive(1L, false)).thenReturn(Mono.just(response));

        webTestClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/pharmacies/1/status").queryParam("active", false).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.active").isEqualTo(false);
    }

    @Test
    void search_returnsMatchingPharmacies() {
        PharmacyResponse response = samplePharmacyResponse(1L, true);
        when(pharmacyService.search("spring")).thenReturn(Flux.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/pharmacies").queryParam("query", "spring").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Test Pharmacy");
    }

    @Test
    void users_returnsPharmacyUsers() {
        PharmacyUserResponse user = samplePharmacyUserResponse(10L, 1L, 100L);
        when(pharmacyService.getUsers(1L)).thenReturn(Flux.just(user));

        webTestClient.get()
                .uri("/api/v1/pharmacies/1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(10)
                .jsonPath("$[0].userId").isEqualTo(100)
                .jsonPath("$[0].role").isEqualTo("PHARMACIST");
    }

    @Test
    void addUser_returnsCreatedUser() {
        PharmacyUserResponse response = samplePharmacyUserResponse(10L, 1L, 100L);
        when(pharmacyService.addUser(eq(1L), any(PharmacyUserRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/v1/pharmacies/1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"userId\":100,\"role\":\"PHARMACIST\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(10)
                .jsonPath("$.pharmacyId").isEqualTo(1)
                .jsonPath("$.userId").isEqualTo(100);
    }

    @Test
    void updateUser_updatesExistingUser() {
        PharmacyUserResponse response = new PharmacyUserResponse(10L, 1L, 100L, "MANAGER", true, NOW, NOW);
        when(pharmacyService.updateUser(eq(1L), eq(100L), any(PharmacyUserRequest.class))).thenReturn(Mono.just(response));

        webTestClient.put()
                .uri("/api/v1/pharmacies/1/users/100")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"userId\":100,\"role\":\"MANAGER\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.role").isEqualTo("MANAGER");
    }

    @Test
    void removeUser_returnsNoContent() {
        when(pharmacyService.removeUser(1L, 100L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/pharmacies/1/users/100")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void prescriptionHistory_returnsPrescriptionList() {
        PrescriptionHistoryResponse history = new PrescriptionHistoryResponse(5L, 200L, 300L, "DISPENSED", NOW, NOW);
        when(pharmacyService.getPrescriptionHistory(1L)).thenReturn(Flux.just(history));

        webTestClient.get()
                .uri("/api/v1/pharmacies/1/prescriptions/history")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(5)
                .jsonPath("$[0].prescriptionId").isEqualTo(200)
                .jsonPath("$[0].status").isEqualTo("DISPENSED");
    }

    private static PharmacyResponse samplePharmacyResponse(Long id, boolean active) {
        return new PharmacyResponse(id, "Test Pharmacy", "LIC-001", "test@pharmacy.com", "555-0100",
                "123 Main St", null, "Springfield", "IL", "62701", active, NOW, NOW);
    }

    private static PharmacyUserResponse samplePharmacyUserResponse(Long id, Long pharmacyId, Long userId) {
        return new PharmacyUserResponse(id, pharmacyId, userId, "PHARMACIST", true, NOW, NOW);
    }

    private static String samplePharmacyRequestJson() {
        return """
                {
                  "name": "Test Pharmacy",
                  "licenseNumber": "LIC-001",
                  "email": "test@pharmacy.com",
                  "phone": "555-0100",
                  "addressLine1": "123 Main St",
                  "city": "Springfield",
                  "state": "IL",
                  "postalCode": "62701"
                }
                """;
    }
}
