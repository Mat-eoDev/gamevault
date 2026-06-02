package com.gamevault.model;

// TODO (Marc) : ajouter les valeurs du statut (ex: IN_COLLECTION, TO_PLAY, IN_PROGRESS, COMPLETED, ABANDONED)
public enum GameStatus {

    IN_COLLECTION("Collection");

    private final String label;

    GameStatus(String label) { this.label = label; }

    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }
}
