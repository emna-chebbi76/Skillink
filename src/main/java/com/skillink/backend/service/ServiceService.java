package com.skillink.backend.service;

import com.skillink.backend.dto.ServiceDto;
import com.skillink.backend.entity.ServiceEntity;
import com.skillink.backend.entity.User;
import com.skillink.backend.exception.ResourceNotFoundException;
import com.skillink.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public List<ServiceDto> getAll() {
        return serviceRepository.findAll().stream()
                .map(s -> enrichWithStats(ServiceDto.from(s), s))
                .toList();
    }

    public ServiceDto getById(Long id) {
        ServiceEntity s = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service non trouvé : " + id));
        return enrichWithStats(ServiceDto.from(s), s);
    }

    public List<ServiceDto> getMine(String email) {
        User prestataire = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return serviceRepository.findByPrestataire(prestataire).stream()
                .map(s -> enrichWithStats(ServiceDto.from(s), s))
                .toList();
    }

    public ServiceDto create(Map<String, Object> body, String email) {
        User prestataire = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        ServiceEntity s = ServiceEntity.builder()
                .titre((String) body.get("titre"))
                .description((String) body.get("description"))
                .categorie((String) body.get("categorie"))
                .prix(toDouble(body.get("prix")))
                .unite((String) body.getOrDefault("unite", "heure"))
                .ville((String) body.get("ville"))
                .quartier((String) body.get("quartier"))
                .lat(toDouble(body.get("lat")))
                .lng(toDouble(body.get("lng")))
                .image((String) body.get("image"))
                .disponibilite((String) body.get("disponibilite"))
                .prestataire(prestataire)
                .build();

        return ServiceDto.from(serviceRepository.save(s));
    }

    public ServiceDto update(Long id, Map<String, Object> body) {
        ServiceEntity s = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service non trouvé : " + id));

        if (body.containsKey("titre"))         s.setTitre((String) body.get("titre"));
        if (body.containsKey("description"))   s.setDescription((String) body.get("description"));
        if (body.containsKey("categorie"))     s.setCategorie((String) body.get("categorie"));
        if (body.containsKey("prix"))          s.setPrix(toDouble(body.get("prix")));
        if (body.containsKey("unite"))         s.setUnite((String) body.get("unite"));
        if (body.containsKey("ville"))         s.setVille((String) body.get("ville"));
        if (body.containsKey("disponibilite")) s.setDisponibilite((String) body.get("disponibilite"));
        if (body.containsKey("image"))         s.setImage((String) body.get("image"));
        if (body.containsKey("lat"))           s.setLat(toDouble(body.get("lat")));
        if (body.containsKey("lng"))           s.setLng(toDouble(body.get("lng")));

        return ServiceDto.from(serviceRepository.save(s));
    }

    public void delete(Long id) {
        serviceRepository.deleteById(id);
    }

    private ServiceDto enrichWithStats(ServiceDto dto, ServiceEntity s) {
        var reviews = reviewRepository.findByPrestataire(s.getPrestataire());
        int nb = reviews.size();
        double avg = nb > 0 ? Math.round(reviews.stream()
                .mapToInt(r -> r.getRating()).average().orElse(0) * 10.0) / 10.0 : 0;
        dto.setNbAvis(nb);
        dto.setNoteMoyenne(avg);
        return dto;
    }

    private Double toDouble(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.doubleValue();
        return Double.parseDouble(o.toString());
    }
}
