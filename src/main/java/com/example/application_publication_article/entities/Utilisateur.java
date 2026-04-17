package com.example.application_publication_article.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "utilisateurs")
@Getter
@Setter
@NoArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    // L'email doit être unique pour servir d'identifiant de connexion
    @Column(nullable = false, unique = true)
    private String email;

    // On stockera le mot de passe haché plus tard, jamais en clair !
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // --- LA FAMEUSE CLÉ ÉTRANGÈRE ---

    @ManyToOne(fetch = FetchType.LAZY) // Plusieurs utilisateurs peuvent avoir le même rôle
    @JoinColumn(name = "role_id", nullable = false) // Nom de la colonne dans la BDD MySQL
    private Role role;

    // --------------------------------

    // --- LES RELATIONS MANY-TO-MANY ---

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "categories_suivies", // Le nom exact de la table de jointure dans MySQL
            joinColumns = @JoinColumn(name = "utilisateur_id"), // La clé qui pointe vers cette classe (Utilisateur)
            inverseJoinColumns = @JoinColumn(name = "categorie_id") // La clé qui pointe vers l'autre classe (Catégorie)
    )
    private Set<Categorie> categoriesSuivies = new HashSet<>();

    // --------------------------------

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateAt = LocalDateTime.now();
    }
}