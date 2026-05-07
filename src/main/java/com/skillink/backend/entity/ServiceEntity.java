package com.skillink.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "services")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private String categorie;

    @Column(nullable = false)
    private Double prix;

    @Column(nullable = false)
    private String unite; // heure | forfait

    @Column(nullable = false)
    private String ville;

    private String quartier;
    private Double lat;
    private Double lng;

    @Column(columnDefinition = "TEXT")
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestataire_id", nullable = false)
    private User prestataire;

    private String disponibilite; // maintenant | semaine | weekend

    @Builder.Default
    private Boolean actif = true;

    @Builder.Default
    private Double noteMoyenne = 0.0;

    @Builder.Default
    private Integer nbAvis = 0;
}
