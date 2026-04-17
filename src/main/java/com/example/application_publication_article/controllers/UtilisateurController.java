package com.example.application_publication_article.controllers;

import com.example.application_publication_article.entities.TypeRole;
import com.example.application_publication_article.entities.Utilisateur;
import com.example.application_publication_article.services.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    // --- LE DTO D'INSCRIPTION ---
    // Le moule exact du JSON que le formulaire d'inscription Angular va envoyer
    public record InscriptionDTO(String nom, String email, String motDePasse, TypeRole role) {
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
}