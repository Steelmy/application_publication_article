package com.example.application_publication_article.controllers;

import com.example.application_publication_article.entities.Categorie;
import com.example.application_publication_article.entities.TypeRole;
import com.example.application_publication_article.entities.Utilisateur;
import com.example.application_publication_article.services.CategorieService;
import com.example.application_publication_article.services.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final CategorieService categorieService;

    // --- LE DTO D'INSCRIPTION ---
    // Le moule exact du JSON que le formulaire d'inscription Angular va envoyer
    public record InscriptionDTO(String nom, String email, String motDePasse, TypeRole role) {
    }

    // --- LE DTO DE CONNEXION ---
    public record ConnexionDTO(String email, String motDePasse) {
    }

    @PostMapping("/inscription")
    public ResponseEntity<?> inscrire(@RequestBody InscriptionDTO dto) {
        try {
            Utilisateur nouvelUser = new Utilisateur();
            nouvelUser.setNom(dto.nom());
            nouvelUser.setEmail(dto.email());
            nouvelUser.setPasswordHash(dto.motDePasse());

            UtilisateurService.UtilisateurResponseDTO userCree = utilisateurService.inscrireUtilisateur(nouvelUser, dto.role());
            return ResponseEntity.status(HttpStatus.CREATED).body(userCree);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/connexion")
    public ResponseEntity<?> seConnecter(@RequestBody ConnexionDTO dto) {
        try {
            UtilisateurService.UtilisateurResponseDTO utilisateur = utilisateurService.authentifier(dto.email(), dto.motDePasse());
            return ResponseEntity.ok(utilisateur);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/categories-suivies")
    public ResponseEntity<?> getCategoriesSuivies(@PathVariable Long id) {
        try {
            List<Categorie> categories = categorieService.getCategoriesSuivies(id);
            return ResponseEntity.ok(categories);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}