package de.uniks.pioneers.service;

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

	public void setSize(int size) {
		this.size = size;
	}

	public int getVictoryPoints() {
		return victoryPoints;
	}

	public void setVictoryPoints(int victoryPoints) {
		this.victoryPoints = victoryPoints;
	}

	public String getMapTemplate() {
		return mapTemplate;
	}

	public void setMapTemplate(String mapTemplate) {
		this.mapTemplate = mapTemplate;
	}

	public boolean isRollSeven() {
		return rollSeven;
	}

	public void setRollSeven(boolean rollSeven) {
		this.rollSeven = rollSeven;
	}

	public int getStartingResources() {
		return startingResources;
	}

	public void setStartingResources(int startingResources) {
		this.startingResources = startingResources;
	}
}
