package com.ynov.controller;

import com.ynov.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

public class DetailController {

    @FXML
    private void goBackToCollection() {
        App.showCollectionView();
    }

    @FXML
    private void goToEditGame() {
        App.showGameFormView();
    }

    @FXML
    private void confirmDelete() {
        try {
            URL fxmlUrl = getClass().getResource("/com/ynov/delete-dialog-view.fxml");

            if (fxmlUrl == null) {
                throw new RuntimeException("FXML suppression introuvable : /com/ynov/delete-dialog-view.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load());

            URL cssUrl = getClass().getResource("/com/ynov/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Supprimer ce jeu ?");
            dialogStage.setScene(scene);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            DeleteDialogController controller = loader.getController();

            if (controller.isConfirmed()) {
                App.showCollectionView();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}