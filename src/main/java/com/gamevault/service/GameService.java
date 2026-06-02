package com.gamevault.service;

import com.gamevault.model.Game;
import com.gamevault.model.GameStatus;
import com.gamevault.repository.GameRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameService {

    private final GameRepository repository = new GameRepository();

    public void addGame(Game game) {
        validate(game);
        repository.save(game);
    }

    public void updateGame(Game game) {
        validate(game);
        repository.save(game);
    }

    public void deleteGame(Game game) {
        if (game == null || game.getId() == null)
            throw new IllegalArgumentException("Impossible de supprimer un jeu non sauvegardé.");
        repository.delete(game);
    }

    public Optional<Game> findById(Long id) {
        return repository.findById(id);
    }

    public List<Game> getAllGames() {
        return repository.findAll();
    }

    public List<Game> search(String query) {
        if (query == null || query.isBlank()) return getAllGames();
        return repository.search(query.trim());
    }

    public List<Game> filter(List<Game> games, String platform, GameStatus status) {
        return games.stream()
                .filter(g -> platform == null || platform.isBlank() || g.getPlatform().equalsIgnoreCase(platform))
                .filter(g -> status == null || g.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Game> sort(List<Game> games, String field, boolean ascending) {
        Comparator<Game> cmp = switch (field) {
            case "platform"    -> Comparator.comparing(Game::getPlatform, Comparator.nullsLast(String::compareToIgnoreCase));
            case "releaseYear" -> Comparator.comparing(Game::getReleaseYear, Comparator.nullsLast(Integer::compareTo));
            case "rating"      -> Comparator.comparing(Game::getRating, Comparator.nullsLast(Double::compareTo));
            case "status"      -> Comparator.comparing(g -> g.getStatus().getLabel());
            default            -> Comparator.comparing(Game::getTitle, String::compareToIgnoreCase);
        };
        if (!ascending) cmp = cmp.reversed();
        return games.stream().sorted(cmp).collect(Collectors.toList());
    }

    private void validate(Game game) {
        if (game == null)
            throw new IllegalArgumentException("Le jeu ne peut pas être null.");
        if (game.getTitle() == null || game.getTitle().isBlank())
            throw new IllegalArgumentException("Le titre est obligatoire.");
        if (game.getPlatform() == null || game.getPlatform().isBlank())
            throw new IllegalArgumentException("La plateforme est obligatoire.");
        if (game.getReleaseYear() != null && (game.getReleaseYear() < 1950 || game.getReleaseYear() > 2100))
            throw new IllegalArgumentException("L'année de sortie est invalide.");
        if (game.getRating() != null && (game.getRating() < 0 || game.getRating() > 10))
            throw new IllegalArgumentException("La note doit être comprise entre 0 et 10.");
    }
}
