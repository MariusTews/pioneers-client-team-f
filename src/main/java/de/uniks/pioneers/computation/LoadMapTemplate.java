package de.uniks.pioneers.computation;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Harbor;
import de.uniks.pioneers.model.Map;
import de.uniks.pioneers.model.Tile;
import de.uniks.pioneers.service.HexFillService;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.util.Objects;

import static de.uniks.pioneers.Constants.*;


public class LoadMapTemplate {

    private Pane pane;
    private final CalculateMap calculateMap = new CalculateMap();
    private final HexFillService hexFillService = new HexFillService();

    public Pane loadMap(Map map, boolean loadForIngame) {

        int maxRange = 0;

        for (Tile tile : map.tiles()) {
            maxRange = checkRange(tile.x(), tile.y(), tile.z(), maxRange);
        }

		for (Harbor harbor : map.harbors()) {
			 maxRange = checkRange(harbor.x(), harbor.y(), harbor.z(), maxRange);
        }
        this.pane = calculateMap.buildPane(maxRange);
        double center = calculateMap.getCenter();

        for (Tile tile : map.tiles()) {
            int x = tile.x().intValue();
            int y = tile.y().intValue();
            int z = tile.z().intValue();
            double xCoordinate = calculateMap.getxCoordinate(center, x, z);
            double yCoordinate = calculateMap.getyCoordinate(center, z);

            if (loadForIngame) {
                this.pane.getChildren().add(calculateMap.buildHexagon(xCoordinate, yCoordinate, x, y, z, false));
                this.pane.getChildren().add(calculateMap.buildImage(xCoordinate, yCoordinate, x, y, z, false));
                this.pane.getChildren().add(calculateMap.buildLabel(xCoordinate, yCoordinate, x, y, z));

                for (int i = 1; i <= 6; i++) {
                    buildBuildingCircle(i, xCoordinate, yCoordinate, x, y, z);
                    buildRoadCircle(i, xCoordinate, yCoordinate, x, y, z);
                }
            } else {
                Polygon hexagon = calculateMap.buildHexagon(xCoordinate, yCoordinate, x, y, z, false);
                hexFillService.fillHexagon(hexagon, tile.type());
                this.pane.getChildren().add(hexagon);

                Label label = calculateMap.buildLabel(xCoordinate, yCoordinate, x, y, z);
                label.setText("" + tile.numberToken());
                this.pane.getChildren().add(label);
            }
        }

        for (Harbor harbor : map.harbors()) {
            int x = harbor.x().intValue();
            int y = harbor.y().intValue();
            int z = harbor.z().intValue();

            ImageView imageView = null;
            Polygon road1 = null;
            Polygon road2 = null;

            int i = harbor.side().intValue();
            switch (i) {
                case 1 -> {
                    double xCoordinate = calculateMap.getxCoordinate(center, x + 1, z - 1);
                    double yCoordinate = calculateMap.getyCoordinate(center, z - 1);
                    imageView = calculateMap.buildImage(xCoordinate, yCoordinate, x + 1, y, z - 1, true);
                    road1 = calculateMap.buildRoad(xCoordinate, yCoordinate + 30, x + 1, y, z - 1, 180, true);
                    road2 = calculateMap.buildRoad(xCoordinate - 26, yCoordinate + 16, x + 1, y, z - 1, 240, true);
                    this.pane.getChildren().add(imageView);
                    this.pane.getChildren().add(road1);
                    this.pane.getChildren().add(road2);
                }
                case 3 -> {
                    double xCoordinate = calculateMap.getxCoordinate(center, x + 1, z);
                    double yCoordinate = calculateMap.getyCoordinate(center, z);
                    imageView = calculateMap.buildImage(xCoordinate, yCoordinate, x + 1, y - 1, z, true);
                    road1 = calculateMap.buildRoad(xCoordinate - 26, yCoordinate + 16, x + 1, y - 1, z, 240, true);
                    road2 = calculateMap.buildRoad(xCoordinate - 26, yCoordinate - 16, x + 1, y - 1, z, 300, true);
                    this.pane.getChildren().add(imageView);
                    this.pane.getChildren().add(road1);
                    this.pane.getChildren().add(road2);
                }
                case 5 -> {
                    double xCoordinate = calculateMap.getxCoordinate(center, x, z + 1);
                    double yCoordinate = calculateMap.getyCoordinate(center, z + 1);
                    imageView = calculateMap.buildImage(xCoordinate, yCoordinate, x, y - 1, z + 1, true);
                    road1 = calculateMap.buildRoad(xCoordinate - 26, yCoordinate - 16, x, y - 1, z + 1, 300, true);
                    road2 = calculateMap.buildRoad(xCoordinate, yCoordinate - 30, x, y - 1, z + 1, 0, true);
                    this.pane.getChildren().add(imageView);
                    this.pane.getChildren().add(road1);
                    this.pane.getChildren().add(road2);
                }
                case 7 -> {
                    double xCoordinate = calculateMap.getxCoordinate(center, x - 1, z + 1);
                    double yCoordinate = calculateMap.getyCoordinate(center, z + 1);
                    imageView = calculateMap.buildImage(xCoordinate, yCoordinate, x - 1, y, z + 1, true);
                    road1 = calculateMap.buildRoad(xCoordinate, yCoordinate - 30, x - 1, y, z + 1, 0, true);
                    road2 = calculateMap.buildRoad(xCoordinate + 26, yCoordinate - 16, x - 1, y, z + 1, 60, true);
                    this.pane.getChildren().add(imageView);
                    this.pane.getChildren().add(road1);
                    this.pane.getChildren().add(road2);
                }
                case 9 -> {
                    double xCoordinate = calculateMap.getxCoordinate(center, x - 1, z);
                    double yCoordinate = calculateMap.getyCoordinate(center, z);
                    imageView = calculateMap.buildImage(xCoordinate, yCoordinate, x - 1, y + 1, z, true);
                    road1 = calculateMap.buildRoad(xCoordinate + 26, yCoordinate - 16, x - 1, y + 1, z, 60, true);
                    road2 = calculateMap.buildRoad(xCoordinate + 26, yCoordinate + 16, x - 1, y + 1, z, 120, true);
                    this.pane.getChildren().add(imageView);
                    this.pane.getChildren().add(road1);
                    this.pane.getChildren().add(road2);
                }
                case 11 -> {
                    double xCoordinate = calculateMap.getxCoordinate(center, x, z - 1);
                    double yCoordinate = calculateMap.getyCoordinate(center, z - 1);
                    imageView = calculateMap.buildImage(xCoordinate, yCoordinate, x, y + 1, z - 1, true);
                    road1 = calculateMap.buildRoad(xCoordinate + 26, yCoordinate + 16, x, y + 1, z - 1, 120, true);
                    road2 = calculateMap.buildRoad(xCoordinate, yCoordinate + 30, x, y + 1, z - 1, 180, true);
                    this.pane.getChildren().add(imageView);
                    this.pane.getChildren().add(road1);
                    this.pane.getChildren().add(road2);
                }
            }

            if (!loadForIngame && imageView != null && road1 != null && road2 != null) {
                road1.setVisible(true);
                road2.setVisible(true);

                if (harbor.type() != null) {
                    switch (harbor.type()) {
                        case MOON_ROCK ->
                                imageView.setImage(new Image(Objects.requireNonNull(Main.class.getResource("view/assets/moon_rock_harbor.png")).toString()));
                        case NEPTUNE_CRYSTAL ->
                                imageView.setImage(new Image(Objects.requireNonNull(Main.class.getResource("view/assets/neptun_crystals_harbor.png")).toString()));
                        case MARS_BAR ->
                                imageView.setImage(new Image(Objects.requireNonNull(Main.class.getResource("view/assets/mars_bar_harbor.png")).toString()));
                        case VENUS_GRAIN ->
                                imageView.setImage(new Image(Objects.requireNonNull(Main.class.getResource("view/assets/venus_grain_harbor.png")).toString()));
                        case EARTH_CACTUS ->
                                imageView.setImage(new Image(Objects.requireNonNull(Main.class.getResource("view/assets/earth_cactus_harbor.png")).toString()));
                    }
                } else {
                    imageView.setImage(new Image(Objects.requireNonNull(Main.class.getResource("view/assets/harbor.png")).toString()));
                }
            }
        }
        return this.pane;
    }

