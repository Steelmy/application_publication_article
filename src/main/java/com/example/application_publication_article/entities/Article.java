package com.example.application_publication_article.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(length = 500) // On autorise un résumé un peu plus long (500 caractères)
    private String resume;

    // --- LE POINT DE MAÎTRISE : LE CONTENU LONG ---
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "nombre_de_vues", columnDefinition = "integer default 0")
    private Integer nombreDeVues = 0;

    // --- LES DEUX CLÉS ÉTRANGÈRES ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id", nullable = false)
    private Utilisateur auteur;

    // --------------------------------

    // --- LA RELATION MANY-TO-MANY (LIKES) ---

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "article_likes", // Le nom exact de la table de jointure
            joinColumns = @JoinColumn(name = "article_id"), // La clé vers cette classe (Article)
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id") // La clé vers l'utilisateur qui a liké
    )
    private Set<Utilisateur> likes = new HashSet<>();

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