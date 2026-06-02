package com.gamevault.repository;

import com.gamevault.model.Game;

import java.util.List;
import java.util.Optional;

// TODO (Marc) : implémenter chaque méthode avec Hibernate (Session, Transaction)
public class GameRepository {

    public void save(Game game) {
        // TODO
    }

    public void delete(Game game) {
        // TODO
    }

    public Optional<Game> findById(Long id) {
        // TODO
        return Optional.empty();
    }

    public List<Game> findAll() {
        // TODO
        return List.of();
    }

    public List<Game> search(String query) {
        // TODO
        return List.of();
    }
}
