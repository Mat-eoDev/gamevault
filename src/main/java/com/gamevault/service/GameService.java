package com.gamevault.service;

import com.gamevault.model.Game;
import com.gamevault.model.GameStatus;
import com.gamevault.repository.GameRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameService {

    private final GameRepository repository;

    public GameService() {
        this(new GameRepository());
    }

    public GameService(GameRepository repository) {
        this.repository = repository;
    }

    public void addGame(Game game) {
        validate(game);
        applyDefaults(game);
        repository.save(game);
    }

    public void updateGame(Game game) {
        validate(game);
        applyDefaults(game);
        repository.save(game);
    }

    public void deleteGame(Game game) {
        if (game == null || game.getId() == null) {
            throw new IllegalArgumentException("Le jeu à supprimer est invalide.");
        }
        repository.findById(game.getId())
                .orElseThrow(() -> new IllegalArgumentException("Le jeu à supprimer n'existe pas."));
        repository.delete(game);
    }

    public Optional<Game> findById(Long id) {
        return repository.findById(id);
    }

    public List<Game> getAllGames() {
        return repository.findAll();
    }

    public List<Game> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.search(query.trim());
    }

    public List<Game> filter(List<Game> games, String platform, GameStatus status) {
        if (games == null) {
            return List.of();
        }

        return games.stream()
                .filter(game -> platform == null
                        || platform.isBlank()
                        || platform.equalsIgnoreCase(game.getPlatform()))
                .filter(game -> status == null || status == game.getStatus())
                .collect(Collectors.toList());
    }

    public List<Game> sort(List<Game> games, String field, boolean ascending) {
        if (games == null) {
            return List.of();
        }

        Comparator<Game> comparator = switch (field == null ? "title" : field) {
            case "platform" -> compareNullable(Game::getPlatform, String.CASE_INSENSITIVE_ORDER, ascending);
            case "releaseYear" -> compareNullable(Game::getReleaseYear, Integer::compareTo, ascending);
            case "rating" -> compareNullable(Game::getRating, Double::compareTo, ascending);
            case "status" -> compareNullable(
                    game -> game.getStatus() == null ? null : game.getStatus().getLabel(),
                    String.CASE_INSENSITIVE_ORDER,
                    ascending);
            case "title" -> compareNullable(Game::getTitle, String.CASE_INSENSITIVE_ORDER, ascending);
            default -> compareNullable(Game::getTitle, String.CASE_INSENSITIVE_ORDER, ascending);
        };

        return games.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private void validate(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("Le jeu est obligatoire.");
        }
        if (game.getTitle() == null || game.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre est obligatoire.");
        }
        if (game.getPlatform() == null || game.getPlatform().trim().isEmpty()) {
            throw new IllegalArgumentException("La plateforme est obligatoire.");
        }
        if (game.getReleaseYear() != null
                && (game.getReleaseYear() < 1950 || game.getReleaseYear() > 2100)) {
            throw new IllegalArgumentException("L'année de sortie doit être comprise entre 1950 et 2100.");
        }
        if (game.getRating() != null && (game.getRating() < 0.0 || game.getRating() > 10.0)) {
            throw new IllegalArgumentException("La note doit être comprise entre 0.0 et 10.0.");
        }
    }

    private void applyDefaults(Game game) {
        if (game.getStatus() == null) {
            game.setStatus(GameStatus.IN_COLLECTION);
        }
        if (game.getAddedAt() == null) {
            game.setAddedAt(LocalDate.now());
        }
    }

    private <T> Comparator<Game> compareNullable(
            Function<Game, T> extractor,
            Comparator<? super T> comparator,
            boolean ascending) {
        Comparator<? super T> directedComparator = ascending ? comparator : comparator.reversed();
        return Comparator.comparing(extractor, Comparator.nullsLast(directedComparator));
    }
}
