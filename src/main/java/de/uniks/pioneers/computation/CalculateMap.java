package de.uniks.pioneers.computation;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;

public class CalculateMap {

	private Polygon buildHexagon(int x, int y, int z) {
		//creates a Hexagon at the given coordinate
		double hexPoint1 = 0.0;
		double hexPoint2 = 30.0;
		double hexPoint3 = 52.0;
		double hexPoint4 = 60.0;
		double hexPoint5 = -30.0;
		double hexPoint6 = -52.0;
		double hexPoint7 = -60.0;

		Polygon hexagon = new Polygon(
				hexPoint6, hexPoint2,
				hexPoint1, hexPoint4,
				hexPoint3, hexPoint2,
				hexPoint3, hexPoint5,
				hexPoint1, hexPoint7,
				hexPoint6, hexPoint5
		);

		String id = createId(x, y, z);
		hexagon.setId(id);

		return hexagon;
	}

	private void buildCircle(Pane pane, double xCoordinate, double yCoordinate, int x, int y, int z, int side) {
		//creates a Circle at the given coordinate
		Circle circle = new Circle(10);
		String id = createId(x, y, z) + "_" + side;
		circle.setId(id);
		pane.getChildren().add(circle);
		circle.setFill(Color.TRANSPARENT);
		circle.setLayoutX(xCoordinate);
		circle.setLayoutY(yCoordinate);
	}

	private void buildImage(Pane pane, double xCoordinate, double yCoordinate, int x, int y, int z, boolean Harbour) {
		//creates an ImageView at the given coordinate
		ImageView imageView = new ImageView();
		imageView.setFitHeight(40);
		imageView.setFitWidth(40);
		String id;
		if (Harbour) {
			id = createId(x, y, z) + "_HarbourImage";
		} else {
			id = createId(x, y, z) + "_RobberImage";
		}
		imageView.setId(id);
		pane.getChildren().add(imageView);
		imageView.setLayoutX(xCoordinate - 20);
		imageView.setLayoutY(yCoordinate - 20);
	}

	private void buildRoad(Pane pane, double xCoordinate, double yCoordinate, int x, int y, int z, double rotation, boolean harbour) {
		//creates a road at the given coordinate
		double roadPos1 = -20.0;
		double roadPos2 = -5.0;
		double roadPos3 = 5.0;
		double roadPos4 = 20.0;
		double roadPos5 = 25.0;
		double roadPos6 = -25.0;
		double roadPos7 = 0.0;

		Polygon road = new Polygon(
				roadPos2, roadPos1,
				roadPos7, roadPos6,
				roadPos3, roadPos1,
				roadPos3, roadPos4,
				roadPos7, roadPos5,
				roadPos2, roadPos4
		);

		if (!harbour) {
			if (rotation == 0.0) {
				String id = createId(x, y, z) + "_" + 3 + "_Road";
				road.setId(id);
			} else if (rotation == 60.0) {
				String id = createId(x, y, z) + "_" + 11 + "_Road";
				road.setId(id);
			} else if (rotation == 120.0) {
				String id = createId(x, y, z) + "_" + 7 + "_Road";
				road.setId(id);
			}
		}

		// For easier Harbour placement Ids are corresponding to the harbour location id +1 and -1
		// example harbour at pos 9 has roads 8 and 10 visible
		if (harbour) {
			if (rotation == 0.0) {
				String id = createId(x, y, z) + "_" + 6 + "_HarbourRoad";
				road.setId(id);
			} else if (rotation == 60.0) {
				String id = createId(x, y, z) + "_" + 8 + "_HarbourRoad";
				road.setId(id);
			} else if (rotation == 120.0) {
				String id = createId(x, y, z) + "_" + 10 + "_HarbourRoad";
				road.setId(id);
			} else if (rotation == 180.0) {
				String id = createId(x, y, z) + "_" + 0 + "_HarbourRoad";
				road.setId(id);
			} else if (rotation == 240.0) {
				String id = createId(x, y, z) + "_" + 2 + "_HarbourRoad";
				road.setId(id);
			} else if (rotation == 300.0) {
				String id = createId(x, y, z) + "_" + 4 + "_HarbourRoad";
				road.setId(id);
			}
		}

		pane.getChildren().add(road);
		road.setVisible(false);
		road.setStroke(Color.BLACK);
		road.setStrokeWidth(1.0);
		road.setLayoutX(xCoordinate);
		road.setLayoutY(yCoordinate);
		road.setRotate(rotation);
	}

