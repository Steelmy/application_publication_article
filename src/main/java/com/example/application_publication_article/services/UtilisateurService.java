package com.example.application_publication_article.services;

import com.example.application_publication_article.entities.Role;
import com.example.application_publication_article.entities.TypeRole;
import com.example.application_publication_article.entities.Utilisateur;
import com.example.application_publication_article.repositories.RoleRepository;
import com.example.application_publication_article.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    // On injecte les deux Repositories dont on a besoin, ET notre encodeur de mots
    // de passe
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public Utilisateur inscrireUtilisateur(Utilisateur nouvelUtilisateur, TypeRole typeRole) {

        // Règle métier n°1 : Vérifier que l'email n'existe pas déjà
        if (utilisateurRepository.existsByEmail(nouvelUtilisateur.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé !");
        }

        // Règle métier n°2 : Récupérer le bon rôle en base de données (grâce à notre
        // Optional !)
        Role role = roleRepository.findByNomRole(typeRole)
                .orElseThrow(() -> new RuntimeException("Erreur : Le rôle spécifié n'existe pas."));

        // Règle métier n°3 : Hacher le mot de passe
        // (On part du principe que "getPasswordHash" contient temporairement le mot de
        // passe en clair tapé par l'utilisateur)
        String motDePasseSecurise = passwordEncoder.encode(nouvelUtilisateur.getPasswordHash());
        nouvelUtilisateur.setPasswordHash(motDePasseSecurise);

        // On assigne le rôle à l'utilisateur
        nouvelUtilisateur.setRole(role);

        // Enfin, on sauvegarde dans la base de données
        return utilisateurRepository.save(nouvelUtilisateur);
    }

    public Utilisateur authentifier(String email, String motDePasse) {

        // Message volontairement générique pour ne pas révéler si l'email existe
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect."));

        if (!passwordEncoder.matches(motDePasse, utilisateur.getPasswordHash())) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect.");
        }

        return utilisateur;
    }
}