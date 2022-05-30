package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.inject.Inject;
import java.io.IOException;

public class CircleSubController implements Controller{

    private Parent parent;
    private App app;
    private Circle view;

    @Inject
    public CircleSubController(App app, Circle view){
        this.app = app;
        this.view = view;
    }

    @Override
    public void init() {
        // Add mouse listeners
        this.view.setOnMouseEntered(this::onFieldMouseHoverEnter);
        this.view.setOnMouseExited(this::onFieldMouseHoverExit);
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
            this.view.setFill(Color.GRAY);
            this.view.setRadius(10.0);
    }

    // Mouse leaves the field
    private void onFieldMouseHoverExit(MouseEvent event) {
        // Change the view
            this.view.setFill(Color.WHITE);
            this.view.setRadius(10.0);
    }
}