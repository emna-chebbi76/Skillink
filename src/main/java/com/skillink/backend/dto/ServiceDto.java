package com.skillink.backend.dto;

import com.skillink.backend.entity.ServiceEntity;
import lombok.Data;

@Data
public class ServiceDto {
    private Long id;
    private String titre;
    private String description;
    private String categorie;
    private Double prix;
    private String unite;
    private String ville;
    private String quartier;
    private Double lat;
    private Double lng;
    private String image;
    private UserDto prestataire;
    private Long prestataireId;
    private String disponibilite;
    private Boolean actif;
    private Double noteMoyenne;
    private Integer nbAvis;

    public static ServiceDto from(ServiceEntity s) {
        ServiceDto dto = new ServiceDto();
        dto.setId(s.getId());
        dto.setTitre(s.getTitre());
        dto.setDescription(s.getDescription());
        dto.setCategorie(s.getCategorie());
        dto.setPrix(s.getPrix());
        dto.setUnite(s.getUnite());
        dto.setVille(s.getVille());
        dto.setQuartier(s.getQuartier());
        dto.setLat(s.getLat());
        dto.setLng(s.getLng());
        dto.setImage(s.getImage());
        dto.setDisponibilite(s.getDisponibilite());
        dto.setActif(s.getActif());
        dto.setNoteMoyenne(s.getNoteMoyenne());
        dto.setNbAvis(s.getNbAvis());
        if (s.getPrestataire() != null) {
            dto.setPrestataire(UserDto.from(s.getPrestataire()));
            dto.setPrestataireId(s.getPrestataire().getId());
        }
        return dto;
    }
}
