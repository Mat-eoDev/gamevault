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

- [Java 21+](https://adoptium.net/)
- [Maven 3.8+](https://maven.apache.org/download.cgi)

Vérifier les installations :
```bash
java --version
mvn --version
```

---

## Installation et lancement

```bash
# 1. Cloner le dépôt
git clone https://github.com/Mat-eoDev/gamevault.git
cd gamevault

# 2. Se placer sur la branche de développement
git checkout dev

# 3. Compiler le projet
mvn clean compile

# 4. Lancer l'application
mvn javafx:run
```

La base de données SQLite (`gamevault.db`) est créée automatiquement au premier lancement dans le répertoire courant.

---

## Lancer les tests

```bash
mvn test
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
