package com.gamevault.controller;

import com.gamevault.model.Game;
import com.gamevault.model.GameStatus;
import com.gamevault.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class GameFormController implements Initializable {

    @FXML private TextField titleField;
    @FXML private TextField developerField;
    @FXML private TextField publisherField;
    @FXML private TextField yearField;
    @FXML private ComboBox<String> platformCombo;
    @FXML private TextArea descriptionArea;
    @FXML private Slider ratingSlider;
    @FXML private Label ratingLabel;
    @FXML private ComboBox<GameStatus> statusCombo;
    @FXML private ImageView coverPreview;
    @FXML private Label errorLabel;

    private final GameService gameService = new GameService();
    private Game game;
    private Runnable onSaved;
    private String selectedCoverPath;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        platformCombo.getItems().addAll(
                "PC", "PlayStation 5", "PlayStation 4", "PlayStation 3",
                "Xbox Series X", "Xbox One", "Nintendo Switch", "Nintendo 3DS",
                "Super Nintendo", "Sega Mega Drive", "Sega Saturn", "Dreamcast", "Autre"
        );
        statusCombo.getItems().addAll(GameStatus.values());
        statusCombo.setValue(GameStatus.IN_COLLECTION);
        ratingSlider.valueProperty().addListener((obs, o, v) ->
                ratingLabel.setText(String.format("%.1f / 10", v.doubleValue())));
        errorLabel.setVisible(false);
    }

    public void setGame(Game game) {
        this.game = game;
        if (game != null) populate(game);
    }

    public void setOnSaved(Runnable callback) { this.onSaved = callback; }

    @FXML
    private void onChooseCover() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choisir une jaquette");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fc.showOpenDialog(coverPreview.getScene().getWindow());
        if (file != null) {
            selectedCoverPath = file.getAbsolutePath();
            coverPreview.setImage(new Image(file.toURI().toString(), 160, 220, true, true));
        }
    }

    @FXML
    private void onSave() {
        errorLabel.setVisible(false);
        try {
            Game target = (game != null) ? game : new Game();
            applyFields(target);
            if (game == null) gameService.addGame(target);
            else gameService.updateGame(target);
            if (onSaved != null) onSaved.run();
            close();
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    @FXML private void onCancel() { close(); }

    private void populate(Game g) {
        titleField.setText(g.getTitle());
        developerField.setText(g.getDeveloper() != null ? g.getDeveloper() : "");
        publisherField.setText(g.getPublisher() != null ? g.getPublisher() : "");
        yearField.setText(g.getReleaseYear() != null ? String.valueOf(g.getReleaseYear()) : "");
        platformCombo.setValue(g.getPlatform());
        descriptionArea.setText(g.getDescription() != null ? g.getDescription() : "");
        if (g.getRating() != null) ratingSlider.setValue(g.getRating());
        if (g.getStatus() != null) statusCombo.setValue(g.getStatus());
        if (g.getCoverPath() != null && !g.getCoverPath().isBlank()) {
            try { coverPreview.setImage(new Image("file:" + g.getCoverPath(), 160, 220, true, true)); }
            catch (Exception ignored) {}
        }
    }

    private void applyFields(Game g) {
        g.setTitle(titleField.getText());
        g.setDeveloper(developerField.getText());
        g.setPublisher(publisherField.getText());
        g.setPlatform(platformCombo.getValue());
        g.setDescription(descriptionArea.getText());
        g.setRating(ratingSlider.getValue() > 0 ? ratingSlider.getValue() : null);
        g.setStatus(statusCombo.getValue());
        if (selectedCoverPath != null) g.setCoverPath(selectedCoverPath);
        String y = yearField.getText().trim();
        if (!y.isBlank()) {
            try { g.setReleaseYear(Integer.parseInt(y)); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("L'année doit être un nombre (ex: 2021)."); }
        } else {
            g.setReleaseYear(null);
        }
    }

    private void close() { ((Stage) titleField.getScene().getWindow()).close(); }
}
