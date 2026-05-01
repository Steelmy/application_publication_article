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
public class Categorie extends BaseEntity {

    @Id // Dit à Spring : "Ceci est la Clé Primaire"
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Laisse MySQL gérer l'Auto-Incrément (1, 2, 3...)
    private Long id;

    @Column(name = "nom_categorie", nullable = false, unique = true)
    private String nomCategorie;

}