    public int checkRange(Number a, Number b, Number c, int maxRange) {
        int x = Math.abs(a.intValue());
        int y = Math.abs(b.intValue());
        int z = Math.abs(c.intValue());

        int range = maxRange;

		if (x > maxRange) {
			range = x;
        } else if (y > maxRange) {
            range = y;
        } else if (z > maxRange) {
            range = z;
		}

		return range;
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
                Circle colorCircle = calculateMap.buildColorCircle(xCoordinate, yCoordinate - 60, x, y, z, 0);
                addToPane(colorCircle);
                addToPane(circle);
            }
            // Circle bottom middle
            case 2 -> {
                Circle circle = calculateMap.buildCircle(xCoordinate, yCoordinate + 60, x, y, z, 6);
                Circle colorCircle = calculateMap.buildColorCircle(xCoordinate, yCoordinate + 60, x, y, z, 6);
                addToPane(colorCircle);
                addToPane(circle);
            }
            // Circle top right
            case 3 -> {
                Circle circle = calculateMap.buildCircle(xCoordinate + 52, yCoordinate - 30, x + 1, y, z - 1, 6);
                Circle colorCircle = calculateMap.buildColorCircle(xCoordinate + 52, yCoordinate - 30, x + 1, y, z - 1, 6);
                addToPane(colorCircle);
                addToPane(circle);
            }
            // Circle top left
            case 4 -> {
                Circle circle = calculateMap.buildCircle(xCoordinate - 52, yCoordinate - 30, x, y + 1, z - 1, 6);
                Circle colorCircle = calculateMap.buildColorCircle(xCoordinate - 52, yCoordinate - 30, x, y + 1, z - 1, 6);
                addToPane(colorCircle);
                addToPane(circle);
            }
            // Circle bottom right
            case 5 -> {
                Circle circle = calculateMap.buildCircle(xCoordinate + 52, yCoordinate + 30, x, y - 1, z + 1, 0);
                Circle colorCircle = calculateMap.buildColorCircle(xCoordinate + 52, yCoordinate + 30, x, y - 1, z + 1, 0);
                addToPane(colorCircle);
                addToPane(circle);
            }
            // Circle bottom left
            case 6 -> {
                Circle circle = calculateMap.buildCircle(xCoordinate - 52, yCoordinate + 30, x - 1, y, z + 1, 0);
                Circle colorCircle = calculateMap.buildColorCircle(xCoordinate - 52, yCoordinate + 30, x - 1, y, z + 1, 0);
                addToPane(colorCircle);
                addToPane(circle);
            }
        }
    }
}
