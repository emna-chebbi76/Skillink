package com.skillink.backend.config;

import com.skillink.backend.entity.*;
import com.skillink.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Ajouter des catégories par défaut
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new Category(null, "Bricolage", "Petits travaux manuels", "fas fa-hammer"));
            categoryRepository.save(new Category(null, "Jardinage", "Entretien d'espaces verts", "fas fa-leaf"));
            categoryRepository.save(new Category(null, "Ménage", "Nettoyage et entretien", "fas fa-broom"));
            categoryRepository.save(new Category(null, "Informatique", "Dépannage et assistance", "fas fa-laptop"));
            categoryRepository.save(new Category(null, "Cours particuliers", "Soutien scolaire et langues", "fas fa-book"));
            System.out.println("✅ Catégories initiales créées");
        }

        // 2. Ajouter des utilisateurs (Admin, Prestataires, Clients)
        User admin = null;
        if (!userRepository.existsByEmail("admin@skillink.com")) {
            admin = userRepository.save(User.builder()
                    .nom("Admin")
                    .prenom("Super")
                    .email("admin@skillink.com")
                    .password(passwordEncoder.encode("admin123"))
                    .telephone("12345678")
                    .role(Role.ADMIN)
                    .build());
            System.out.println("✅ Administrateur créé : admin@skillink.com");
        }

        User prest1 = null;
        if (!userRepository.existsByEmail("prestataire@skillink.com")) {
            prest1 = userRepository.save(User.builder()
                    .nom("Bricoleur")
                    .prenom("Jean")
                    .email("prestataire@skillink.com")
                    .password(passwordEncoder.encode("prestataire123"))
                    .telephone("98765432")
                    .role(Role.PRESTATAIRE)
                    .build());
            System.out.println("✅ Prestataire 1 créé : prestataire@skillink.com");
        } else {
            prest1 = userRepository.findByEmail("prestataire@skillink.com").orElse(null);
        }

        User prest2 = null;
        if (!userRepository.existsByEmail("karim.info@skillink.com")) {
            prest2 = userRepository.save(User.builder()
                    .nom("Housni")
                    .prenom("Karim")
                    .email("karim.info@skillink.com")
                    .password(passwordEncoder.encode("karim123"))
                    .telephone("22334455")
                    .role(Role.PRESTATAIRE)
                    .build());
            System.out.println("✅ Prestataire 2 créé : karim.info@skillink.com");
        } else {
            prest2 = userRepository.findByEmail("karim.info@skillink.com").orElse(null);
        }

        User client1 = null;
        if (!userRepository.existsByEmail("client@skillink.com")) {
            client1 = userRepository.save(User.builder()
                    .nom("Dupont")
                    .prenom("Alice")
                    .email("client@skillink.com")
                    .password(passwordEncoder.encode("client123"))
                    .telephone("55443322")
                    .role(Role.CLIENT)
                    .build());
            System.out.println("✅ Client 1 créé : client@skillink.com");
        } else {
            client1 = userRepository.findByEmail("client@skillink.com").orElse(null);
        }

        User client2 = null;
        if (!userRepository.existsByEmail("mehdi.client@gmail.com")) {
            client2 = userRepository.save(User.builder()
                    .nom("Kader")
                    .prenom("Mehdi")
                    .email("mehdi.client@gmail.com")
                    .password(passwordEncoder.encode("mehdi123"))
                    .telephone("77889900")
                    .role(Role.CLIENT)
                    .build());
            System.out.println("✅ Client 2 créé : mehdi.client@gmail.com");
        } else {
            client2 = userRepository.findByEmail("mehdi.client@gmail.com").orElse(null);
        }

        // 3. Ajouter des services avec coordonnées pour la carte
        if (prest1 != null) {
            // Service 1 : Plomberie (Marsa)
            if (!serviceRepository.existsByTitre("Réparation Plomberie Express")) {
                serviceRepository.save(ServiceEntity.builder()
                        .titre("Réparation Plomberie Express")
                        .description("Réparation de fuites, débouchage et installation sanitaire rapide.")
                        .categorie("Bricolage")
                        .prix(45.0)
                        .unite("heure")
                        .ville("Tunis")
                        .quartier("Marsa")
                        .lat(36.8848)
                        .lng(10.3247)
                        .prestataire(prest1)
                        .disponibilite("semaine")
                        .image("https://images.unsplash.com/photo-1581244277943-fe4a9c777189")
                        .build());
            } else {
                serviceRepository.findByTitre("Réparation Plomberie Express").ifPresent(s -> {
                    if (s.getLat() == null) {
                        s.setLat(36.8848); s.setLng(10.3247);
                        serviceRepository.save(s);
                    }
                });
            }

            // Service 2 : Nettoyage (Lac 2)
            if (!serviceRepository.existsByTitre("Nettoyage Appartement")) {
                serviceRepository.save(ServiceEntity.builder()
                        .titre("Nettoyage Appartement")
                        .description("Nettoyage complet de votre domicile avec produits écologiques.")
                        .categorie("Ménage")
                        .prix(25.0)
                        .unite("heure")
                        .ville("Tunis")
                        .quartier("Lac 2")
                        .lat(36.8465)
                        .lng(10.2711)
                        .prestataire(prest1)
                        .disponibilite("maintenant")
                        .image("https://images.unsplash.com/photo-1581578731548-c64695cc6952")
                        .build());
            } else {
                serviceRepository.findByTitre("Nettoyage Appartement").ifPresent(s -> {
                    if (s.getLat() == null) {
                        s.setLat(36.8465); s.setLng(10.2711);
                        serviceRepository.save(s);
                    }
                });
            }
        }

        if (prest2 != null) {
            // Service 3 : Mathématiques (Ennasr)
            if (!serviceRepository.existsByTitre("Cours de Mathématiques")) {
                serviceRepository.save(ServiceEntity.builder()
                        .titre("Cours de Mathématiques")
                        .description("Soutien scolaire personnalisé pour lycéens et étudiants.")
                        .categorie("Cours particuliers")
                        .prix(30.0)
                        .unite("heure")
                        .ville("Ariana")
                        .quartier("Ennasr")
                        .lat(36.8584)
                        .lng(10.1587)
                        .prestataire(prest2)
                        .disponibilite("weekend")
                        .image("https://images.unsplash.com/photo-1635070041078-e363dbe005cb")
                        .build());
            } else {
                serviceRepository.findByTitre("Cours de Mathématiques").ifPresent(s -> {
                    if (s.getLat() == null) {
                        s.setLat(36.8584); s.setLng(10.1587);
                        serviceRepository.save(s);
                    }
                });
            }
        }


        System.out.println("✅ Vérification des services terminée");
    }
}

