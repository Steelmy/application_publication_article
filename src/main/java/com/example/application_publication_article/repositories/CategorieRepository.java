package com.example.application_publication_article.repositories;

import com.example.application_publication_article.entities.Categorie;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    Optional<Categorie> findByNomCategorie(String nomCategorie);

    List<Categorie> findByCreatedAt(LocalDateTime createdAt);

    List<Categorie> findByUpdateAt(LocalDateTime updateAt);

}
