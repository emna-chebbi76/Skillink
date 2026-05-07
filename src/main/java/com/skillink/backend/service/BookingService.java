package com.skillink.backend.service;

import com.skillink.backend.dto.BookingDto;
import com.skillink.backend.entity.*;
import com.skillink.backend.exception.ResourceNotFoundException;
import com.skillink.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final NotificationRepository notificationRepository;

    public List<BookingDto> getAll() {
        return bookingRepository.findAll().stream().map(BookingDto::from).toList();
    }

    public List<BookingDto> getMine(String email) {
        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return bookingRepository.findByClient(client).stream().map(BookingDto::from).toList();
    }

    public List<BookingDto> getForProvider(String email) {
        User prestataire = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        List<ServiceEntity> myServices = serviceRepository.findByPrestataire(prestataire);
        if (myServices.isEmpty()) return List.of();
        return bookingRepository.findByServiceIn(myServices).stream().map(BookingDto::from).toList();
    }

    public BookingDto create(Map<String, Object> body, String email) {
        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Long serviceId = toLong(body.get("serviceId"));
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service non trouvé : " + serviceId));

        Booking booking = Booking.builder()
                .client(client)
                .service(service)
                .date(LocalDate.parse((String) body.get("date")))
                .time((String) body.getOrDefault("time", "10:00"))
                .nbPersonnes(toInt(body.getOrDefault("nbPersonnes", 1)))
                .total(toDouble(body.get("total")))
                .status("En attente")
                .build();

        Booking saved = bookingRepository.save(booking);

        // Notification au prestataire
        if (service.getPrestataire() != null) {
            notificationRepository.save(Notification.builder()
                    .userId(service.getPrestataire().getId())
                    .text("Nouvelle réservation pour \"" + service.getTitre() + "\" de la part de "
                            + client.getPrenom() + " " + client.getNom())
                    .type("info").build());
        }

        return BookingDto.from(saved);
    }

    public BookingDto update(Long id, Map<String, Object> body) {
        Booking b = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée : " + id));

        if (body.containsKey("status"))      b.setStatus((String) body.get("status"));
        if (body.containsKey("date"))        b.setDate(LocalDate.parse((String) body.get("date")));
        if (body.containsKey("time"))        b.setTime((String) body.get("time"));
        if (body.containsKey("nbPersonnes")) b.setNbPersonnes(toInt(body.get("nbPersonnes")));
        if (body.containsKey("total"))       b.setTotal(toDouble(body.get("total")));
        if (body.containsKey("hasReviewed")) b.setHasReviewed((Boolean) body.get("hasReviewed"));

        return BookingDto.from(bookingRepository.save(b));
    }

    public void cancel(Long id) {
        bookingRepository.deleteById(id);
    }

    private Long toLong(Object o) {
        if (o instanceof Number n) return n.longValue();
        return Long.parseLong(o.toString());
    }
    private Integer toInt(Object o) {
        if (o instanceof Number n) return n.intValue();
        return Integer.parseInt(o.toString());
    }
    private Double toDouble(Object o) {
        if (o instanceof Number n) return n.doubleValue();
        return Double.parseDouble(o.toString());
    }
}
