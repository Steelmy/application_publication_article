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

    // POST : Inscrire un nouvel utilisateur
    // URL : http://localhost:8080/api/utilisateurs/inscription
    @PostMapping("/inscription")
    public ResponseEntity<?> inscrire(@RequestBody InscriptionDTO dto) {

        try {
            // 1. On prépare l'entité (le mot de passe est encore en clair ici)
            Utilisateur nouvelUser = new Utilisateur();
            nouvelUser.setNom(dto.nom());
            nouvelUser.setEmail(dto.email());
            nouvelUser.setPasswordHash(dto.motDePasse());

            // 2. On l'envoie au Service (qui va le hacher et vérifier l'email)
            Utilisateur userCree = utilisateurService.inscrireUtilisateur(nouvelUser, dto.role());

            // 3. Succès : On renvoie un code 201 (Created)
            return ResponseEntity.status(HttpStatus.CREATED).body(userCree);

        } catch (IllegalArgumentException e) {
            // 4. Échec : Si le Service lève une erreur (ex: email déjà pris),
            // on la capture et on renvoie une erreur 400 (Bad Request) propre au navigateur
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            // Pour les autres erreurs (ex: le rôle n'existe pas)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // POST : Connecter un utilisateur existant
    // URL : http://localhost:8080/api/utilisateurs/connexion
    @PostMapping("/connexion")
    public ResponseEntity<?> seConnecter(@RequestBody ConnexionDTO dto) {

        try {
            Utilisateur utilisateur = utilisateurService.authentifier(dto.email(), dto.motDePasse());
            return ResponseEntity.ok(utilisateur);

        } catch (IllegalArgumentException e) {
            // Identifiants invalides : on renvoie 401 (Unauthorized)
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