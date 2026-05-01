package com.example.application_publication_article.controllers;

import com.example.application_publication_article.entities.Article;
import com.example.application_publication_article.services.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<Page<ArticleService.ArticleResponseDTO>> getAllArticles(
            @PageableDefault(size = 12, sort = {"createdAt", "id"}, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) Long utilisateurId) {
        return ResponseEntity.ok(articleService.getAllArticles(pageable, categorie, utilisateurId));
    }

    @GetMapping("/une")
    public ResponseEntity<ArticleService.ArticleResponseDTO> getArticleUne(
            @RequestParam(required = false) Long utilisateurId) {
        return articleService.getArticleUne(utilisateurId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}/similaires")
    public ResponseEntity<List<ArticleService.ArticleResponseDTO>> getSimilaires(
            @PathVariable Long id,
            @RequestParam(defaultValue = "3") int limit,
            @RequestParam(required = false) Long utilisateurId) {
        try {
            return ResponseEntity.ok(articleService.getSimilaires(id, limit, utilisateurId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleService.ArticleResponseDTO> getArticleById(
            @PathVariable Long id,
            @RequestParam(required = false) Long utilisateurId) {
        return articleService.getArticleById(id, utilisateurId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public record ArticleCreateDTO(String titre, String resume, String contenu, Long auteurId, Long categorieId) {
    }

    @PostMapping
    public ResponseEntity<ArticleService.ArticleResponseDTO> createArticle(@RequestBody ArticleCreateDTO dto) {
        Article nouvelArticle = new Article();
        nouvelArticle.setTitre(dto.titre());
        nouvelArticle.setResume(dto.resume());
        nouvelArticle.setContenu(dto.contenu());
        ArticleService.ArticleResponseDTO articleCree = articleService.creerArticle(nouvelArticle, dto.auteurId(), dto.categorieId());
        return ResponseEntity.status(HttpStatus.CREATED).body(articleCree);
    }

    // 4. DELETE : Supprimer un article
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    // utilisateurId en query param tant que l'auth Spring Security n'est pas branchée
    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id, @RequestParam Long utilisateurId) {
        try {
            ArticleService.LikeEtat etat = articleService.toggleLike(id, utilisateurId);
            return ResponseEntity.ok(etat);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}