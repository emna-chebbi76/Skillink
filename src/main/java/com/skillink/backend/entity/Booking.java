package com.skillink.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Column(nullable = false)
    private LocalDate date;

    private String time;

    @Builder.Default
    private Integer nbPersonnes = 1;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    @Builder.Default
    private String status = "En attente"; // En attente | Confirmé | Terminé | Annulé

    @Builder.Default
    private Boolean hasReviewed = false;
}
