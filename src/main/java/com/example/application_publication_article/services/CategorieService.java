package com.example.application_publication_article.services;

import com.example.application_publication_article.entities.Categorie;
import com.example.application_publication_article.entities.Utilisateur;
import com.example.application_publication_article.repositories.ArticleRepository;
import com.example.application_publication_article.repositories.CategorieRepository;
import com.example.application_publication_article.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategorieService {

    private final CategorieRepository categorieRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ArticleRepository articleRepository;

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

    @Transactional
    public FollowEtat toggleFollow(Long categorieId, Long utilisateurId) {
        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new IllegalArgumentException("Erreur : Cette catégorie n'existe pas."));

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Erreur : Cet utilisateur n'existe pas."));

        boolean ajoute = utilisateur.getCategoriesSuivies().add(categorie);
        if (!ajoute) {
            utilisateur.getCategoriesSuivies().remove(categorie);
        }
        long nbAbonnes = utilisateurRepository.countAbonnesByCategorieId(categorieId);
        return new FollowEtat(nbAbonnes, ajoute);
    }

    @Transactional(readOnly = true)
    public List<Categorie> getCategoriesSuivies(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Erreur : Cet utilisateur n'existe pas."));
        return List.copyOf(utilisateur.getCategoriesSuivies());
    }

    @Transactional(readOnly = true)
    public List<CategorieAvecCompteur> getAllAvecCompteurs() {
        Map<Long, Long> compteurs = new HashMap<>();
        for (Object[] ligne : articleRepository.countParCategorie()) {
            compteurs.put((Long) ligne[0], (Long) ligne[1]);
        }
        return categorieRepository.findAll().stream()
                .map(c -> new CategorieAvecCompteur(
                        c.getId(),
                        c.getNomCategorie(),
                        compteurs.getOrDefault(c.getId(), 0L)))
                .toList();
    }

    public record FollowEtat(long nombreAbonnes, boolean suivieParUtilisateur) {
    }

    public record CategorieAvecCompteur(Long id, String nomCategorie, long nbArticles) {
    }
}