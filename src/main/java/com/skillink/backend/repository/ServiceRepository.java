package com.skillink.backend.repository;

import com.skillink.backend.entity.ServiceEntity;
import com.skillink.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findByPrestataire(User prestataire);
    List<ServiceEntity> findByCategorie(String categorie);
    List<ServiceEntity> findByVille(String ville);
    List<ServiceEntity> findByActifTrue();
    boolean existsByTitre(String titre);
    java.util.Optional<ServiceEntity> findByTitre(String titre);
}
