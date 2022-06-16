package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Tile;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import java.io.IOException;
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
        this.view.setOnMouseEntered(this::onFieldMouseHoverEnter);
        this.view.setOnMouseExited(this::onFieldMouseHoverExit);
        setPolygonColor();
    }


    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/GameFieldSubView.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return parent;
    }

    // Mouse hovers over field
    private void onFieldMouseHoverEnter(MouseEvent event) {
        // Change the view
        this.view.setStroke(Color.RED);
    }

    // Mouse leaves the field
    private void onFieldMouseHoverExit(MouseEvent event) {
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
            case "hills" -> {
                Image hills = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/3_moon.png")).toExternalForm());
                ImagePattern hillPattern = new ImagePattern(hills);
                view.setFill(hillPattern);
            }
            case "mountains" -> {
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
