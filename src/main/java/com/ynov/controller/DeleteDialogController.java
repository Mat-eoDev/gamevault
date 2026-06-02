package com.ynov.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class DeleteDialogController {

    private boolean confirmed = false;

    @FXML
    private void cancelDelete() {
        confirmed = false;
        closeWindow();
    }

    @FXML
    private void confirmDelete() {
        confirmed = true;
        closeWindow();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private void closeWindow() {
        Stage stage = (Stage) javafx.stage.Window.getWindows()
                .stream()
                .filter(window -> window.isShowing() && window instanceof Stage)
                .reduce((first, second) -> second)
                .orElse(null);

        if (stage != null) {
            stage.close();
        }
    }
}