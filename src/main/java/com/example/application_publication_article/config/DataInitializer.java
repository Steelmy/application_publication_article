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
        seederArticlesSupplementaires();
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

    private void seederArticlesSupplementaires() {
        if (articleRepository.count() >= 26 || utilisateurRepository.count() == 0) {
            return;
        }

        Utilisateur auteur = utilisateurRepository.findAll().get(0);
        Map<String, Categorie> cats = categorieRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(Categorie::getNomCategorie, c -> c));

        creer(auteur, cats.get("Sciences"),
                "La matière noire existe-t-elle vraiment ?",
                "Depuis des décennies, elle expliquerait 85 % de la masse de l'univers. Mais plusieurs alternatives remettent ce paradigme en question.",
                "La matière noire est une hypothèse élégante : invisible, interagissant uniquement par la gravité, elle expliquerait des observations troublantes comme la rotation des galaxies. Mais aucune particule n'a jamais été détectée. Certaines théories modifiées de la gravité, comme MOND, rivalisent désormais sérieusement.");

        creer(auteur, cats.get("Sciences"),
                "CRISPR-Cas9 : une décennie de révolution génomique",
                "De la thérapie génique au riz plus résistant, l'outil d'édition du génome a transformé la biologie en dix ans.",
                "En 2012, Jennifer Doudna et Emmanuelle Charpentier publiaient le papier fondateur. Dix ans plus tard, CRISPR-Cas9 a déjà guéri des patients drépanocytaires, créé des souris résistantes au VIH, et ouvert des débats éthiques majeurs sur l'édition de l'embryon humain.");

        creer(auteur, cats.get("Sciences"),
                "Le microbiome intestinal, second cerveau ?",
                "Les recherches s'accumulent : nos bactéries intestinales influenceraient notre humeur, notre appétit, peut-être nos décisions.",
                "L'axe intestin-cerveau n'est plus une curiosité. Des études récentes montrent que certaines souches bactériennes produisent des neurotransmetteurs. La dépression, l'anxiété, l'autisme même sont désormais étudiés sous l'angle du microbiote.");

        creer(auteur, cats.get("Littérature"),
                "Relire Proust à l'ère du scroll infini",
                "La Recherche comme antidote à l'attention fragmentée : paradoxe d'une œuvre qui exige ce que notre époque nous a retiré.",
                "Lire Proust en 2026, c'est résister. Résister à l'injonction de la brièveté, aux notifications, à la dopamine du défilement. Ses phrases interminables deviennent un exercice de reconquête du temps long.");

        creer(auteur, cats.get("Littérature"),
                "La littérature jeunesse face à l'éco-anxiété",
                "De plus en plus de romans pour adolescents abordent frontalement la crise climatique. Comment raconter sans écraser ?",
                "Les éditeurs jeunesse le constatent : les lecteurs de 12-17 ans veulent des livres qui parlent du climat. Mais entre récit apocalyptique et utopie pédagogique, l'équilibre narratif reste à inventer.");

        creer(auteur, cats.get("Littérature"),
                "Haruki Murakami et le mystère de la persistance",
                "Pourquoi ses romans, traduits en cinquante langues, continuent-ils de fasciner deux générations ?",
                "Murakami écrit des histoires de chats qui parlent, de puits profonds, de solitudes urbaines. Son univers semble hors du temps — et c'est peut-être là sa force : offrir un refuge narratif que l'actualité ne pollue jamais.");

        creer(auteur, cats.get("Philosophie"),
                "Hannah Arendt et la banalité du mal, soixante ans après",
                "Le concept forgé lors du procès Eichmann reste-t-il opérant pour penser les génocides contemporains ?",
                "Jérusalem, 1961. Arendt observe Eichmann et décrit un homme ordinaire, incapable de penser. La banalité du mal n'est pas l'absence de mal — c'est l'absence de réflexion. Un diagnostic qui résonne jusqu'aux bureaucraties numériques d'aujourd'hui.");

        creer(auteur, cats.get("Philosophie"),
                "Spinoza, le philosophe que la neuroscience redécouvre",
                "Émotions, conatus, déterminisme : trois siècles plus tard, l'Éthique dialogue avec Damasio et les recherches contemporaines.",
                "Antonio Damasio l'a dit explicitement : Spinoza avait raison. Le corps précède l'esprit, les émotions structurent la raison, le désir de persévérer dans son être est le moteur fondamental. La philosophie du XVIIe siècle confirmée par l'IRM.");

        creer(auteur, cats.get("Philosophie"),
                "Faut-il encore enseigner la philosophie au lycée ?",
                "Enquête sur une discipline française fragilisée, entre réforme du bac et désintérêt supposé des élèves.",
                "L'épreuve de philosophie au bac cristallise des débats récurrents : élitisme, inadéquation au monde actuel, évaluation impossible. Pourtant les enseignants défendent farouchement un espace que rien ne remplace.");

        creer(auteur, cats.get("Histoire"),
                "La Commune de Paris, un laboratoire démocratique oublié",
                "Soixante-douze jours de 1871 qui ont inventé des formes politiques dont nous héritons sans le savoir.",
                "Mandat impératif, révocabilité des élus, écoles laïques, séparation des Églises et de l'État : la Commune a expérimenté en quelques semaines ce que la République mettra des décennies à institutionnaliser.");

        creer(auteur, cats.get("Histoire"),
                "1989 : ce qui s'est vraiment effondré",
                "Au-delà du mur de Berlin, une année charnière qui a redéfini le monde — et dont nous mesurons encore les conséquences.",
                "La chute du mur, Tian'anmen, Soljenitsyne autorisé à revenir, le Velvet Revolution à Prague : 1989 concentre une décennie d'événements. Mais l'histoire qu'on en raconte a longtemps occulté les trajectoires plus sombres.");

        creer(auteur, cats.get("Histoire"),
                "Les routes de la soie, mondialisation avant la lettre",
                "Pendant quinze siècles, un réseau d'échanges a relié la Chine à la Méditerranée. Relire cette histoire éclaire la nôtre.",
                "Avant Internet, avant même l'imprimerie, les routes de la soie convoyaient marchandises, religions, épidémies et idées. Étudier leur fonctionnement, c'est comprendre que la mondialisation n'est ni neuve, ni occidentale.");

        creer(auteur, cats.get("Société"),
                "Le retour des tiers-lieux dans la France rurale",
                "Anciennes gares, granges reconverties, cafés associatifs : une nouvelle géographie de la sociabilité émerge loin des métropoles.",
                "Fab labs, coworkings, médiathèques-cafés : partout en France rurale, des lieux hybrides recréent du lien. Financés par l'Europe, portés par des collectifs, ils répondent à un besoin que ni le marché ni l'État n'avaient su combler.");

        creer(auteur, cats.get("Société"),
                "Télétravail : la fin du bureau ou sa réinvention ?",
                "Cinq ans après la pandémie, les entreprises négocient un nouveau contrat spatial avec leurs salariés.",
                "Plus de la moitié des cadres français télétravaillent au moins un jour par semaine. Les bureaux se transforment, les trajets domicile-travail se reconfigurent, et la frontière vie privée/professionnelle se redessine — parfois violemment.");

        creer(auteur, cats.get("Société"),
                "Pourquoi les jeunes lisent-ils encore ?",
                "Contre toutes les prédictions, la lecture chez les 15-24 ans a progressé en 2025. Enquête sur les ressorts de ce sursaut.",
                "BookTok, clubs de lecture inversés, bibliothèques ouvertes jusqu'à minuit : les formes changent mais la lecture résiste. Les éditeurs s'adaptent, les librairies se réinventent, et une génération reprend possession du livre à sa manière.");

        creer(auteur, cats.get("Technologie"),
                "Les modèles de langage peuvent-ils vraiment raisonner ?",
                "Enquête sur les limites cognitives des LLM, entre hype, benchmarks truqués et percées réelles.",
                "Depuis GPT-4, chaque nouveau modèle clame qu'il raisonne. Mais qu'est-ce que raisonner ? Les chercheurs ne s'accordent ni sur la définition ni sur les tests. Pendant ce temps, les capacités réelles des LLM progressent, et nos frontières conceptuelles avec.");

        creer(auteur, cats.get("Technologie"),
                "L'informatique quantique sort-elle enfin du laboratoire ?",
                "IBM, Google, PsiQuantum : la course est lancée, mais la machine utile reste un horizon incertain.",
                "Cent qubits, mille qubits, dix mille qubits : la montée en échelle s'accélère. Mais la correction d'erreur reste le nerf de la guerre. Tant que les qubits logiques coûteront des milliers de qubits physiques, le quantique utile restera à portée de main — sans l'atteindre.");

        creer(auteur, cats.get("Technologie"),
                "Le web décentralisé, promesse oubliée ?",
                "Dix ans après l'engouement, où en sont les protocoles qui voulaient reprendre Internet aux géants ?",
                "ActivityPub, AT Protocol, Nostr : la galaxie du web décentralisé continue de croître, loin des projecteurs. Mastodon stagne, Bluesky surprend, les protocoles mûrissent. La décentralisation n'a pas gagné — elle a persisté.");

        creer(auteur, cats.get("Arts"),
                "L'art contemporain face au tournant post-numérique",
                "Quand les artistes digèrent l'IA générative : ni hype ni rejet, un nouveau vocabulaire émerge dans les galeries.",
                "Les expositions récentes à la FIAC, à Miami, à Art Basel montrent une intégration nuancée des outils d'IA. L'image générée n'est plus une nouveauté — c'est devenu un médium parmi d'autres, avec ses codes et ses clichés.");

        creer(auteur, cats.get("Arts"),
                "Pourquoi le cinéma européen reprend-il le risque ?",
                "De la Palme d'Or à la distribution en salle : une nouvelle génération de cinéastes refuse le compromis narratif.",
                "Les dernières éditions de Cannes et de la Berlinale l'ont confirmé : une vague de films exigeants, souvent longs, politiques, formalistes, trouve à nouveau son public. Les plateformes n'ont pas tué le cinéma d'auteur — elles l'ont peut-être même revitalisé.");

        System.out.println("✓ 20 articles supplémentaires créés");
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
