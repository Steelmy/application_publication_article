package com.example.application_publication_article.controllers;

import com.example.application_publication_article.entities.Article;
import com.example.application_publication_article.services.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    // 1. GET : Récupérer tous les articles
    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    // 2. GET : Récupérer un article précis
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        return articleService.getArticleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- LE FAMEUX DTO (Grâce aux Records de Java 17) ---
    // C'est le "moule" exact du JSON que ton Angular va envoyer
    public record ArticleCreateDTO(String titre, String resume, String contenu, Long auteurId, Long categorieId) {
    }

    // 3. POST : Créer un article
    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody ArticleCreateDTO dto) {

        // Étape A : On transforme le DTO en une vraie Entité Article (vide de relations
        // pour le moment)
        Article nouvelArticle = new Article();
        nouvelArticle.setTitre(dto.titre());
        nouvelArticle.setResume(dto.resume());
        nouvelArticle.setContenu(dto.contenu());

        // Étape B : On envoie tout au Service qui va se charger de trouver le bon
        // Auteur et la bonne Catégorie
        Article articleCree = articleService.creerArticle(nouvelArticle, dto.auteurId(), dto.categorieId());

        // Étape C : On renvoie l'article fraîchement créé avec un code 201
        return ResponseEntity.status(HttpStatus.CREATED).body(articleCree);
    }

    // 4. DELETE : Supprimer un article
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
}