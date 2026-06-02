package com.gamevault.controller;

import com.gamevault.model.Game;
import com.gamevault.model.GameStatus;
import com.gamevault.service.GameService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> platformFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Game> gameTable;
    @FXML private TableColumn<Game, String> colTitle;
    @FXML private TableColumn<Game, String> colPlatform;
    @FXML private TableColumn<Game, Integer> colYear;
    @FXML private TableColumn<Game, String> colRating;
    @FXML private TableColumn<Game, String> colStatus;
    @FXML private Label statusBar;
    @FXML private Label subtitleLabel;

    // Detail panel
    @FXML private Label detailEmpty;
    @FXML private VBox  detailContent;
    @FXML private ImageView detailCover;
    @FXML private Label detailTitle;
    @FXML private Label detailPlatform;
    @FXML private Label detailYear;
    @FXML private Label detailDeveloper;
    @FXML private Label detailPublisher;
    @FXML private Label detailRating;
    @FXML private Label detailStatus;
    @FXML private Label detailDescription;

    private final GameService gameService = new GameService();
    private final ObservableList<Game> displayedGames = FXCollections.observableArrayList();
    private String currentSortField = "title";
    private boolean sortAscending = true;
    private boolean refreshing = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bindColumns();
        initFilters();
        gameTable.setItems(displayedGames);
        gameTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, v) -> showDetail(v));
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

    @FXML
    private void onAddGame() { openForm(null); }

    @FXML
    private void onEditGame() {
        Game selected = gameTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionnez un jeu à modifier.", ButtonType.OK).showAndWait();
            return;
        }
        openForm(selected);
    }

    @FXML
    private void onDeleteGame() {
        Game selected = gameTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionnez un jeu à supprimer.", ButtonType.OK).showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer « " + selected.getTitle() + " » ?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().filter(b -> b == ButtonType.YES).ifPresent(b -> {
            try {
                gameService.deleteGame(selected);
                refresh();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression : " + e.getMessage()).showAndWait();
            }
        });
    }

    private void bindColumns() {
        colTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));
        colTitle.setCellFactory(col -> new TableCell<>() {
            private final ImageView iv  = new ImageView();
            private final Label     lbl = new Label();
            private final HBox      box = new HBox(8, iv, lbl);
            {
                iv.setFitHeight(36); iv.setFitWidth(27); iv.setPreserveRatio(true);
                box.setAlignment(Pos.CENTER_LEFT);
            }
            @Override
            protected void updateItem(String title, boolean empty) {
                super.updateItem(title, empty);
                if (empty || title == null) { setGraphic(null); setText(null); return; }
                lbl.setText(title);
                int idx = getIndex();
                if (idx >= 0 && idx < getTableView().getItems().size()) {
                    Game g = getTableView().getItems().get(idx);
                    if (g != null && g.getCoverPath() != null && !g.getCoverPath().isBlank()) {
                        try { iv.setImage(new Image("file:" + g.getCoverPath(), 27, 36, true, true)); }
                        catch (Exception ignored) { iv.setImage(null); }
                    } else { iv.setImage(null); }
                }
                setGraphic(box);
                setText(null);
            }
        });

        colPlatform.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPlatform()));
        colYear.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getReleaseYear()));
        colRating.setCellValueFactory(d -> {
            Double r = d.getValue().getRating();
            return new SimpleStringProperty(r != null ? String.format("★ %.1f", r) : "—");
        });
        colStatus.setCellValueFactory(d -> {
            GameStatus s = d.getValue().getStatus();
            return new SimpleStringProperty(s != null ? s.getLabel() : "");
        });
    }

    private void initFilters() {
        platformFilter.getItems().add("Toutes plateformes");
        platformFilter.setValue("Toutes plateformes");
        platformFilter.setOnAction(e -> refresh());

        statusFilter.getItems().add("Tous statuts");
        for (GameStatus s : GameStatus.values()) statusFilter.getItems().add(s.getLabel());
        statusFilter.setValue("Tous statuts");
        statusFilter.setOnAction(e -> refresh());
    }

    private void refresh() {
        if (refreshing) return;
        refreshing = true;
        try {
            Long selectedId = Optional.ofNullable(gameTable.getSelectionModel().getSelectedItem())
                    .map(Game::getId).orElse(null);

            List<Game> results = gameService.search(searchField.getText());
            String platform = "Toutes plateformes".equals(platformFilter.getValue()) ? null : platformFilter.getValue();
            GameStatus status = resolveStatus(statusFilter.getValue());
            results = gameService.filter(results, platform, status);
            results = gameService.sort(results, currentSortField, sortAscending);
            displayedGames.setAll(results);
            statusBar.setText(results.size() + " jeu(x) affiché(s)");
            subtitleLabel.setText(gameService.getAllGames().size() + " jeux dans la collection");
            refreshPlatformOptions();

            // Restore selection after data reload
            if (selectedId != null) {
                for (Game g : displayedGames) {
                    if (selectedId.equals(g.getId())) {
                        gameTable.getSelectionModel().select(g);
                        break;
                    }
                }
            }
        } finally {
            refreshing = false;
        }
        showDetail(gameTable.getSelectionModel().getSelectedItem());
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

    private void showDetail(Game game) {
        if (game == null) {
            detailEmpty.setVisible(true);   detailEmpty.setManaged(true);
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
        detailRating.setText(game.getRating() != null ? String.format("★ %.1f / 10", game.getRating()) : "—");
        detailStatus.setText(game.getStatus() != null ? game.getStatus().getLabel() : "—");
        detailDescription.setText(nvl(game.getDescription(), ""));

        if (game.getCoverPath() != null && !game.getCoverPath().isBlank()) {
            try { detailCover.setImage(new Image("file:" + game.getCoverPath(), 210, 280, true, true)); }
            catch (Exception ignored) { detailCover.setImage(null); }
        } else {
            detailCover.setImage(null);
        }
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
