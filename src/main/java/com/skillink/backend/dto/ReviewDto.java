package com.skillink.backend.dto;

import com.skillink.backend.entity.Review;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDto {
    private Long id;
    private Long prestataireId;
    private Long clientId;
    private String clientName;
    private String prestataireNom;
    private String serviceName;
    private Integer rating;
    private String comment;
    private LocalDateTime date;

    public static ReviewDto from(Review r) {
        ReviewDto dto = new ReviewDto();
        dto.setId(r.getId());
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        dto.setDate(r.getDate());
        dto.setServiceName(r.getServiceName());
        if (r.getPrestataire() != null) {
            dto.setPrestataireId(r.getPrestataire().getId());
            dto.setPrestataireNom(r.getPrestataire().getPrenom() + " " + r.getPrestataire().getNom());
        }
        if (r.getClient() != null) {
            dto.setClientId(r.getClient().getId());
            dto.setClientName(r.getClient().getPrenom() + " " + r.getClient().getNom());
        }
        return dto;
    }
}
