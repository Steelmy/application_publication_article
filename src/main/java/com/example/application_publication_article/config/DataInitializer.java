package com.example.application_publication_article.config;

import com.example.application_publication_article.entities.Article;
import com.example.application_publication_article.entities.Categorie;
import com.example.application_publication_article.entities.Role;
import com.example.application_publication_article.entities.TypeRole;
import com.example.application_publication_article.entities.Utilisateur;
import com.example.application_publication_article.repositories.ArticleRepository;
import com.example.application_publication_article.repositories.CategorieRepository;
import com.example.application_publication_article.repositories.RoleRepository;
import com.example.application_publication_article.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final CategorieRepository categorieRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ArticleRepository articleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seederRoles();
        seederCategories();
        seederUserDemo();
        seederArticles();
    }

    private void seederRoles() {
        if (roleRepository.count() == 0) {
            for (TypeRole type : TypeRole.values()) {
                Role role = new Role();
                role.setNomRole(type);
                roleRepository.save(role);
            }
            System.out.println("✓ Rôles initialisés : ADMIN, REDACTEUR, UTILISATEUR");
        }
    }

    private void seederCategories() {
        if (categorieRepository.count() == 0) {
            for (String nom : List.of("Sciences", "Littérature", "Philosophie", "Histoire", "Société", "Technologie", "Arts")) {
                Categorie c = new Categorie();
                c.setNomCategorie(nom);
                categorieRepository.save(c);
            }
            System.out.println("✓ 7 catégories de démo créées");
        }
    }

    private void seederUserDemo() {
        // On cible l'email pour que le compte démo soit créé même si d'autres
        // utilisateurs existent déjà (tests d'inscription, etc.).
        if (!utilisateurRepository.existsByEmail("redacteur@inkwell.fr")) {
            Role redacteur = roleRepository.findByNomRole(TypeRole.REDACTEUR)
                    .orElseThrow(() -> new RuntimeException("Rôle REDACTEUR manquant"));
            Utilisateur demo = new Utilisateur();
            demo.setNom("Camille Aubert");
            demo.setEmail("redacteur@inkwell.fr");
            demo.setPasswordHash(passwordEncoder.encode("demo1234"));
            demo.setRole(redacteur);
            utilisateurRepository.save(demo);
            System.out.println("✓ Compte démo créé : redacteur@inkwell.fr / demo1234");
        }
    }

    private void seederArticles() {
        if (articleRepository.count() > 0 || utilisateurRepository.count() == 0) {
            return;
        }

        Utilisateur auteur = utilisateurRepository.findAll().get(0);

        Map<String, Categorie> cats = categorieRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(Categorie::getNomCategorie, c -> c));

        creer(auteur, cats.get("Sciences"),
                "Le cerveau bayésien : pourquoi nous prédisons plus que nous ne percevons",
                "La perception serait moins une caméra qu'un pari. Karl Friston et le principe d'énergie libre ont bouleversé notre compréhension de la conscience.",
                "Imaginez un instant que votre cerveau ne perçoive pas le monde, mais qu'il le devine. Que ce que vous tenez pour la réalité immédiate soit une hypothèse en perpétuelle révision.\n\nL'idée est d'une élégance vertigineuse. Le cerveau ne serait pas un organe passif, recevant des données brutes. Il serait un moteur prédictif, formulant en permanence des hypothèses sur ce qui devrait arriver.\n\nLe nom qui revient sans cesse dans cette histoire est celui de Karl Friston, neuroscientifique britannique du University College de Londres. Auteur le plus cité au monde dans son domaine, il a formalisé en 2010 ce qu'il appelle le principe d'énergie libre.");

        creer(auteur, cats.get("Sciences"),
                "Les océans absorbent-ils encore notre carbone ?",
                "Une étude récente du CNRS révèle un essoufflement inattendu de la pompe biologique dans l'Atlantique nord.",
                "Pendant des décennies, l'océan a joué le rôle de tampon climatique. Une nouvelle étude publiée dans Nature suggère que ce mécanisme pourrait s'essouffler plus vite qu'anticipé.\n\nLes équipes du CNRS ont analysé vingt ans de données satellite et de mesures in situ. Le constat est sans appel : la pompe biologique ralentit dans plusieurs zones critiques.");

        creer(auteur, cats.get("Littérature"),
                "Annie Ernaux et la mémoire comme matière première",
                "Relire l'œuvre de la prix Nobel à l'aune de ses entretiens récents : une autobiographie collective en construction.",
                "Annie Ernaux n'écrit pas des souvenirs. Elle fabrique, ouvrage après ouvrage, une mémoire commune.\n\nDe La Place aux Années, son projet n'a jamais varié : extraire de l'intime les fils d'une histoire collective. Une démarche que ses entretiens récents éclairent d'une lumière nouvelle.");

        creer(auteur, cats.get("Histoire"),
                "1973 : l'année où le pétrole a redessiné le monde",
                "Cinquante ans après, retour sur le choc qui inventa l'économie mondialisée et l'écologie politique.",
                "Octobre 1973. En quelques semaines, le prix du baril quadruple. Le monde occidental découvre sa dépendance.\n\nDe cette crise naissent simultanément la mondialisation telle qu'on la connaît, les premières politiques d'efficacité énergétique, et un mouvement écologiste qui bascule du conservationnisme à la critique systémique.");

        creer(auteur, cats.get("Technologie"),
                "Ce que l'IA générative change vraiment au métier d'éditeur",
                "Enquête en six maisons d'édition françaises : entre fascination, panique morale et nouveaux ateliers d'écriture assistée.",
                "Dans les couloirs des maisons d'édition parisiennes, le sujet est partout. Et nulle part vraiment.\n\nNotre enquête a recueilli la parole de directeurs littéraires, d'éditeurs jeunesse, de correcteurs. Ce qui en ressort déjoue les récits dominants.");

        creer(auteur, cats.get("Philosophie"),
                "Bergson, ou le temps qui résiste à la mesure",
                "Pourquoi le philosophe de la durée reste-t-il aussi actuel face aux promesses de quantification du vivant ?",
                "À l'heure où chaque seconde de notre attention est mesurée, le concept bergsonien de durée retrouve une actualité brûlante.\n\nDurée vécue contre temps mesuré : Bergson nous invite à une distinction qui résiste à toutes les métriques contemporaines.");

        System.out.println("✓ 6 articles de démo créés");
    }

    private void creer(Utilisateur auteur, Categorie categorie, String titre, String resume, String contenu) {
        Article a = new Article();
        a.setTitre(titre);
        a.setResume(resume);
        a.setContenu(contenu);
        a.setAuteur(auteur);
        a.setCategorie(categorie);
        a.setNombreDeVues(ThreadLocalRandom.current().nextInt(500, 50_000));
        articleRepository.save(a);
    }
}
