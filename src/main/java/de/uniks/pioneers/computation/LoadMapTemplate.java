package de.uniks.pioneers.computation;

import de.uniks.pioneers.model.Harbor;
import de.uniks.pioneers.model.Map;
import de.uniks.pioneers.model.Tile;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;


public class LoadMapTemplate {

	private int maxRange;
	private Pane pane;

	private final CalculateMap calculateMap = new CalculateMap();

	public Pane loadMap(Map map, boolean loadForIngame) {

		maxRange = 0;

		for (Tile tile : map.tiles()) {
			checkRange(tile.x(), tile.y(), tile.z());
		}

		for (Harbor harbor : map.harbors()) {
			checkRange(harbor.x(), harbor.y(), harbor.z());
		}
		this.pane = calculateMap.buildPane(maxRange);
		double center = calculateMap.getCenter();

		for (Tile tile : map.tiles()) {
			int x = tile.x().intValue();
			int y = tile.y().intValue();
			int z = tile.z().intValue();
			double xCoordinate = calculateMap.getxCoordinate(center, x, z);
			double yCoordinate = calculateMap.getyCoordinate(center, z);

			this.pane.getChildren().add(calculateMap.buildHexagon(xCoordinate, yCoordinate, x, y, z));

			if (loadForIngame) {
				this.pane.getChildren().add(calculateMap.buildImage(xCoordinate, yCoordinate, x, y, z, false));
				this.pane.getChildren().add(calculateMap.buildLabel(xCoordinate, yCoordinate, x, y, z));

				for (int i = 0; i < 6; i++) {
					buildBuildingCircle(i, xCoordinate, yCoordinate, x, y, z);
					buildRoadCircle(i, xCoordinate, yCoordinate, x, y, z);
				}
			}
		}

		for (Harbor harbor : map.harbors()) {
			int x = harbor.x().intValue();
			int y = harbor.y().intValue();
			int z = harbor.z().intValue();

			if (loadForIngame) {
				this.pane.getChildren().add(calculateMap.buildImage(xCoordinate, yCoordinate, x, y, z, true));
				int i = harbor.side().intValue();
				switch (i) {
					case 1 -> {
						this.pane.getChildren().add(calculateMap.buildRoad(xCoordinate, yCoordinate + 30, x, y, z, 180, true));
						this.pane.getChildren().add(calculateMap.buildRoad(xCoordinate - 26, yCoordinate + 16, x, y, z, 240, true));
					}
					case 3 -> {
						this.pane.getChildren().add(calculateMap.buildRoad(xCoordinate - 26, yCoordinate + 16, x, y, z, 240, true));
						this.pane.getChildren().add(calculateMap.buildRoad(xCoordinate - 26, yCoordinate - 16, x, y, z, 300, true));
					}
					case 5 -> {
						this.pane.getChildren().add(calculateMap.buildRoad(xCoordinate - 26, yCoordinate - 16, x, y, z, 300, true));
						this.pane.getChildren().add(calculateMap.buildRoad(xCoordinate, yCoordinate - 30, x, y, z, 0, true));
					}
					case 7 -> {
						this.pane.getChildren().add(calculateMap.buildRoad(xCoordinate, yCoordinate - 30, x, y, z, 0, true));
						this.pane.getChildren().add(calculateMap.buildRoad(xCoordinate + 26, yCoordinate - 16, x, y, z, 60, true));
					}
					case 9 -> {
						this.pane.getChildren().add(calculateMap.buildRoad(xCoordinate + 26, yCoordinate - 16, x, y, z, 60, true));
						this.pane.getChildren().add(calculateMap.buildRoad(xCoordinate + 26, yCoordinate + 16, x, y, z, 120, true));
					}
					case 11 -> {
						this.pane.getChildren().add(calculateMap.buildRoad(xCoordinate + 26, yCoordinate + 16, x, y, z, 120, true));
						this.pane.getChildren().add(calculateMap.buildRoad(xCoordinate, yCoordinate + 30, x, y, z, 180, true));
					}
				}
			}
		}
		return this.pane;
	}

