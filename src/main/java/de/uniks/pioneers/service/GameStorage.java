package de.uniks.pioneers.service;

import de.uniks.pioneers.model.GameSettings;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GameStorage {
    private String id;
    private String mapTemplate;
    private int size;
    private int victoryPoints;
    private boolean rollSeven;
    private int startingResources;

    @Inject
    public GameStorage() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public String getMapTemplate() {
        return mapTemplate;
    }

    public boolean isRollSeven() {
        return rollSeven;
    }

    public int getStartingResources() {
        return startingResources;
    }

    public void setGameOptions(GameSettings settings) {
        this.size = settings.mapRadius();
        this.victoryPoints = settings.victoryPoints();
        this.mapTemplate = settings.mapTemplate();
        this.rollSeven = settings.roll7();
        this.startingResources = settings.startingResources();
    }
}
