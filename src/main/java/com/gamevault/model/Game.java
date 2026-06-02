package com.gamevault.model;

import jakarta.persistence.*;
import java.time.LocalDate;

// TODO (Marc) : compléter les annotations Hibernate si besoin, ajouter des champs métier
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String developer;
    private String publisher;
    private Integer releaseYear;

    @Column(nullable = false)
    private String platform;

    @Column(length = 2000)
    private String description;

    private Double rating;

    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.IN_COLLECTION;

    private String coverPath;

    @Column(updatable = false)
    private LocalDate addedAt = LocalDate.now();

    public Game() {}

    public Game(String title, String platform) {
        this.title = title;
        this.platform = platform;
    }

    // TODO (Marc) : getters et setters
}
