package com.gamevault.model;

public enum GameStatus {

    IN_COLLECTION("Collection"),
    TO_PLAY("À faire"),
    IN_PROGRESS("En cours"),
    COMPLETED("Terminé"),
    ABANDONED("Abandonné");

    private final String label;

    GameStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
