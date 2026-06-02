# GameVault – Guide de travail par rôle

> Ce document est **la référence principale de l'équipe**.  
> Chaque membre doit le lire entièrement, puis se concentrer sur sa section.  
> Tout le code se pousse sur une branche `feature/xxx` → PR vers `dev` → review → merge.

---

## Contexte du projet

L'association **RetroSphere** gère une collection de jeux vidéo (rétro et modernes) sur de nombreuses plateformes : PC, PlayStation, Xbox, Nintendo Switch, Sega Saturn, Dreamcast, Super Nintendo, etc.  
Aujourd'hui tout est dans des feuilles de calcul dispersées. L'objectif est de livrer un **logiciel desktop** Java moderne qui centralise tout.

**Ce qui est évalué :**
- Qualité de l'expérience utilisateur (UX compte autant que le code)
- Robustesse (aucun crash sur erreur de saisie ou fichier manquant)
- Documentation (README clair, Figma, justification UX)
- Facilité d'installation et de déploiement
- Capacité de l'équipe à expliquer ses choix techniques

---

## Stack technique imposée

| Technologie | Usage |
|-------------|-------|
| Java 21 | Langage principal |
| JavaFX 21 | Interface graphique |
| FXML | Déclaration des vues (séparées des contrôleurs) |
| Hibernate 6 | ORM pour la persistance |
| SQLite | Base de données locale |
| Maven | Gestion du projet et des dépendances |

---

## Architecture du projet

```
src/
├── main/
│   ├── java/com/gamevault/
│   │   ├── MainApp.java              ← point d'entrée (Mateo)
│   │   ├── model/                    ← entités métier (Marc)
│   │   │   ├── Game.java
│   │   │   └── GameStatus.java
│   │   ├── repository/               ← accès base de données (Marc)
│   │   │   └── GameRepository.java
│   │   ├── service/                  ← logique métier (Marc)
│   │   │   └── GameService.java
│   │   ├── controller/               ← contrôleurs JavaFX (Hiba)
│   │   │   ├── MainController.java
│   │   │   └── GameFormController.java
│   │   ├── config/                   ← Hibernate (Mateo)
│   │   │   └── HibernateUtil.java
│   │   └── util/                     ← utilitaires (Mateo)
│   │       └── AppConfig.java
│   └── resources/
│       ├── fxml/                     ← vues (Hiba)
│       │   ├── MainView.fxml
│       │   └── GameFormView.fxml
│       ├── css/                      ← styles (Hiba)
│       │   └── style.css
│       ├── hibernate.cfg.xml         ← config Hibernate (Mateo)
│       └── application.properties   ← paramètres (Mateo)
└── test/
    └── java/com/gamevault/           ← tests unitaires (Marc)
docs/
├── TASKS.md                          ← ce fichier
└── ux-justification.md              ← document UX/UI (Yanina)
```

---

## Workflow Git

```
main          ← code livrable final, protégé
  └── dev     ← branche d'intégration, on merge les features ici
        ├── feature/backend-model-service    (Marc)
        ├── feature/frontend-ui              (Hiba)
        └── feature/devops-config            (Mateo)
```

**Toujours partir de `dev` pour créer sa branche :**
```bash
git checkout dev
git pull origin dev
git checkout -b feature/ma-branche
```

**Pousser et ouvrir une PR :**
```bash
git push origin feature/ma-branche
# → ouvrir une Pull Request vers dev sur GitHub
# → un autre membre de l'équipe relit et approuve
```

---

---

# MATEO — Rôle 1 : Chef de projet / DevOps

## Branche
```bash
git checkout -b feature/devops-config
```

## Fichiers à compléter

### `pom.xml` ✅ déjà fait
Vérifier que tout compile : `mvn clean compile`

### `src/main/resources/hibernate.cfg.xml` ✅ déjà fait
- Connexion SQLite configurée
- Mapping de l'entité `Game` déclaré
- `hbm2ddl.auto=update` → la table est créée automatiquement au premier lancement

### `src/main/resources/application.properties` ✅ déjà fait
Paramètres externalisés (chemin DB, mode SQL, version app).  
Si l'environnement change (ex: chemin différent), seul ce fichier est à modifier.

### `README.md` — À compléter
Le README est **un critère d'évaluation**. Il doit permettre à quelqu'un qui ne connaît pas le projet de l'installer et le lancer en moins de 5 minutes.

Contenu attendu :
```markdown
# GameVault

## Prérequis
- Java 21+
- Maven 3.8+

## Installation
git clone ...
cd gamevault
mvn clean compile

## Lancer l'application
mvn javafx:run

## Lancer les tests
mvn test

## Structure du projet
(copier depuis ce fichier TASKS.md)

## Équipe
...

## Lien Figma
(à ajouter quand Yanina l'envoie)
```

### Produire un JAR exécutable (bonus valorisé)
Ajouter le plugin `maven-shade-plugin` dans `pom.xml` pour générer un fat JAR :
```bash
mvn package
java -jar target/gamevault-1.0.0-fat.jar
```

