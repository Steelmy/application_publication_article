package com.example.application_publication_article.entities; // Vérifie que ce nom correspond bien au tien

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity // Dit à Spring : "Ceci est une table de base de données"
@Table(name = "categories") // Force le nom de la table au pluriel dans MySQL
@Getter // Magie Lombok : génère tous les "getters" (ex: getNomCategorie())
@Setter // Magie Lombok : génère tous les "setters"
@NoArgsConstructor // Magie Lombok : crée un constructeur vide obligatoire pour Hibernate
public class Categorie {

    @Id // Dit à Spring : "Ceci est la Clé Primaire"
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Laisse MySQL gérer l'Auto-Incrément (1, 2, 3...)
    private Long id;

    @Column(name = "nom_categorie", nullable = false, unique = true)
    private String nomCategorie;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    // --- L'astuce du Chef (Les Callbacks JPA) ---

    @PrePersist // Exécuté automatiquement juste AVANT la toute première sauvegarde en BDD
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    @PreUpdate // Exécuté automatiquement juste AVANT chaque modification
    protected void onUpdate() {
        this.updateAt = LocalDateTime.now();
    }
}