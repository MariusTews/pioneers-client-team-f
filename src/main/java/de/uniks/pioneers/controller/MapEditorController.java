package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.computation.CalculateMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapEditorController implements Controller {
    @FXML
    public TextField nameTextField;
    @FXML
    public Button mapSizeMinusButton;
    @FXML
    public Label mapSizeLabel;
    @FXML
    public Button mapSizePlusButton;
    @FXML
    public Button saveButton;
    @FXML
    public ScrollPane mapPane;

    private List<Polygon> hexagons = new ArrayList<>();

    @Inject
    public MapEditorController() {
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        // load UI elements
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/MapEditorView.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Pane map = new CalculateMap().buildMap(2, true);

        this.mapPane.setContent(map);

        for (Node node : map.getChildren()) {
            if (node.getId().endsWith("_harborButton")) {
                Button button = (Button) node;
                button.setOnAction(this::harborButtonPressed);
            } else if (node.getId().endsWith("_tileButton")) {
                Button button = (Button) node;
                button.setOnAction(this::tileButtonPressed);
            } else {
                this.hexagons.add((Polygon) node);
            }
        }

        return parent;
    }

    private void tileButtonPressed(ActionEvent event) {
    }

    private void harborButtonPressed(ActionEvent event) {
    }

    public void mapSizeMinusButtonPressed(ActionEvent event) {
    }

    public void mapSizePlusButtonPressed(ActionEvent event) {
    }

    public void saveButtonPressed(ActionEvent event) {
    }
}
