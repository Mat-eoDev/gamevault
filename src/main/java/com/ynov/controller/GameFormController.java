package com.ynov.controller;

import com.ynov.App;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class GameFormController {

    @FXML private TextField titleField;
    @FXML private TextField developerField;
    @FXML private TextField publisherField;
    @FXML private TextField yearField;
    @FXML private ComboBox<String> platformComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField genreField;
    @FXML private Slider noteSlider;
    @FXML private TextArea descriptionArea;

    @FXML
    public void initialize() {
        platformComboBox.getItems().addAll(
                "PC",
                "Nintendo Switch",
                "PlayStation 4",
                "PlayStation 5",
                "Xbox Series",
                "Mobile"
        );

        statusComboBox.getItems().addAll(
                "Terminé",
                "En cours",
                "À faire",
                "Abandonné"
        );

        platformComboBox.setValue("Nintendo Switch");
        statusComboBox.setValue("À faire");
        noteSlider.setValue(7);
    }

    @FXML
    private void cancelForm() {
        App.showCollectionView();
    }

    @FXML
    private void saveGame() {
        if (titleField.getText().isBlank()) {
            showError("Le titre est obligatoire.");
            return;
        }

        if (developerField.getText().isBlank()) {
            showError("Le développeur est obligatoire.");
            return;
        }

        if (yearField.getText().isBlank()) {
            showError("L'année de sortie est obligatoire.");
            return;
        }

        showSuccess("Interface prête. Le backend branchera ensuite l'enregistrement du jeu.");
        App.showCollectionView();
    }

    @FXML
    private void chooseImage() {
        showSuccess("Le choix d'image sera relié plus tard au backend ou au stockage local.");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}