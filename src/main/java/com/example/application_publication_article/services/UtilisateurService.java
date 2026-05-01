package com.example.application_publication_article.services;

import com.example.application_publication_article.entities.Role;
import com.example.application_publication_article.entities.TypeRole;
import com.example.application_publication_article.entities.Utilisateur;
import com.example.application_publication_article.repositories.RoleRepository;
import com.example.application_publication_article.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    // On injecte les deux Repositories dont on a besoin, ET notre encodeur de mots
    // de passe
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurResponseDTO inscrireUtilisateur(Utilisateur nouvelUtilisateur, TypeRole typeRole) {
        if (utilisateurRepository.existsByEmail(nouvelUtilisateur.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé !");
        }

        Role role = roleRepository.findByNomRole(typeRole)
                .orElseThrow(() -> new RuntimeException("Erreur : Le rôle spécifié n'existe pas."));

        String motDePasseSecurise = passwordEncoder.encode(nouvelUtilisateur.getPasswordHash());
        nouvelUtilisateur.setPasswordHash(motDePasseSecurise);
        nouvelUtilisateur.setRole(role);

        return mapper(utilisateurRepository.save(nouvelUtilisateur));
    }

    @Transactional(readOnly = true)
    public UtilisateurResponseDTO authentifier(String email, String motDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect."));

        if (!passwordEncoder.matches(motDePasse, utilisateur.getPasswordHash())) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect.");
        }

        return mapper(utilisateur);
    }

    private static UtilisateurResponseDTO mapper(Utilisateur u) {
        return new UtilisateurResponseDTO(
                u.getId(),
                u.getNom(),
                u.getEmail(),
                new RoleDTO(u.getRole().getId(), u.getRole().getNomRole().name()),
                u.getCreatedAt(),
                u.getUpdateAt()
        );
    }

    public record UtilisateurResponseDTO(
            Long id,
            String nom,
            String email,
            RoleDTO role,
            LocalDateTime createdAt,
            LocalDateTime updateAt
    ) {
    }

    public record RoleDTO(Long id, String nomRole) {
    }
}