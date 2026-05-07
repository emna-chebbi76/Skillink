package com.skillink.backend.dto;

import com.skillink.backend.entity.Booking;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingDto {
    private Long id;
    private Long clientId;
    private Long serviceId;
    private LocalDate date;
    private String time;
    private Integer nbPersonnes;
    private Double total;
    private String status;
    private Boolean hasReviewed;

    // Nested objects (enrichissement)
    private ServiceSummary service;
    private UserDto client;

    @Data
    public static class ServiceSummary {
        private Long id;
        private String titre;
        private String image;
        private String categorie;
        private Double prix;
        private String ville;
        private Long prestataireId;
    }

    public static BookingDto from(Booking b) {
        BookingDto dto = new BookingDto();
        dto.setId(b.getId());
        dto.setDate(b.getDate());
        dto.setTime(b.getTime());
        dto.setNbPersonnes(b.getNbPersonnes());
        dto.setTotal(b.getTotal());
        dto.setStatus(b.getStatus());
        dto.setHasReviewed(b.getHasReviewed());

        if (b.getClient() != null) {
            dto.setClientId(b.getClient().getId());
            dto.setClient(UserDto.from(b.getClient()));
        }
        if (b.getService() != null) {
            dto.setServiceId(b.getService().getId());
            ServiceSummary ss = new ServiceSummary();
            ss.setId(b.getService().getId());
            ss.setTitre(b.getService().getTitre());
            ss.setImage(b.getService().getImage());
            ss.setCategorie(b.getService().getCategorie());
            ss.setPrix(b.getService().getPrix());
            ss.setVille(b.getService().getVille());
            if (b.getService().getPrestataire() != null)
                ss.setPrestataireId(b.getService().getPrestataire().getId());
            dto.setService(ss);
        }
        return dto;
    }
}
