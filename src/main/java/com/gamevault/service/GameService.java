package com.gamevault.service;

import com.gamevault.model.Game;
import com.gamevault.model.GameStatus;
import com.gamevault.repository.GameRepository;

import java.util.List;
import java.util.Optional;

// TODO (Marc) : implémenter la logique métier, la validation et les filtres
public class GameService {

    private final GameRepository repository = new GameRepository();

    public void addGame(Game game) {
        // TODO : valider puis appeler repository.save()
    }

    public void updateGame(Game game) {
        // TODO : valider puis appeler repository.save()
    }

    public void deleteGame(Game game) {
        // TODO : vérifier que le jeu existe puis appeler repository.delete()
    }

    public Optional<Game> findById(Long id) {
        // TODO
        return repository.findById(id);
    }

    public List<Game> getAllGames() {
        // TODO
        return repository.findAll();
    }

    public List<Game> search(String query) {
        // TODO : si query vide retourner tous les jeux
        return repository.search(query);
    }

    public List<Game> filter(List<Game> games, String platform, GameStatus status) {
        // TODO : filtrer par plateforme et statut
        return games;
    }

    public List<Game> sort(List<Game> games, String field, boolean ascending) {
        // TODO : trier par title, platform, releaseYear, rating ou status
        return games;
    }

    private void validate(Game game) {
        // TODO : title obligatoire, platform obligatoire, année entre 1950-2100, note entre 0-10
    }
}
