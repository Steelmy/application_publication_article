package com.example.application_publication_article.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Le point de maîtrise de l'Architecte ---
    @Enumerated(EnumType.STRING)
    @Column(name = "nom_role", nullable = false, unique = true)
    private TypeRole nomRole;
}