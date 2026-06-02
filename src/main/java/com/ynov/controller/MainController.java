package com.ynov.controller;

import com.ynov.App;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class MainController {

    @FXML
    private void goToCollection() {
        App.showCollectionView();
    }

    @FXML
    private void goToAddGame() {
        App.showGameFormView();
    }

    @FXML
    private void goToDetail() {
        App.showDetailView();
    }

    @FXML
    private void showSettingsMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Paramètres");
        alert.setHeaderText(null);
        alert.setContentText("Écran paramètres à faire plus tard.");
        alert.showAndWait();
    }
}