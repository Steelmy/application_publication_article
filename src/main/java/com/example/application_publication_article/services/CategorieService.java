package com.example.application_publication_article.services;

import com.example.application_publication_article.entities.Categorie;
import com.example.application_publication_article.entities.Utilisateur;
import com.example.application_publication_article.repositories.CategorieRepository;
import com.example.application_publication_article.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategorieService {

    private final CategorieRepository categorieRepository;
    private final UtilisateurRepository utilisateurRepository;

    // 1. Lire toutes les catégories
    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }

    // 2. Trouver une catégorie par son ID
    public Optional<Categorie> getCategorieById(Long id) {
        return categorieRepository.findById(id);
    }

    // 3. Créer ou mettre à jour une catégorie
    public Categorie saveCategorie(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    // 4. Supprimer une catégorie
    public void deleteCategorie(Long id) {
        categorieRepository.deleteById(id);
    }

    // 5. Toggle follow : ajoute la catégorie aux suivies si absente, la retire sinon.
    //    Retourne le nombre total d'abonnés et l'état de l'utilisateur.
    @Transactional
    public FollowEtat toggleFollow(Long categorieId, Long utilisateurId) {
        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new IllegalArgumentException("Erreur : Cette catégorie n'existe pas."));

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Erreur : Cet utilisateur n'existe pas."));

        boolean ajoute;
        if (utilisateur.getCategoriesSuivies().contains(categorie)) {
            utilisateur.getCategoriesSuivies().remove(categorie);
            ajoute = false;
        } else {
            utilisateur.getCategoriesSuivies().add(categorie);
            ajoute = true;
        }
        utilisateurRepository.save(utilisateur);
        long nbAbonnes = utilisateurRepository.countAbonnesByCategorieId(categorieId);
        return new FollowEtat(nbAbonnes, ajoute);
    }

    // 6. Catégories suivies par un utilisateur
    @Transactional(readOnly = true)
    public List<Categorie> getCategoriesSuivies(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Erreur : Cet utilisateur n'existe pas."));
        return List.copyOf(utilisateur.getCategoriesSuivies());
    }

    public record FollowEtat(long nombreAbonnes, boolean suivieParUtilisateur) {
    }
}