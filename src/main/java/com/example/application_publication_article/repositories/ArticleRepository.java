package com.example.application_publication_article.repositories;

import com.example.application_publication_article.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Indique à Spring que ce composant gère les accès à la BDD
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // --- LA MAGIE DE SPRING DATA JPA ---

    // 1. Trouver tous les articles d'une catégorie spécifique
    List<Article> findByCategorieId(Long categorieId);

    // 2. Trouver tous les articles d'un auteur spécifique
    List<Article> findByAuteurId(Long auteurId);

    // 3. Chercher des articles dont le titre contient un mot-clé (barre de
    // recherche)
    List<Article> findByTitreContainingIgnoreCase(String motCle);

    // 4. Compter les articles groupés par catégorie (un seul SQL GROUP BY)
    @Query("SELECT a.categorie.id, COUNT(a) FROM Article a GROUP BY a.categorie.id")
    List<Object[]> countParCategorie();
}