package de.uniks.pioneers.service;

import de.uniks.pioneers.Main;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HexFillService {
    public void fillHexagon(Polygon hexagon, String type) {
        switch (type) {
            case "random" -> hexagon.setFill(Color.grayRgb(200, 0.5));
            case "desert" -> hexagon.setFill(
                    new ImagePattern(
                            new Image(Objects.requireNonNull(
                                    Main.class.getResource("view/assets/2_desert.png")).toExternalForm())
                    ));
            case "fields" -> hexagon.setFill(
                    new ImagePattern(
                            new Image(Objects.requireNonNull(
                                    Main.class.getResource("view/assets/4_venus.png")).toExternalForm())
                    ));
            case "mountains" -> hexagon.setFill(
                    new ImagePattern(
                            new Image(Objects.requireNonNull(
                                    Main.class.getResource("view/assets/3_moon.png")).toExternalForm())
                    ));
            case "hills" -> hexagon.setFill(
                    new ImagePattern(
                            new Image(Objects.requireNonNull(
                                    Main.class.getResource("view/assets/1_mars.png")).toExternalForm())
                    ));
            case "forest" -> hexagon.setFill(
                    new ImagePattern(
                            new Image(Objects.requireNonNull(
                                    Main.class.getResource("view/assets/6_earth.png")).toExternalForm())
                    ));
            case "pasture" -> hexagon.setFill(
                    new ImagePattern(
                            new Image(Objects.requireNonNull(
                                    Main.class.getResource("view/assets/5_neptun.png")).toExternalForm())
                    ));
            default -> {
            }
        }
    }

    /*
    * get the position of every coordinate
    * parse and differentiate between positive and negative values
    * add them to list and return
    * */
    public List<Integer> parseID(String id) {
        List<Integer> resultID = new ArrayList<>();

        int posX = id.indexOf("x");
        int posY = id.indexOf("y");
        int posZ = id.indexOf("z");

        String x = "";
        String y = "";
        String z = "";

        if (posX != -1 || posY != -1 || posZ != -1) {
            x = id.substring(posX + 1, posY);
            y = id.substring(posY + 1, posZ);
            z = id.substring(posZ + 1);
        }

        if (x.startsWith("M")) {
            resultID.add(0, (-1) * Integer.parseInt(x.substring(1)));
        } else {
            resultID.add(0, Integer.parseInt(x));
        }

        if (y.startsWith("M")) {
            resultID.add(1, (-1) * Integer.parseInt(y.substring(1)));
        } else {
            resultID.add(1, Integer.parseInt(y));
        }

        if (z.startsWith("M")) {
            resultID.add(2, (-1) * Integer.parseInt(z.substring(1)));
        } else {
            resultID.add(2, Integer.parseInt(z));
        }

        return resultID;
    }
}
