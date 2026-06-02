package com.gamevault.controller;

import com.gamevault.model.Game;
import com.gamevault.model.GameStatus;
import com.gamevault.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> platformFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private FlowPane gameGrid;
    @FXML private Label statusBar;
    @FXML private Label subtitleLabel;

    // Detail panel
    @FXML private Label     detailEmpty;
    @FXML private VBox      detailContent;
    @FXML private ImageView detailCover;
    @FXML private Label     detailTitle;
    @FXML private Label     detailPlatform;
    @FXML private Label     detailYear;
    @FXML private Label     detailDeveloper;
    @FXML private Label     detailPublisher;
    @FXML private Label     detailRating;
    @FXML private Label     detailStatus;
    @FXML private Label     detailDescription;

    private final GameService gameService = new GameService();
    private String currentSortField = "title";
    private boolean sortAscending   = true;
    private boolean refreshing      = false;
    private Game    selectedGame    = null;
    private VBox    selectedCard    = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initFilters();
        searchField.textProperty().addListener((obs, o, v) -> refresh());
        refresh();
    }

    @FXML private void onSearch()         { refresh(); }
    @FXML private void onShowCollection() { refresh(); }
    @FXML private void onSortTitle()      { sortBy("title"); }
    @FXML private void onSortYear()       { sortBy("releaseYear"); }
    @FXML private void onSortRating()     { sortBy("rating"); }
    @FXML private void onSortPlatform()   { sortBy("platform"); }

    @FXML
    private void onSettings() {
        new Alert(Alert.AlertType.INFORMATION, "Paramètres à venir.", ButtonType.OK).showAndWait();
    }

    @FXML private void onAddGame() { openForm(null); }

    @FXML
    private void onEditGame() {
        if (selectedGame == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionnez un jeu à modifier.", ButtonType.OK).showAndWait();
            return;
        }
        openForm(selectedGame);
    }

    @FXML
    private void onDeleteGame() {
        if (selectedGame == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionnez un jeu à supprimer.", ButtonType.OK).showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer « " + selectedGame.getTitle() + " » ?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().filter(b -> b == ButtonType.YES).ifPresent(b -> {
            try {
                gameService.deleteGame(selectedGame);
                selectedGame = null;
                selectedCard = null;
                refresh();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage()).showAndWait();
            }
        });
    }

    // ── Filters ──────────────────────────────────────────────────────────────

    private void initFilters() {
        platformFilter.getItems().add("Toutes plateformes");
        platformFilter.setValue("Toutes plateformes");
        platformFilter.setOnAction(e -> refresh());

        statusFilter.getItems().add("Tous statuts");
        for (GameStatus s : GameStatus.values()) statusFilter.getItems().add(s.getLabel());
        statusFilter.setValue("Tous statuts");
        statusFilter.setOnAction(e -> refresh());
    }

    private void refreshPlatformOptions() {
        String current = platformFilter.getValue();
        platformFilter.getItems().setAll("Toutes plateformes");
        gameService.getAllGames().stream()
                .map(Game::getPlatform)
                .filter(Objects::nonNull)
                .distinct().sorted()
                .forEach(p -> platformFilter.getItems().add(p));
        String restored = (current != null && platformFilter.getItems().contains(current))
                ? current : "Toutes plateformes";
        platformFilter.setValue(restored);
    }

    // ── Refresh ───────────────────────────────────────────────────────────────

    private void refresh() {
        if (refreshing) return;
        refreshing = true;
        try {
            List<Game> results = gameService.search(searchField.getText());
            String platform = "Toutes plateformes".equals(platformFilter.getValue())
                    ? null : platformFilter.getValue();
            GameStatus status = resolveStatus(statusFilter.getValue());
            results = gameService.filter(results, platform, status);
            results = gameService.sort(results, currentSortField, sortAscending);

            selectedCard = null;
            gameGrid.getChildren().clear();
            for (Game g : results) gameGrid.getChildren().add(buildCard(g));

            statusBar.setText(results.size() + " jeu(x) affiché(s)");
            subtitleLabel.setText(gameService.getAllGames().size() + " jeux dans la collection");
            refreshPlatformOptions();
        } finally {
            refreshing = false;
        }

        // If previously selected game no longer visible, clear detail panel
        if (selectedGame != null && selectedCard == null) {
            selectedGame = null;
            showDetail(null);
        }
    }

    // ── Card builder ──────────────────────────────────────────────────────────

    private VBox buildCard(Game g) {
        // Cover area (fixed size, with placeholder if no image)
        StackPane coverArea = new StackPane();
        coverArea.setPrefSize(130, 175);
        coverArea.setMaxSize(130, 175);
        coverArea.getStyleClass().add("card-cover");

        boolean loaded = false;
        if (g.getCoverPath() != null && !g.getCoverPath().isBlank()) {
            try {
                ImageView iv = new ImageView(
                        new Image("file:" + g.getCoverPath(), 130, 175, false, true));
                iv.setFitWidth(130);
                iv.setFitHeight(175);
                coverArea.getChildren().add(iv);
                loaded = true;
            } catch (Exception ignored) {}
        }
        if (!loaded) {
            String initial = (g.getTitle() != null && !g.getTitle().isEmpty())
                    ? String.valueOf(g.getTitle().charAt(0)).toUpperCase() : "?";
            Label lbl = new Label(initial);
            lbl.getStyleClass().add("card-cover-initial");
            coverArea.getChildren().add(lbl);
        }

        Label titleLbl = new Label(g.getTitle());
        titleLbl.getStyleClass().add("card-title");
        titleLbl.setWrapText(true);
        titleLbl.setMaxWidth(130);
        titleLbl.setAlignment(Pos.CENTER);

        Label platformLbl = new Label(g.getPlatform() != null ? g.getPlatform() : "");
        platformLbl.getStyleClass().add("card-platform");

        VBox card = new VBox(8, coverArea, titleLbl, platformLbl);
        card.getStyleClass().add("game-card");
        card.setPrefWidth(150);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(10));

        card.setOnMouseClicked(e -> selectCard(card, g));

        // Restore visual selection if this card was previously selected
        if (selectedGame != null && selectedGame.getId() != null
                && selectedGame.getId().equals(g.getId())) {
            card.getStyleClass().add("game-card-selected");
            selectedCard = card;
            showDetail(g);
        }

        return card;
    }

    private void selectCard(VBox card, Game g) {
        if (selectedCard != null) selectedCard.getStyleClass().remove("game-card-selected");
        selectedGame = g;
        selectedCard = card;
        card.getStyleClass().add("game-card-selected");
        showDetail(g);
    }

    // ── Detail panel ──────────────────────────────────────────────────────────

    private void showDetail(Game game) {
        if (game == null) {
            detailEmpty.setVisible(true);    detailEmpty.setManaged(true);
            detailContent.setVisible(false); detailContent.setManaged(false);
            return;
        }
        detailEmpty.setVisible(false);   detailEmpty.setManaged(false);
        detailContent.setVisible(true);  detailContent.setManaged(true);

        detailTitle.setText(game.getTitle());
        detailPlatform.setText(nvl(game.getPlatform(), "—"));
        detailYear.setText(game.getReleaseYear() != null ? String.valueOf(game.getReleaseYear()) : "—");
        detailDeveloper.setText(nvl(game.getDeveloper(), "—"));
        detailPublisher.setText(nvl(game.getPublisher(), "—"));
        detailRating.setText(game.getRating() != null
                ? String.format("★ %.1f / 10", game.getRating()) : "—");
        detailStatus.setText(game.getStatus() != null ? game.getStatus().getLabel() : "—");
        detailDescription.setText(nvl(game.getDescription(), ""));

        if (game.getCoverPath() != null && !game.getCoverPath().isBlank()) {
            try { detailCover.setImage(
                    new Image("file:" + game.getCoverPath(), 220, 300, true, true)); }
            catch (Exception ignored) { detailCover.setImage(null); }
        } else {
            detailCover.setImage(null);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void sortBy(String field) {
        if (currentSortField.equals(field)) sortAscending = !sortAscending;
        else { currentSortField = field; sortAscending = true; }
        refresh();
    }

    private GameStatus resolveStatus(String label) {
        if (label == null || "Tous statuts".equals(label)) return null;
        for (GameStatus s : GameStatus.values()) if (s.getLabel().equals(label)) return s;
        return null;
    }

    private String nvl(String s, String def) {
        return (s != null && !s.isBlank()) ? s : def;
    }

    private void openForm(Game game) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameFormView.fxml"));
            Parent root = loader.load();
            GameFormController ctrl = loader.getController();
            ctrl.setGame(game);
            ctrl.setOnSaved(this::refresh);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(game == null ? "Ajouter un jeu" : "Modifier un jeu");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir le formulaire : " + e.getMessage()).showAndWait();
        }
    }
}
