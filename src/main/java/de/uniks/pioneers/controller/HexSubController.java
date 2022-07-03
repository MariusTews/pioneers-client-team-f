package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Tile;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import java.util.Objects;

public class HexSubController implements Controller {

    private final Polygon view;
    private final Tile tile;

    @Inject
    public HexSubController(Polygon view, Tile tile) {
        this.view = view;
        this.tile = tile;
    }

    @Override
    public void init() {
        // Add mouse listeners
        this.view.setOnMouseEntered(event -> onFieldMouseHoverEnter());
        this.view.setOnMouseExited(event -> onFieldMouseHoverExit());
        setPolygonColor();
    }


    @Override
    public void destroy() {
    }

    @Override
    public Parent render() {
        //this method needs to be pressed because this subController implements controller therefore the method is empty
        return null;
    }

    // Mouse hovers over field
    private void onFieldMouseHoverEnter() {
        // Change the view
        this.view.setStroke(Color.RED);
    }

    // Mouse leaves the field
    private void onFieldMouseHoverExit() {
        // Change the view
        this.view.setStroke(Color.BLACK);
    }

    private void setPolygonColor() {
        String type = tile.type();
        switch (type) {
            case "desert" -> {
                Image desert = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/2_desert.png")).toExternalForm());
                ImagePattern desertPattern = new ImagePattern(desert);
                view.setFill(desertPattern);
            }
            case "fields" -> {
                Image fields = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/4_venus.png")).toExternalForm());
                ImagePattern fieldPattern = new ImagePattern(fields);
                view.setFill(fieldPattern);
            }
            case "mountains" -> {
                Image hills = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/3_moon.png")).toExternalForm());
                ImagePattern hillPattern = new ImagePattern(hills);
                view.setFill(hillPattern);
            }
            case "hills" -> {
                Image mountains = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/1_mars.png")).toExternalForm());
                ImagePattern mountainPattern = new ImagePattern(mountains);
                view.setFill(mountainPattern);
            }
            case "forest" -> {
                Image forest = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/6_earth.png")).toExternalForm());
                ImagePattern forestPattern = new ImagePattern(forest);
                view.setFill(forestPattern);
            }
            case "pasture" -> {
                Image pasture = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/5_neptun.png")).toExternalForm());
                ImagePattern pasturePattern = new ImagePattern(pasture);
                view.setFill(pasturePattern);
            }
        }
    }
}
