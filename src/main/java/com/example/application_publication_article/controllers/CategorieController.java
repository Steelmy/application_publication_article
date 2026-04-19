package com.example.application_publication_article.controllers;

import com.example.application_publication_article.entities.Categorie;
import com.example.application_publication_article.services.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indique que c'est un point d'entrée API (renvoie du JSON)
@RequestMapping("/api/categories") // L'URL de base pour toutes les méthodes ici
@RequiredArgsConstructor
public class CategorieController {

    // On injecte le Service (Le Controller ne parle JAMAIS au Repository
    // directement)
    private final CategorieService categorieService;

    // 1. GET : Récupérer toutes les catégories
    // URL : http://localhost:8080/api/categories
    @GetMapping
    public ResponseEntity<List<Categorie>> getAllCategories() {
        List<Categorie> categories = categorieService.getAllCategories();
        return ResponseEntity.ok(categories); // Renvoie un statut HTTP 200 (OK)
    }

    // 2. GET : Récupérer une seule catégorie
    // URL : http://localhost:8080/api/categories/1
    @GetMapping("/{id}")
    public ResponseEntity<Categorie> getCategorieById(@PathVariable Long id) {
        return categorieService.getCategorieById(id)
                .map(ResponseEntity::ok) // Si trouvé, renvoie 200 OK
                .orElse(ResponseEntity.notFound().build()); // Si absent, renvoie 404 Not Found
    }

    // 3. POST : Créer une nouvelle catégorie
    // URL : http://localhost:8080/api/categories
    @PostMapping
    public ResponseEntity<Categorie> createCategorie(@RequestBody Categorie categorie) {
        Categorie nouvelleCategorie = categorieService.saveCategorie(categorie);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleCategorie); // Renvoie 201 Created
    }

    // 4. DELETE : Supprimer une catégorie
    // URL : http://localhost:8080/api/categories/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        categorieService.deleteCategorie(id);
        return ResponseEntity.noContent().build(); // Renvoie 204 No Content
    }

    // 5. POST : Toggle follow sur une catégorie
    @PostMapping("/{id}/follow")
    public ResponseEntity<?> toggleFollow(@PathVariable Long id, @RequestParam Long utilisateurId) {
        try {
            CategorieService.FollowEtat etat = categorieService.toggleFollow(id, utilisateurId);
            return ResponseEntity.ok(etat);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}