	private void buildWaterTile(Pane pane, double xCoordinate, double yCoordinate, int x, int y, int z, int position) {

		//all cases need the image
		buildImage(pane, xCoordinate, yCoordinate, x, y, z, true);

		switch (position) {
			case 1, 2, 10 -> {
				// for the building
				buildCircle(pane, xCoordinate, yCoordinate + 60, x, y, z, 6);

				//for the road
				buildCircle(pane, xCoordinate - 26, yCoordinate + 45, x, y, z, 7);
				buildRoad(pane, xCoordinate - 26, yCoordinate + 45, x, y, z, 120.0, false);

				//for the harbour
				buildRoad(pane, xCoordinate, yCoordinate + 30, x, y, z, 180.0, true);
				buildRoad(pane, xCoordinate - 26, yCoordinate + 16, x, y, z, 240.0, true);

				if (position == 1) {
					buildRoad(pane, xCoordinate - 26, yCoordinate - 16, x, y, z, 300.0, true);
				} else if (position == 2) {
					buildRoad(pane, xCoordinate + 26, yCoordinate + 16, x, y, z, 120.0, true);
				}
			}
			case 3 -> {
				buildRoad(pane, xCoordinate - 26, yCoordinate + 16, x, y, z, 240.0, true);
				buildRoad(pane, xCoordinate - 26, yCoordinate - 16, x, y, z, 300.0, true);
			}
			case 4, 5, 8 -> {
				// for the building
				buildCircle(pane, xCoordinate, yCoordinate - 60, x, y, z, 0);

				//for the road
				buildCircle(pane, xCoordinate - 26, yCoordinate - 45, x, y, z, 11);
				buildRoad(pane, xCoordinate - 26, yCoordinate - 45, x, y, z, 60.0, false);

				//for the harbour
				buildRoad(pane, xCoordinate, yCoordinate - 30, x, y, z, 0.0, true);
				buildRoad(pane, xCoordinate - 26, yCoordinate - 16, x, y, z, 300.0, true);

				if (position == 4) {
					buildRoad(pane, xCoordinate - 26, yCoordinate + 16, x, y, z, 240.0, true);
				} else if (position == 5) {
					buildRoad(pane, xCoordinate + 26, yCoordinate - 16, x, y, z, 60.0, true);
				}
			}
			case 6 -> {
				// for the building
				buildCircle(pane, xCoordinate, yCoordinate - 60, x, y, z, 6);

				//for the harbour
				buildRoad(pane, xCoordinate, yCoordinate - 30, x, y, z, 0.0, true);
				buildRoad(pane, xCoordinate + 26, yCoordinate - 16, x, y, z, 60.0, true);
			}
			case 7 -> {
				// for the building
				buildCircle(pane, xCoordinate, yCoordinate - 60, x, y, z, 0);

				//for the road
				buildCircle(pane, xCoordinate + 52, yCoordinate, x, y, z, 3);
				buildRoad(pane, xCoordinate + 52, yCoordinate, x, y, z, 0.0, false);

				//for the harbour
				buildRoad(pane, xCoordinate, yCoordinate - 30, x, y, z, 0.0, true);
				buildRoad(pane, xCoordinate + 26, yCoordinate - 16, x, y, z, 60.0, true);
				buildRoad(pane, xCoordinate + 26, yCoordinate + 16, x, y, z, 120.0, true);
			}
			case 9 -> {
				//for the road
				buildCircle(pane, xCoordinate + 52, yCoordinate, x, y, z, 3);
				buildRoad(pane, xCoordinate + 52, yCoordinate, x, y, z, 0.0, false);

				//for the harbour
				buildRoad(pane, xCoordinate + 26, yCoordinate - 16, x, y, z, 60.0, true);
				buildRoad(pane, xCoordinate + 26, yCoordinate + 16, x, y, z, 120.0, true);
			}
			case 11 -> {
				// for the building
				buildCircle(pane, xCoordinate, yCoordinate + 60, x, y, z, 6);

				//for the road
				buildCircle(pane, xCoordinate + 52, yCoordinate, x, y, z, 3);
				buildRoad(pane, xCoordinate + 52, yCoordinate, x, y, z, 0.0, false);

				//for the harbour
				buildRoad(pane, xCoordinate, yCoordinate + 30, x, y, z, 180.0, true);
				buildRoad(pane, xCoordinate + 26, yCoordinate - 16, x, y, z, 60.0, true);
				buildRoad(pane, xCoordinate + 26, yCoordinate + 16, x, y, z, 120.0, true);

			}
			case 12 -> {
				// for the building
				buildCircle(pane, xCoordinate, yCoordinate + 60, x, y, z, 6);

				//for the harbour
				buildRoad(pane, xCoordinate, yCoordinate + 30, x, y, z, 180.0, true);
				buildRoad(pane, xCoordinate + 26, yCoordinate + 16, x, y, z, 120.0, true);
			}
		}
	}

