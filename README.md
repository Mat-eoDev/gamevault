# GameVault

> Gestionnaire de collection de jeux vidéo pour l'association **RetroSphere**

<!-- TODO: ajouter un screenshot de l'application une fois l'UI terminée -->

---

## Présentation

RetroSphere est une association de passionnés de jeux vidéo gérant une collection de titres rétro et modernes sur de nombreuses plateformes (PC, PlayStation, Xbox, Nintendo Switch, Sega Saturn, Dreamcast, Super Nintendo, et bien d'autres).

GameVault est l'application desktop qui remplace leurs feuilles de calcul dispersées. Elle permet de centraliser, consulter, rechercher et gérer l'ensemble de la collection depuis une interface moderne et intuitive.

---

## Fonctionnalités

- Consulter l'ensemble de la collection en un coup d'œil
- Ajouter, modifier et supprimer un jeu
- Rechercher un jeu par titre, plateforme ou développeur
- Filtrer par plateforme et par statut (Terminé, En cours, À faire…)
- Trier par titre, année, note ou statut
- Associer une jaquette à chaque jeu
- Données persistées localement (SQLite) — retrouvées après redémarrage

---

## Stack technique

| Technologie | Version | Usage |
|-------------|---------|-------|
| Java | 21 | Langage principal |
| JavaFX | 21 | Interface graphique |
| FXML | — | Déclaration des vues |
| Hibernate | 6.4 | ORM / persistance |
| SQLite | 3.45 | Base de données locale |
| Maven | 3.8+ | Build et dépendances |

---

## Prérequis

- [Java 21+](https://adoptium.net/) — **seul prérequis obligatoire**
- Maven 3.8+ *(optionnel — le projet inclut un Maven Wrapper)*

Vérifier Java :
```bash
java --version
```

---

## Installation et lancement

```bash
# 1. Cloner le dépôt
git clone https://github.com/Mat-eoDev/gamevault.git
cd gamevault

# 2. Se placer sur la branche principale
git checkout dev
```

**Option A — avec le Maven Wrapper (Maven non requis)**
```bash
# macOS / Linux
./mvnw javafx:run

# Windows
mvnw.cmd javafx:run
```

**Option B — avec Maven installé**
```bash
mvn javafx:run
```

**Option C — fat JAR (aucun outil requis, Java 21 uniquement)**
```bash
# Générer le JAR
./mvnw package          # ou : mvn package

# Lancer
java -jar target/gamevault-1.0.0-fat.jar
```

La base de données SQLite (`gamevault.db`) est créée automatiquement au premier lancement.

---

## Lancer les tests

```bash
./mvnw test    # ou : mvn test
```

---

## Structure du projet

```
gamevault/
├── src/
│   ├── main/
│   │   ├── java/com/gamevault/
│   │   │   ├── MainApp.java              # Point d'entrée
│   │   │   ├── model/                    # Game, GameStatus
│   │   │   ├── repository/               # GameRepository (CRUD Hibernate)
│   │   │   ├── service/                  # GameService (logique métier, validation)
│   │   │   ├── controller/               # Contrôleurs JavaFX
│   │   │   ├── config/                   # HibernateUtil
│   │   │   └── util/                     # AppConfig (lecture application.properties)
│   │   └── resources/
│   │       ├── fxml/                     # Vues FXML
│   │       ├── css/                      # Feuilles de style
│   │       ├── hibernate.cfg.xml         # Configuration Hibernate
│   │       └── application.properties   # Paramètres externalisés
│   └── test/
│       └── java/com/gamevault/           # Tests unitaires
└── docs/
    ├── TASKS.md                          # Guide de travail par rôle
    └── ux-justification.md              # Justification des choix UX/UI
```

---

## Configuration

Les paramètres modifiables sont dans `src/main/resources/application.properties` :

```properties
db.path=gamevault.db          # chemin vers la base SQLite
hibernate.show_sql=false      # afficher les requêtes SQL dans les logs
hibernate.hbm2ddl.auto=update # création automatique des tables
```

---

## Conception UX/UI

<!-- TODO: ajouter le lien Figma quand Yanina l'envoie -->
> Maquette Figma : *à venir*

Le document de justification des choix ergonomiques est disponible dans [`docs/ux-justification.md`](docs/ux-justification.md).

---

## Équipe

| Rôle | Membre |
|------|--------|
| Chef de projet / DevOps | Mateo |
| Backend / Base de données | Marc |
| Frontend / Interface | Hiba |
| UX/UI / Documentation | Yanina |
