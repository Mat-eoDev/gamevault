package com.gamevault.model;

import jakarta.persistence.*;
import java.time.LocalDate;

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

    // Getters & Setters

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDeveloper() { return developer; }
    public void setDeveloper(String developer) { this.developer = developer; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }

    public String getCoverPath() { return coverPath; }
    public void setCoverPath(String coverPath) { this.coverPath = coverPath; }

    public LocalDate getAddedAt() { return addedAt; }

    @Override
    public String toString() {
        return title + " (" + platform + ", " + releaseYear + ")";
    }
}