### Docker Compose (bonus)
Créer un `docker-compose.yml` + `Dockerfile` pour que n'importe qui puisse lancer le projet sans installer Java.

### Reviews de PRs
- Quand Marc ou Hiba poussent une PR, c'est toi qui reviews et merges vers `dev`
- Vérifier : le code compile, pas de TODO oublié, pas de fichier `.db` ou `.log` commité

---

---

# MARC — Rôle 2 : Backend / Base de données

## Branche
```bash
git checkout dev && git pull origin dev
git checkout -b feature/backend-model-service
```

## Fichiers à implémenter

### `model/Game.java`
Ajouter tous les getters et setters pour :
- `id`, `title`, `developer`, `publisher`, `releaseYear`, `platform`
- `description`, `rating` (0.0 – 10.0), `status`, `coverPath`, `addedAt`

### `model/GameStatus.java`
Compléter l'enum avec toutes les valeurs :
```java
IN_COLLECTION("Collection"),
TO_PLAY("À faire"),
IN_PROGRESS("En cours"),
COMPLETED("Terminé"),
ABANDONED("Abandonné");
```

### `repository/GameRepository.java`
Implémenter avec Hibernate (`HibernateUtil.getSessionFactory()`) :

```java
// save() : persist si id==null, merge sinon
// delete() : récupérer l'entité managée et la supprimer
// findById() : session.get(Game.class, id)
// findAll() : HQL "FROM Game ORDER BY title"
// search() : HQL avec LOWER(title) LIKE :q OR LOWER(platform) LIKE :q OR LOWER(developer) LIKE :q
```

**Important :** toujours ouvrir/fermer la Session dans un try-with-resources.  
Toujours faire `tx.rollback()` dans le catch en cas d'erreur.

### `service/GameService.java`
Implémenter la logique métier :

**Validation (méthode `validate`) :**
- `title` ne doit pas être null ou vide → `IllegalArgumentException("Le titre est obligatoire.")`
- `platform` ne doit pas être null ou vide → `IllegalArgumentException("La plateforme est obligatoire.")`
- `releaseYear` si renseigné doit être entre 1950 et 2100
- `rating` si renseignée doit être entre 0.0 et 10.0

**Filtres (`filter`) :**
```java
// filtrer la liste par plateforme (si non null) ET par statut (si non null)
// utiliser stream().filter().collect()
```

**Tri (`sort`) :**
```java
// selon field : "title", "platform", "releaseYear", "rating", "status"
// ascending true/false
// utiliser Comparator avec nullsLast pour les champs optionnels
```

### Tests unitaires
Créer `src/test/java/com/gamevault/GameServiceTest.java` :
- Test ajout d'un jeu valide
- Test ajout sans titre → doit lever `IllegalArgumentException`
- Test ajout sans plateforme → doit lever `IllegalArgumentException`
- Test avec note invalide (> 10) → doit lever `IllegalArgumentException`
- Test filtre par plateforme
- Test tri par année

### Robustesse — points clés évalués
- Si la base de données n'existe pas → Hibernate la crée automatiquement (`hbm2ddl.auto=update`)
- Si `findAll()` échoue → retourner une liste vide, logguer l'erreur, ne jamais crasher
- Toutes les exceptions doivent être catchées et loggées avec SLF4J

---

---

# HIBA — Rôle 3 : Frontend / Interface

## Branche
```bash
git checkout dev && git pull origin dev
git checkout -b feature/frontend-ui
```

> Attendre que Marc ait poussé le modèle `Game` et `GameService` avant de brancher dessus.  
> En attendant, tu peux construire les FXML et le CSS de manière autonome.

## Fichiers à implémenter

### `fxml/MainView.fxml`
Vue principale de l'application. Structure recommandée (voir maquette Yanina) :

```
BorderPane
├── left  : VBox sidebar
│           - Logo "GAMEVAULT"
│           - Boutons navigation : Collection, + Ajouter, Statistiques, Paramètres
│           - En bas : nom/version
├── center: VBox contenu
│           - Label titre "Ma collection"
│           - HBox toolbar : TextField recherche + ComboBox plateforme + ComboBox statut + boutons Modifier/Supprimer
│           - TableView (colonnes : Titre, Plateforme, Année, Note, Statut)
│           - Label statusBar (ex: "6 jeux affichés")
└── right : VBox panneau détail (optionnel mais valorisé)
            - ImageView jaquette
            - Labels infos du jeu sélectionné
            - Boutons Modifier / Supprimer
```

Tous les `fx:id` doivent correspondre exactement aux champs déclarés dans `MainController`.

### `fxml/GameFormView.fxml`
Formulaire d'ajout / modification :
- `TextField` : titre*, développeur, éditeur, année
- `ComboBox` plateforme (editable=true pour permettre une valeur custom)
- `TextArea` description
- `Slider` note 0-10 + `Label` qui affiche la valeur en temps réel
- `ComboBox` statut
- `ImageView` jaquette (160×220) + `Button` "Choisir une image"
- `Label` erreur (rouge, `visible="false"` par défaut)
- `Button` Annuler + `Button` Enregistrer

