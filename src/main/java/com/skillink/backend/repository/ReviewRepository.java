package com.skillink.backend.repository;

import com.skillink.backend.entity.Review;
import com.skillink.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPrestataire(User prestataire);
    List<Review> findByClient(User client);
    boolean existsByPrestataireAndClient(User prestataire, User client);
}
