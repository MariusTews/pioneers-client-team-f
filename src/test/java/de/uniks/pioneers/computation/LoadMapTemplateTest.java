package de.uniks.pioneers.computation;

import de.uniks.pioneers.model.Harbor;
import de.uniks.pioneers.model.Map;
import de.uniks.pioneers.model.Tile;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;


class LoadMapTemplateTest extends ApplicationTest {

	LoadMapTemplate loadMapTemplate = new LoadMapTemplate();

	List<Tile> createTiles() {
		List<Tile> tiles = new ArrayList<>();
		tiles.add(new Tile(0, 0, 0, "desert", 7));
		tiles.add(new Tile(0, 1, -1, "fields", 3));
		tiles.add(new Tile(1, 0, -1, "hills", 0));
		tiles.add(new Tile(-1, 1, 0, "mountains", 8));
		tiles.add(new Tile(1, 0, -1, "forest", 10));
		tiles.add(new Tile(-1, 0, 1, "pasture", 5));
		tiles.add(new Tile(0, -1, 1, "fields", 2));
		return tiles;
	}

	List<Harbor> createHarbors() {
		List<Harbor> harbors = new ArrayList<>();
		harbors.add(new Harbor(1, 0, -1, "grain", 1));
		harbors.add(new Harbor(1, -1, 0, "brick", 3));
		harbors.add(new Harbor(0, -1, 1, "ore", 5));
		harbors.add(new Harbor(-1, 0, 1, "lumber", 7));
		harbors.add(new Harbor(-1, 1, 0, "wool", 9));
		harbors.add(new Harbor(0, 1, -1, null, 11));
		return harbors;
	}

	@Test
	public void loadForInGame() {
		Map map = new Map("testMap", this.createTiles(), this.createHarbors());
		Pane mapPane = loadMapTemplate.loadMap(map, true);

		Assertions.assertEquals(mapPane.getChildren().size(), 115);

		List<Node> hexagons = new ArrayList<>();
		List<Node> roadCircles = new ArrayList<>();
		List<Node> buildingCircles = new ArrayList<>();
		List<Node> roads = new ArrayList<>();
		List<Node> harborImages = new ArrayList<>();
		List<Node> robberImages = new ArrayList<>();
		List<Node> numberLabels = new ArrayList<>();
		List<Node> harborRoads = new ArrayList<>();

		for (Node node : mapPane.getChildren()) {
			if (node.getId().endsWith("_HarbourRoad")) {
				harborRoads.add(node);
			} else if (node.getId().endsWith("_Road")) {
				roads.add(node);
			} else if (node.getId().endsWith("_3") || node.getId().endsWith("_7") || node.getId().endsWith("_11")) {
				roadCircles.add(node);
			} else if (node.getId().endsWith("_0") || node.getId().endsWith("_6")) {
				buildingCircles.add(node);
			} else if (node.getId().endsWith("_label")) {
				numberLabels.add(node);
			} else if (node.getId().endsWith("_HarbourImage")) {
				harborImages.add(node);
			} else if (node.getId().endsWith("_RobberImage")) {
				robberImages.add(node);
			} else {
				hexagons.add(node);
			}
		}

		Assertions.assertEquals(harborRoads.size(), 12);
		Assertions.assertEquals(roads.size(), 27);
		Assertions.assertEquals(roadCircles.size(), 27);
		Assertions.assertEquals(buildingCircles.size(), 22);
		Assertions.assertEquals(numberLabels.size(), 7);
		Assertions.assertEquals(harborImages.size(), 6);
		Assertions.assertEquals(robberImages.size(), 7);
		Assertions.assertEquals(hexagons.size(), 7);
	}

	@Test
	public void loadNotForInGame() {
		Map map = new Map("testMap", this.createTiles(), this.createHarbors());
		Pane mapPane = loadMapTemplate.loadMap(map, false);

		Assertions.assertEquals(mapPane.getChildren().size(), 32);

		List<Node> hexagons = new ArrayList<>();
		List<Node> harborImages = new ArrayList<>();
		List<Node> numberLabels = new ArrayList<>();
		List<Node> harborRoads = new ArrayList<>();

		for (Node node : mapPane.getChildren()) {
			if (node.getId().endsWith("_HarbourRoad")) {
				harborRoads.add(node);
			} else if (node.getId().endsWith("_label")) {
				numberLabels.add(node);
			} else if (node.getId().endsWith("_HarbourImage")) {
				harborImages.add(node);
			} else {
				hexagons.add(node);
			}
		}

		Assertions.assertEquals(harborRoads.size(), 12);
		Assertions.assertEquals(numberLabels.size(), 7);
		Assertions.assertEquals(harborImages.size(), 6);
		Assertions.assertEquals(hexagons.size(), 7);
	}
}