	public Pane buildMap(int size) {

		Pane map = new Pane();
		double paneBaseSize = 350;
		double paneVariableSize = 216;

		//create a map with double the needed size
		double mapSize = (paneBaseSize + paneVariableSize * size) * 2;
		double center;

		//make small maps appear in the center of the scroll pane
		if (mapSize < 1600) {
			map.setMinWidth(1600);
			map.setMinHeight(1600);
			center = 800;

		} else {
			map.setMinWidth(mapSize);
			map.setMinHeight(mapSize);

			center = mapSize / 2;
		}
		int z = size * (-1);

		while (z <= size) {
			int x = size * (-1);
			while (x <= size) {

				int y = -x - z;

				if ((z == 0) || (!(y > size) && !(y < (-size)))) {
					buildAtPosition(map, center, x, y, z);
				}
				x++;
			}
			z++;
		}

		int waterMin = (size + 1) * -1;
		int waterMax = (size + 1);

		//Build the 6 Constant WaterTileCorners
		//top right
		buildWaterTile(map, getxCoordinate(center, waterMax, waterMin), getyCoordinate(center, waterMin), waterMax, 0, waterMin, 10);

		//top left
		buildWaterTile(map, getxCoordinate(center, 0, waterMin), getyCoordinate(center, waterMin), 0, waterMax, waterMin, 12);

		//mid left
		buildWaterTile(map, getxCoordinate(center, waterMin, 0), getyCoordinate(center, 0), waterMin, waterMax, 0, 9);

		//bottom left
		buildWaterTile(map, getxCoordinate(center, waterMin, waterMax), getyCoordinate(center, waterMax), waterMin, 0, waterMax, 6);

		//bottom right
		buildWaterTile(map, getxCoordinate(center, 0, waterMax), getyCoordinate(center, waterMax), 0, waterMin, waterMax, 8);

		//mid right
		buildWaterTile(map, getxCoordinate(center, waterMax, 0), getyCoordinate(center, 0), waterMax, waterMin, 0, 3);

		if (size > 0) {
			// Build the dynamic WaterTiles
			for (int i = 1; i <= size; i++) {
				//bottom left
				buildWaterTile(map, getxCoordinate(center, waterMin, i), getyCoordinate(center, i), waterMin, waterMax - i, i, 7);

				//bottom right
				buildWaterTile(map, getxCoordinate(center, waterMax - i, i), getyCoordinate(center, i), waterMax - i, waterMin, i, 4);

				//top center
				buildWaterTile(map, getxCoordinate(center, i, waterMin), getyCoordinate(center, waterMin), i, waterMax - i, waterMin, 2);

				//top right
				buildWaterTile(map, getxCoordinate(center, waterMax, -i), getyCoordinate(center, -i), waterMax, waterMin + i, -i, 1);

				//top left
				buildWaterTile(map, getxCoordinate(center, waterMin + i, -i), getyCoordinate(center, -i), waterMin + i, waterMax, -i, 11);

				//bottom center
				buildWaterTile(map, getxCoordinate(center, -i, waterMax), getyCoordinate(center, waterMax), -i, waterMin + i, waterMax, 5);
			}
		}
		map.setStyle("-fx-background: transparent; -fx-background-color: transparent; ");
		return map;
	}

