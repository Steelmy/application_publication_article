package com.example.application_publication_article.services;

import com.example.application_publication_article.entities.Article;
import com.example.application_publication_article.entities.Categorie;
import com.example.application_publication_article.entities.Utilisateur;
import com.example.application_publication_article.repositories.ArticleRepository;
import com.example.application_publication_article.repositories.CategorieRepository;
import com.example.application_publication_article.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    // On injecte les trois Repositories dont on a besoin
    private final ArticleRepository articleRepository;
    private final CategorieRepository categorieRepository;
    private final UtilisateurRepository utilisateurRepository;

    // 1. Lire tous les articles
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    // 2. Trouver un article par son ID
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    // 3. Créer un nouvel article (La logique métier complexe)
    public Article creerArticle(Article article, Long auteurId, Long categorieId) {

        // Règle n°1 : Trouver l'auteur. S'il n'existe pas, on bloque tout.
        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new IllegalArgumentException("Erreur : Cet auteur n'existe pas."));

        // Règle n°2 : Trouver la catégorie. Si elle n'existe pas, on bloque.
        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new IllegalArgumentException("Erreur : Cette catégorie n'existe pas."));

        // Règle n°3 : Associer l'auteur et la catégorie à l'article
        article.setAuteur(auteur);
        article.setCategorie(categorie);

        // L'article commence à 0 vue (même si on l'a mis en BDD, c'est bien de le
        // forcer ici aussi)
        article.setNombreDeVues(0);

        // Sauvegarde finale
        return articleRepository.save(article);
    }

    // 4. Supprimer un article
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
}