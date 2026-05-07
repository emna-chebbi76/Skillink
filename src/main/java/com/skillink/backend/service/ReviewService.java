package com.skillink.backend.service;

import com.skillink.backend.dto.ReviewDto;
import com.skillink.backend.entity.Review;
import com.skillink.backend.entity.User;
import com.skillink.backend.exception.ResourceNotFoundException;
import com.skillink.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    public List<ReviewDto> getAll() {
        return reviewRepository.findAll().stream().map(ReviewDto::from).toList();
    }

    public List<ReviewDto> getByPrestataire(Long prestataireId) {
        User prestataire = userRepository.findById(prestataireId)
                .orElseThrow(() -> new ResourceNotFoundException("Prestataire non trouvé"));
        return reviewRepository.findByPrestataire(prestataire).stream().map(ReviewDto::from).toList();
    }

    public List<ReviewDto> getByClient(Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));
        return reviewRepository.findByClient(client).stream().map(ReviewDto::from).toList();
    }

    public ReviewDto create(Map<String, Object> body, String email) {
        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Long prestataireId = toLong(body.get("prestataireId"));
        User prestataire = userRepository.findById(prestataireId)
                .orElseThrow(() -> new ResourceNotFoundException("Prestataire non trouvé"));

        Review review = Review.builder()
                .prestataire(prestataire)
                .client(client)
                .rating(toInt(body.get("rating")))
                .comment((String) body.get("comment"))
                .serviceName((String) body.get("serviceName"))
                .build();

        // Recalculer la note moyenne
        Review saved = reviewRepository.save(review);
        updateServiceStats(prestataire);

        return ReviewDto.from(saved);
    }

    public void delete(Long id) {
        Review r = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avis non trouvé : " + id));
        User prestataire = r.getPrestataire();
        reviewRepository.deleteById(id);
        updateServiceStats(prestataire);
    }

    private void updateServiceStats(User prestataire) {
        var reviews = reviewRepository.findByPrestataire(prestataire);
        double avg = reviews.isEmpty() ? 0.0
                : Math.round(reviews.stream().mapToInt(Review::getRating).average().orElse(0) * 10.0) / 10.0;
        int nb = reviews.size();
        serviceRepository.findByPrestataire(prestataire).forEach(s -> {
            s.setNoteMoyenne(avg);
            s.setNbAvis(nb);
            serviceRepository.save(s);
        });
    }

    private Long toLong(Object o) {
        if (o instanceof Number n) return n.longValue();
        return Long.parseLong(o.toString());
    }
    private Integer toInt(Object o) {
        if (o instanceof Number n) return n.intValue();
        return Integer.parseInt(o.toString());
    }
}