	private void buildAtPosition(Pane map, double center, int x, int y, int z) {
		double xCoordinate = getxCoordinate(center, x, z);
		double yCoordinate = getyCoordinate(center, z);

		Polygon hexagon = buildHexagon(x, y, z);
		hexagon.setLayoutX(xCoordinate);
		hexagon.setLayoutY(yCoordinate);
		map.getChildren().add(hexagon);

		// for the buildings
		buildCircle(map, xCoordinate, yCoordinate - 60, x, y, z, 0);
		buildCircle(map, xCoordinate, yCoordinate + 60, x, y, z, 6);

		//for the roads
		buildCircle(map, xCoordinate + 52, yCoordinate, x, y, z, 3);
		buildCircle(map, xCoordinate - 26, yCoordinate + 45, x, y, z, 7);
		buildCircle(map, xCoordinate - 26, yCoordinate - 45, x, y, z, 11);

		//Create Polygons required for the roads
		buildRoad(map, xCoordinate + 52, yCoordinate, x, y, z, 0.0, false);
		buildRoad(map, xCoordinate - 26, yCoordinate + 45, x, y, z, 120.0, false);
		buildRoad(map, xCoordinate - 26, yCoordinate - 45, x, y, z, 60.0, false);

		//Place the image view for robber
		buildImage(map, xCoordinate, yCoordinate, x, y, z, false);

		//Place a Label fot the number
		buildLabel(map, xCoordinate - 12, yCoordinate - 12, x, y, z);
	}

	private void buildLabel(Pane map, double xCoordinate, double yCoordinate, int x, int y, int z) {
		//creates a label at the given coordinate
		Label label = new Label();
		label.setPrefHeight(24.0);
		label.setPrefWidth(24.0);
		map.getChildren().add(label);
		label.setLayoutX(xCoordinate);
		label.setLayoutY(yCoordinate);
		label.setAlignment(Pos.CENTER);
		label.setFont(Font.font(12));
		label.setTextFill(Color.BLACK);
		label.setId(createId(x, y, z) + "_label");
	}


	private double getxCoordinate(double center, int x, int z) {
		int xOffset = 104;
		int halfXOffset = 52;
		return center + (xOffset * x) + (halfXOffset * z);
	}

	private double getyCoordinate(double center, int z) {
		int yOffset = 90;
		return center + (yOffset * z);
	}

	private String createId(int x, int y, int z) {

		String id = "";
		// parse coordinates to ID
		// warnings are false and need to be ignored
		if ((x < 0) && (y < 0) && (z >= 0)) {
			id = "xM" + x * (-1) + "yM" + y * (-1) + "z" + z;
		} else if ((x < 0) && (y >= 0) && (z < 0)) {
			id = "xM" + x * (-1) + "y" + y + "zM" + z * (-1);
		} else if ((x >= 0) && (y < 0) && (z < 0)) {
			id = "x" + x + "yM" + y * (-1) + "zM" + z * (-1);
		} else if (x < 0 && y >= 0) {
			id = "xM" + x * (-1) + "y" + y + "z" + z;
		} else if (x >= 0 && y < 0) {
			id = "x" + x + "yM" + y * (-1) + "z" + z;
		} else if (x >= 0 && z < 0) {
			id = "x" + x + "y" + y + "zM" + z * (-1);
		} else if (x >= 0) {
			id = "x" + x + "y" + y + "z" + z;
		}
		return id;
	}
}
