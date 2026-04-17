# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Stack

Spring Boot 4.0.5 + Java 17, Gradle (Kotlin DSL), MySQL via JPA/Hibernate, Spring Security, Lombok. Single Gradle module, packagé `com.example.application_publication_article`.

## Commandes courantes

```bash
./gradlew bootRun                        # Lance l'application sur localhost:8080
./gradlew build                          # Compile + tests + packaging du jar
./gradlew test                           # Exécute toute la suite de tests JUnit 5
./gradlew test --tests "ClasseTest.methodeTest"  # Exécute un test précis
./gradlew bootJar                        # Construit le jar exécutable sans les tests
```

Avant `bootRun`, MySQL doit tourner sur `localhost:3306` avec une base `publication_article` créée (voir `database/init.sql`). Identifiants par défaut : user `root`, mot de passe vide (`src/main/resources/application.properties`).

## Architecture

Architecture classique en 4 couches Spring, **un fichier = une responsabilité** :

```
controllers/  ← @RestController, expose /api/*, parse les DTOs (records Java imbriqués)
services/     ← @Service, logique métier, lance IllegalArgumentException sur règle violée
repositories/ ← interfaces JpaRepository, requêtes dérivées du nom de méthode
entities/     ← @Entity JPA, callbacks @PrePersist/@PreUpdate pour les timestamps
config/       ← beans Spring (actuellement uniquement BCryptPasswordEncoder)
```

Règle de flux : un Controller n'appelle **jamais** un Repository directement, toujours via un Service. Les Services injectent plusieurs Repositories quand une opération touche plusieurs agrégats (voir `ArticleService.creerArticle` qui résout auteur + catégorie avant `save`).

DTOs : déclarés comme `record` publics imbriqués dans le Controller qui les consomme (ex. `ArticleController.ArticleCreateDTO`). Pas de package `dto/` dédié.

## Schéma de données — point d'attention

Hibernate est en `ddl-auto=update` : **c'est lui qui crée et fait évoluer les tables**, pas `database/init.sql`. Le fichier SQL existe pour documentation/bootstrap manuel mais diverge déjà de la réalité :

- `init.sql` définit `user` ; l'entité `Utilisateur` mappe vers `utilisateurs` (`@Table(name = "utilisateurs")`).
- `init.sql` insère les rôles `ADMIN`, `USER` ; `TypeRole` énumère `ADMIN`, `REDACTEUR`, `UTILISATEUR`.

Pour modifier le schéma, modifie l'entité JPA — ne touche pas `init.sql` en pensant que ça appliquera la migration. Si tu changes une enum référencée par `@Enumerated(EnumType.STRING)`, les anciennes valeurs en base resteront intactes.

## Sécurité

`SecurityConfig` ne définit **qu'un** `PasswordEncoder` (BCrypt) — aucune `SecurityFilterChain` n'est configurée. Avec Spring Security 6+ par défaut, cela signifie qu'une auth basique automatique peut s'activer ; vérifier avant d'ajouter des endpoints sensibles. Le hash du mot de passe se fait dans `UtilisateurService.inscrireUtilisateur`, pas dans le Controller.

## Conventions

- Identifiants, commentaires et messages d'erreur en **français** (`creerArticle`, `nomCategorie`, `"Cet email est déjà utilisé !"`). Conserver cette convention dans le nouveau code.
- Lombok partout : `@Getter @Setter @NoArgsConstructor` sur les entités, `@RequiredArgsConstructor` pour l'injection par constructeur (champs `private final`). Ne pas écrire de constructeurs/getters manuellement.
- Timestamps gérés par callbacks `@PrePersist` / `@PreUpdate` côté entité — ne pas les setter à la main dans les Services.