	private void checkRange(Number a, Number b, Number c) {
		int x = Math.abs(a.intValue());
		int y = Math.abs(b.intValue());
		int z = Math.abs(c.intValue());

		if (x > maxRange) {
			maxRange = x;
		} else if (y > maxRange) {
			maxRange = y;
		} else if (z > maxRange) {
			maxRange = z;
		}
	}

	private void buildRoadCircle(int i, double xCoordinate, double yCoordinate, int x, int y, int z) {
		switch (i) {
			// top left
			case 1 -> {
				Circle circle = calculateMap.buildCircle(xCoordinate - 26, yCoordinate - 45, x, y, z, 11);
				addToPane(circle);
				Polygon road = calculateMap.buildRoad(xCoordinate - 26, yCoordinate - 45, x, y, z, 60, false);
				addToPane(road);
			}
			// top right
			case 2 -> {
				Circle circle = calculateMap.buildCircle(xCoordinate + 26, yCoordinate - 45, x + 1, y, z - 1, 7);
				addToPane(circle);
				Polygon road = calculateMap.buildRoad(xCoordinate + 26, yCoordinate - 45, x + 1, y, z - 1, 120, false);
				addToPane(road);
			}
			// mid left
			case 3 -> {
				Circle circle = calculateMap.buildCircle(xCoordinate - 52, yCoordinate, x - 1, y + 1, z, 3);
				addToPane(circle);
				Polygon road = calculateMap.buildRoad(xCoordinate - 52, yCoordinate, x - 1, y + 1, z, 0, false);
				addToPane(road);
			}
			// mid right
			case 4 -> {
				Circle circle = calculateMap.buildCircle(xCoordinate + 52, yCoordinate, x, y, z, 3);
				addToPane(circle);
				Polygon road = calculateMap.buildRoad(xCoordinate + 52, yCoordinate, x, y, z, 0, false);
				addToPane(road);
			}
			// bottom left
			case 5 -> {
				Circle circle = calculateMap.buildCircle(xCoordinate - 26, yCoordinate + 45, x, y, z, 7);
				addToPane(circle);
				Polygon road = calculateMap.buildRoad(xCoordinate - 26, yCoordinate + 45, x, y, z, 120, false);
				addToPane(road);
			}
			// bottom right
			case 6 -> {
				Circle circle = calculateMap.buildCircle(xCoordinate + 26, yCoordinate + 45, x, y - 1, z + 1, 11);
				addToPane(circle);
				Polygon road = calculateMap.buildRoad(xCoordinate + 26, yCoordinate + 45, x, y - 1, z + 1, 60, false);
				addToPane(road);
			}
		}
	}

	private void addToPane(Node toAdd) {
		boolean contains = false;
		for (Node node : this.pane.getChildren()) {
			if (node.getId().equals(toAdd.getId())) {
				contains = true;
			}

		}
		if (!contains) {
			this.pane.getChildren().add(toAdd);
		}
	}

	private void buildBuildingCircle(int i, double xCoordinate, double yCoordinate, int x, int y, int z) {
		switch (i) {
			// Circle top middle
			case 1 -> {
				Circle circle = calculateMap.buildCircle(xCoordinate, yCoordinate - 60, x, y, z, 0);
				addToPane(circle);
			}
			// Circle bottom middle
			case 2 -> {
				Circle circle = calculateMap.buildCircle(xCoordinate, yCoordinate + 60, x, y, z, 6);
				addToPane(circle);
			}
			// Circle top right
			case 3 -> {
				Circle circle = calculateMap.buildCircle(xCoordinate + 52, yCoordinate - 30, x + 1, y, z - 1, 6);
				addToPane(circle);
			}
			// Circle top left
			case 4 -> {
				Circle circle = calculateMap.buildCircle(xCoordinate - 52, yCoordinate - 30, x, y + 1, z - 1, 6);
				addToPane(circle);
			}
			// Circle bottom right
			case 5 -> {
				Circle circle = calculateMap.buildCircle(xCoordinate + 52, yCoordinate + 30, x, y - 1, z + 1, 0);
				addToPane(circle);
			}
			// Circle bottom left
			case 6 -> {
				Circle circle = calculateMap.buildCircle(xCoordinate - 52, yCoordinate + 30, x - 1, y, z + 1, 0);
				addToPane(circle);
			}
		}
	}
}
