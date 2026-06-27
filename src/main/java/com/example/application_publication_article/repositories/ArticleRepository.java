package com.example.application_publication_article.repositories;

import com.example.application_publication_article.entities.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByCategorieId(Long categorieId);

    List<Article> findByAuteurId(Long auteurId);

    List<Article> findByTitreContainingIgnoreCase(String motCle);

    @Query("SELECT a.categorie.id, COUNT(a) FROM Article a GROUP BY a.categorie.id")
    List<Object[]> countParCategorie();

    @Query(value = "SELECT a FROM Article a JOIN FETCH a.auteur JOIN FETCH a.categorie",
            countQuery = "SELECT COUNT(a) FROM Article a")
    Page<Article> findAllAvecAuteurEtCategorie(Pageable pageable);

    @Query(value = "SELECT a FROM Article a JOIN FETCH a.auteur JOIN FETCH a.categorie WHERE a.categorie.nomCategorie = :nom",
            countQuery = "SELECT COUNT(a) FROM Article a WHERE a.categorie.nomCategorie = :nom")
    Page<Article> findByCategorieNomAvecAuteurEtCategorie(@Param("nom") String nom, Pageable pageable);

    @Query("SELECT a FROM Article a JOIN FETCH a.auteur JOIN FETCH a.categorie LEFT JOIN FETCH a.likes WHERE a.id = :id")
    Optional<Article> findByIdAvecAuteurEtCategorie(@Param("id") Long id);

    @Query("SELECT a FROM Article a JOIN FETCH a.auteur JOIN FETCH a.categorie ORDER BY a.nombreDeVues DESC")
    List<Article> findArticlesLesPlusVus(Pageable pageable);

    @Query("SELECT a FROM Article a JOIN FETCH a.auteur JOIN FETCH a.categorie WHERE a.categorie.id = :categorieId AND a.id <> :articleId")
    List<Article> findSimilaires(@Param("articleId") Long articleId,
                                 @Param("categorieId") Long categorieId,
                                 Pageable pageable);

    @Query("SELECT a.id, COUNT(u) FROM Article a LEFT JOIN a.likes u WHERE a.id IN :ids GROUP BY a.id")
    List<Object[]> countLikesParArticleIds(@Param("ids") List<Long> ids);

    @Query("SELECT a.id FROM Article a JOIN a.likes u WHERE u.id = :utilisateurId AND a.id IN :ids")
    Set<Long> findArticleIdsLikedByUserAmong(@Param("utilisateurId") Long utilisateurId,
                                             @Param("ids") List<Long> ids);

    @Query(value = "SELECT a FROM Article a JOIN a.likes u JOIN FETCH a.auteur JOIN FETCH a.categorie WHERE u.id = :utilisateurId",
            countQuery = "SELECT COUNT(a) FROM Article a JOIN a.likes u WHERE u.id = :utilisateurId")
    Page<Article> findArticlesLikedByUser(@Param("utilisateurId") Long utilisateurId, Pageable pageable);
}
