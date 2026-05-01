package com.example.application_publication_article.services;

import com.example.application_publication_article.entities.Article;
import com.example.application_publication_article.entities.Categorie;
import com.example.application_publication_article.entities.Utilisateur;
import com.example.application_publication_article.repositories.ArticleRepository;
import com.example.application_publication_article.repositories.CategorieRepository;
import com.example.application_publication_article.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CategorieRepository categorieRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Transactional(readOnly = true)
    public Page<ArticleResponseDTO> getAllArticles(Pageable pageable, String categorieNom, Long utilisateurIdCourant) {
        Page<Article> page = (categorieNom == null || categorieNom.isBlank())
                ? articleRepository.findAllAvecAuteurEtCategorie(pageable)
                : articleRepository.findByCategorieNomAvecAuteurEtCategorie(categorieNom, pageable);
        return enrichirPage(page, utilisateurIdCourant);
    }

    @Transactional(readOnly = true)
    public Optional<ArticleResponseDTO> getArticleById(Long id, Long utilisateurIdCourant) {
        return articleRepository.findByIdAvecAuteurEtCategorie(id)
                .map(a -> {
                    long nbLikes = a.getLikes().size();
                    boolean aime = utilisateurIdCourant != null
                            && a.getLikes().stream().anyMatch(u -> u.getId().equals(utilisateurIdCourant));
                    return mapper(a, nbLikes, aime);
                });
    }

    @Transactional(readOnly = true)
    public Optional<ArticleResponseDTO> getArticleUne(Long utilisateurIdCourant) {
        List<Article> top = articleRepository.findArticlesLesPlusVus(PageRequest.of(0, 1));
        if (top.isEmpty()) return Optional.empty();
        return Optional.of(enrichirListe(top, utilisateurIdCourant).get(0));
    }

    @Transactional(readOnly = true)
    public List<ArticleResponseDTO> getSimilaires(Long articleId, int limit, Long utilisateurIdCourant) {
        Article article = articleRepository.findByIdAvecAuteurEtCategorie(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Erreur : Cet article n'existe pas."));
        List<Article> similaires = articleRepository.findSimilaires(
                articleId, article.getCategorie().getId(), PageRequest.of(0, limit));
        return enrichirListe(similaires, utilisateurIdCourant);
    }

    public ArticleResponseDTO creerArticle(Article article, Long auteurId, Long categorieId) {
        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new IllegalArgumentException("Erreur : Cet auteur n'existe pas."));
        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new IllegalArgumentException("Erreur : Cette catégorie n'existe pas."));
        article.setAuteur(auteur);
        article.setCategorie(categorie);
        article.setNombreDeVues(0);
        Article cree = articleRepository.save(article);
        return mapper(cree, 0L, false);
    }

    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Transactional
    public LikeEtat toggleLike(Long articleId, Long utilisateurId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Erreur : Cet article n'existe pas."));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Erreur : Cet utilisateur n'existe pas."));

        boolean ajoute = article.getLikes().add(utilisateur);
        if (!ajoute) {
            article.getLikes().remove(utilisateur);
        }
        return new LikeEtat(article.getLikes().size(), ajoute);
    }

    private Page<ArticleResponseDTO> enrichirPage(Page<Article> page, Long utilisateurIdCourant) {
        List<ArticleResponseDTO> enrichis = enrichirListe(page.getContent(), utilisateurIdCourant);
        return new org.springframework.data.domain.PageImpl<>(enrichis, page.getPageable(), page.getTotalElements());
    }

    private List<ArticleResponseDTO> enrichirListe(List<Article> articles, Long utilisateurIdCourant) {
        if (articles.isEmpty()) return List.of();
        List<Long> ids = articles.stream().map(Article::getId).toList();

        Map<Long, Long> compteurs = new HashMap<>();
        for (Object[] ligne : articleRepository.countLikesParArticleIds(ids)) {
            compteurs.put((Long) ligne[0], (Long) ligne[1]);
        }
        Set<Long> idsLikes = utilisateurIdCourant == null
                ? Set.of()
                : articleRepository.findArticleIdsLikedByUserAmong(utilisateurIdCourant, ids);

        return articles.stream()
                .map(a -> mapper(a, compteurs.getOrDefault(a.getId(), 0L), idsLikes.contains(a.getId())))
                .toList();
    }

    private static ArticleResponseDTO mapper(Article a, long nbLikes, boolean aimeParUtilisateurCourant) {
        return new ArticleResponseDTO(
                a.getId(),
                a.getTitre(),
                a.getResume(),
                a.getContenu(),
                a.getNombreDeVues(),
                nbLikes,
                aimeParUtilisateurCourant,
                a.getCreatedAt(),
                a.getUpdateAt(),
                new AuteurResumeDTO(a.getAuteur().getId(), a.getAuteur().getNom()),
                new CategorieResumeDTO(a.getCategorie().getId(), a.getCategorie().getNomCategorie())
        );
    }

    public record LikeEtat(int nombreLikes, boolean likeParUtilisateur) {
    }

    public record ArticleResponseDTO(
            Long id,
            String titre,
            String resume,
            String contenu,
            Integer nombreDeVues,
            long nbLikes,
            boolean aimeParUtilisateurCourant,
            LocalDateTime createdAt,
            LocalDateTime updateAt,
            AuteurResumeDTO auteur,
            CategorieResumeDTO categorie
    ) {
    }

    public record AuteurResumeDTO(Long id, String nom) {
    }

    public record CategorieResumeDTO(Long id, String nomCategorie) {
    }
}