### `controller/MainController.java`
Implémenter :
```java
initialize() // → bind les colonnes, init les filtres, charger la liste
onSearch()   // → gameService.search() + filter() → displayedGames.setAll()
onAddGame()  // → FXMLLoader GameFormView, modal, setOnSaved(this::refresh)
onEditGame() // → idem avec le jeu sélectionné
onDeleteGame()// → Alert CONFIRMATION, puis gameService.deleteGame()
```

Binding des colonnes TableView :
```java
colTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));
// idem pour platform, year, rating, status
```

### `controller/GameFormController.java`
Implémenter :
```java
initialize()    // → remplir ComboBox plateforme et statut, listener sur ratingSlider
setGame()       // → pré-remplir les champs si modification
onChooseCover() // → FileChooser images (png, jpg, jpeg), afficher en prévisualisation
onSave()        // → lire les champs, appeler addGame ou updateGame, afficher errorLabel si exception
onCancel()      // → fermer la fenêtre
```

### `css/style.css`
Thème sombre recommandé (cohérent avec la maquette Yanina).  
Points importants :
- `.sidebar` : fond légèrement différent du contenu
- `.btn-primary` : couleur accentuée (violet ou bleu)
- `.btn-danger` : rouge pour le bouton Supprimer
- `.game-table` : alternance de couleurs sur les lignes
- Statuts colorés : Terminé = vert, En cours = orange/jaune, À faire = bleu, Abandonné = gris

### Robustesse — points clés évalués
- Si l'utilisateur clique "Modifier" sans sélectionner de jeu → ne rien faire (ou afficher un message)
- Si l'image choisie est invalide → afficher un message d'erreur, ne pas crasher
- Tous les champs obligatoires marqués visuellement (astérisque dans le label)
- Le formulaire affiche le message d'erreur retourné par `GameService.validate()`

---

---

# YANINA — Rôle 4 : UX/UI / Documentation

## Pas de branche de code pour commencer
Ton travail principal est sur **Figma** (outil de maquettage gratuit en ligne : figma.com).

---

## Maquette Figma — écrans à produire

### Écran 1 : Vue principale (liste de la collection)
- Sidebar gauche avec navigation
- Barre de recherche + filtres (plateforme, statut) + tri
- Liste des jeux en tableau (Titre, Plateforme, Année, Note, Statut)
- Panneau détail à droite au clic sur un jeu (jaquette + infos complètes)
- Barre de statut en bas ("X jeux affichés")

### Écran 2 : Formulaire ajout / modification
- Tous les champs : titre, développeur, éditeur, année, plateforme, description, note, statut, jaquette
- État normal et état d'erreur (champ manquant surligné en rouge, message d'erreur)
- Boutons Annuler / Enregistrer

### Écran 3 : Confirmation de suppression
- Dialog de confirmation avant suppression d'un jeu

### (Optionnel) Écran 4 : Vue statistiques
- Répartition par plateforme, par statut, note moyenne, etc.

**Conseil :** pas besoin d'un design élaboré. L'important est que les **informations soient bien organisées** et que la **navigation soit logique**.

---

## Document `docs/ux-justification.md` — à remplir

Ce document est un **livrable noté**. Il doit expliquer pourquoi tu as fait ces choix, pas juste décrire ce que tu as fait.

Questions auxquelles répondre :
1. Pourquoi la sidebar à gauche ? Pourquoi ces sections ?
2. Pourquoi le tableau pour la liste et pas une grille de cartes ?
3. Comment l'utilisateur ajoute-t-il un jeu ? Quel est le chemin le plus court ?
4. Comment l'app empêche-t-elle les erreurs de saisie ?
5. Comment l'utilisateur retrouve-t-il un jeu dans une grande collection ?
6. Pourquoi ce choix de couleurs / thème ?

Une fois la maquette terminée :
- Partager le lien Figma dans `docs/ux-justification.md`
- Partager le lien Figma dans le `README.md` (demander à Mateo)

---

## Contribution au frontend (après la maquette)
Quand Hiba commence à coder l'interface, tu peux l'aider à vérifier que ce qu'elle implémente **correspond bien à la maquette**.  
Tu peux aussi contribuer au `README.md` (section "Fonctionnalités").

---

---

## Livrables finaux attendus (checklist)

- [ ] Application qui compile et se lance (`mvn javafx:run`)
- [ ] CRUD complet (ajouter, modifier, supprimer, consulter)
- [ ] Recherche + filtres + tri fonctionnels
- [ ] Données persistées en SQLite (retrouvées après redémarrage)
- [ ] Gestion des erreurs de saisie (pas de crash)
- [ ] Maquette Figma avec lien dans le README
- [ ] Document `docs/ux-justification.md` complété
- [ ] `README.md` soigné avec étapes d'installation
- [ ] Tests unitaires (GameService)
- [ ] **Bonus :** JAR exécutable
- [ ] **Bonus :** Docker Compose
- [ ] **Bonus :** Démo vidéo
