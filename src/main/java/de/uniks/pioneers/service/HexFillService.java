package de.uniks.pioneers.service;

import de.uniks.pioneers.Main;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;

import java.util.Objects;

public class HexFillService {

    //@Inject
    //public HexFillService() {
    //}

    public void fillHexagon(Polygon hexagon, String type) {
        switch (type) {
            case "random" -> {
                hexagon.setFill(Color.grayRgb(200, 0.5));
            }
            case "desert" -> {
                hexagon.setFill(
                        new ImagePattern(
                                new Image(Objects.requireNonNull(
                                        Main.class.getResource("view/assets/2_desert.png")).toExternalForm())
                        ));
            }
            case "venus", "fields" -> {
                hexagon.setFill(
                        new ImagePattern(
                                new Image(Objects.requireNonNull(
                                        Main.class.getResource("view/assets/4_venus.png")).toExternalForm())
                        ));
            }
            case "moon", "mountains" -> {
                hexagon.setFill(
                        new ImagePattern(
                                new Image(Objects.requireNonNull(
                                        Main.class.getResource("view/assets/3_moon.png")).toExternalForm())
                        ));
            }
            case "mars", "hills" -> {
                hexagon.setFill(
                        new ImagePattern(
                                new Image(Objects.requireNonNull(
                                        Main.class.getResource("view/assets/1_mars.png")).toExternalForm())
                        ));
            }
            case "earth", "forest" -> {
                hexagon.setFill(
                        new ImagePattern(
                                new Image(Objects.requireNonNull(
                                        Main.class.getResource("view/assets/6_earth.png")).toExternalForm())
                        ));
            }
            case "neptune", "pasture" -> {
                hexagon.setFill(
                        new ImagePattern(
                                new Image(Objects.requireNonNull(
                                        Main.class.getResource("view/assets/5_neptun.png")).toExternalForm())
                        ));
            }
            default -> {
            }
        }
    }
}
