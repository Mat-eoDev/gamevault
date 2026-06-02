package com.gamevault.controller;

import com.gamevault.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

// TODO (Hiba) : relier les éléments FXML, implémenter l'affichage de la liste et les actions
public class MainController implements Initializable {

    // TODO (Hiba) : déclarer les @FXML (TableView, TextField recherche, ComboBox filtres, Label statusBar…)

    private final GameService gameService = new GameService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO : initialiser la table, les filtres, charger la liste
    }

    @FXML
    private void onSearch() {
        // TODO : lancer la recherche et mettre à jour la table
    }

    @FXML
    private void onAddGame() {
        // TODO : ouvrir GameFormView en mode création
    }

    @FXML
    private void onEditGame() {
        // TODO : ouvrir GameFormView en mode édition avec le jeu sélectionné
    }

    @FXML
    private void onDeleteGame() {
        // TODO : demander confirmation puis supprimer le jeu sélectionné
    }
}
