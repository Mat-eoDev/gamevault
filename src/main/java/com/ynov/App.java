package com.ynov;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showCollectionView();
    }

    public static void showCollectionView() {
        loadView("collection-view.fxml", "GameVault - Ma collection");
    }

    public static void showDetailView() {
        loadView("detail-view.fxml", "GameVault - Détail du jeu");
    }

    public static void showGameFormView() {
        loadView("game-form-view.fxml", "GameVault - Ajouter un jeu");
    }

    private static void loadView(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/ynov/" + fxmlFile));
            Scene scene = new Scene(loader.load(), 1200, 760);
            scene.getStylesheets().add(App.class.getResource("/com/ynov/style.css").toExternalForm());

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(650);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}