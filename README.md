# GameVault

Gestionnaire de collection de jeux vidéo – Projet RetroSphere

## Stack technique
- Java 21
- JavaFX 21 (FXML)
- Hibernate 6 + SQLite
- Maven

## Lancer le projet

```bash
mvn javafx:run
```

## Structure du projet

```
src/
├── main/
│   ├── java/com/gamevault/
│   │   ├── MainApp.java
│   │   ├── model/          # Game, GameStatus
│   │   ├── repository/     # GameRepository (CRUD Hibernate)
│   │   ├── service/        # GameService (logique métier)
│   │   ├── controller/     # MainController, GameFormController
│   │   ├── config/         # HibernateUtil
│   │   └── util/           # AppConfig
│   └── resources/
│       ├── fxml/           # Vues JavaFX
│       ├── css/            # Styles
│       ├── hibernate.cfg.xml
│       └── application.properties
└── test/
```

## Équipe
| Rôle | Membre |
|------|--------|
| Chef de projet / DevOps | Mateo |
| Backend / Base de données | Marc |
| Frontend / Interface | Hiba |
| UX/UI / Documentation | Yanina |
