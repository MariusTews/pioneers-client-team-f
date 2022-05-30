package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Tile;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import java.io.IOException;

public class HexSubController implements Controller{

    private Parent parent;
    private App app;
    private Polygon view;
    private Tile tile;

    @Inject
    public HexSubController(App app, Polygon view, Tile tile){
        this.app = app;
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


        this.parent = parent;
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
            case "desert":
                view.setFill(Color.YELLOW);
                break;
            case "fields":
                view.setFill(Color.GREEN);
                break;
            case "hills":
                view.setFill(Color.GREENYELLOW);
                break;
            case "mountains":
                view.setFill(Color.GREY);
                break;
            case "forest":
                view.setFill(Color.ORANGE);
                break;
            case "pasture":
                view.setFill(Color.MAROON);
                break;
        }
    }
}
