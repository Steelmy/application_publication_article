package com.example.application_publication_article.repositories;

import com.example.application_publication_article.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    // Bonus : Vérifier si un email existe déjà (utile pour l'inscription)
    Boolean existsByEmail(String email);
}