package com.example.application_publication_article.services;

import com.example.application_publication_article.entities.Categorie;
import com.example.application_publication_article.repositories.CategorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategorieService {

    private final CategorieRepository categorieRepository;

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
}