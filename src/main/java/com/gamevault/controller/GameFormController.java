package com.gamevault.controller;

import com.gamevault.model.Game;
import com.gamevault.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

// TODO (Hiba) : relier les champs FXML du formulaire, gérer la validation visuelle et la sauvegarde
public class GameFormController implements Initializable {

    // TODO (Hiba) : déclarer les @FXML (TextField titre, développeur, éditeur, année, ComboBox plateforme, statut, Slider note, ImageView jaquette, Label erreur…)

    private final GameService gameService = new GameService();
    private Game game;
    private Runnable onSaved;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO : remplir la ComboBox plateforme, initialiser le Slider, la ComboBox statut
    }

    public void setGame(Game game) {
        this.game = game;
        // TODO : si game != null, pré-remplir tous les champs
    }

    public void setOnSaved(Runnable callback) {
        this.onSaved = callback;
    }

    @FXML
    private void onChooseCover() {
        // TODO : ouvrir un FileChooser pour sélectionner une image et l'afficher en prévisualisation
    }

    @FXML
    private void onSave() {
        // TODO : lire les champs, appeler gameService.addGame() ou updateGame(), afficher les erreurs
    }

    @FXML
    private void onCancel() {
        // TODO : fermer la fenêtre sans sauvegarder
    }
}
