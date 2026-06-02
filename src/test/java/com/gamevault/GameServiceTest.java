package com.gamevault;

import com.gamevault.model.Game;
import com.gamevault.model.GameStatus;
import com.gamevault.repository.GameRepository;
import com.gamevault.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameServiceTest {

    private InMemoryGameRepository repository;
    private GameService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryGameRepository();
        service = new GameService(repository);
    }

    @Test
    void addGameWithValidGameSavesIt() {
        Game game = new Game("Chrono Trigger", "Super Nintendo");

        service.addGame(game);

        assertEquals(1, repository.findAll().size());
        assertEquals("Chrono Trigger", repository.findAll().getFirst().getTitle());
    }

    @Test
    void addGameWithoutTitleThrowsException() {
        Game game = new Game("", "PC");

        assertThrows(IllegalArgumentException.class, () -> service.addGame(game));
    }

    @Test
    void addGameWithoutPlatformThrowsException() {
        Game game = new Game("Portal", " ");

        assertThrows(IllegalArgumentException.class, () -> service.addGame(game));
    }

    @Test
    void addGameWithInvalidRatingThrowsException() {
        Game game = new Game("Celeste", "PC");
        game.setRating(10.5);

        assertThrows(IllegalArgumentException.class, () -> service.addGame(game));
    }

    @Test
    void filterByPlatformReturnsMatchingGames() {
        Game pcGame = new Game("Half-Life", "PC");
        Game switchGame = new Game("Mario Kart 8 Deluxe", "Nintendo Switch");
        List<Game> games = List.of(pcGame, switchGame);

        List<Game> filteredGames = service.filter(games, "PC", null);

        assertEquals(1, filteredGames.size());
        assertEquals("Half-Life", filteredGames.getFirst().getTitle());
    }

    @Test
    void sortByReleaseYearReturnsAscendingGames() {
        Game olderGame = new Game("Sonic Adventure", "Dreamcast");
        olderGame.setReleaseYear(1998);
        Game newerGame = new Game("Elden Ring", "PC");
        newerGame.setReleaseYear(2022);
        List<Game> games = List.of(newerGame, olderGame);

        List<Game> sortedGames = service.sort(games, "releaseYear", true);

        assertEquals("Sonic Adventure", sortedGames.getFirst().getTitle());
        assertEquals("Elden Ring", sortedGames.get(1).getTitle());
    }

    private static class InMemoryGameRepository extends GameRepository {

        private final List<Game> games = new ArrayList<>();
        private long nextId = 1L;

        @Override
        public void save(Game game) {
            if (game.getId() == null) {
                game.setId(nextId++);
                games.add(game);
                return;
            }

            delete(game);
            games.add(game);
        }

        @Override
        public void delete(Game game) {
            games.removeIf(existingGame -> existingGame.getId().equals(game.getId()));
        }

        @Override
        public Optional<Game> findById(Long id) {
            return games.stream()
                    .filter(game -> game.getId().equals(id))
                    .findFirst();
        }

        @Override
        public List<Game> findAll() {
            return new ArrayList<>(games);
        }

        @Override
        public List<Game> search(String query) {
            String normalizedQuery = query.toLowerCase();
            return games.stream()
                    .filter(game -> containsIgnoreCase(game.getTitle(), normalizedQuery)
                            || containsIgnoreCase(game.getPlatform(), normalizedQuery)
                            || containsIgnoreCase(game.getDeveloper(), normalizedQuery))
                    .toList();
        }

        private boolean containsIgnoreCase(String value, String normalizedQuery) {
            return value != null && value.toLowerCase().contains(normalizedQuery);
        }
    }
}
