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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
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

    private final GameService gameService = new GameService();
    private final ObservableList<Game> displayedGames = FXCollections.observableArrayList();
    private String currentSortField = "title";
    private boolean sortAscending = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bindColumns();
        initFilters();
        gameTable.setItems(displayedGames);
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
        List<Game> results = gameService.search(searchField.getText());
        String platform = "Toutes plateformes".equals(platformFilter.getValue()) ? null : platformFilter.getValue();
        GameStatus status = resolveStatus(statusFilter.getValue());
        results = gameService.filter(results, platform, status);
        results = gameService.sort(results, currentSortField, sortAscending);
        displayedGames.setAll(results);
        statusBar.setText(results.size() + " jeu(x) affiché(s)");
        subtitleLabel.setText(gameService.getAllGames().size() + " jeux dans la collection");
        refreshPlatformOptions(results);
    }

    private void refreshPlatformOptions(List<Game> games) {
        String current = platformFilter.getValue();
        platformFilter.getItems().setAll("Toutes plateformes");
        games.stream().map(Game::getPlatform).distinct().sorted().forEach(p -> platformFilter.getItems().add(p));
        platformFilter.setValue(current != null ? current : "Toutes plateformes");
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
