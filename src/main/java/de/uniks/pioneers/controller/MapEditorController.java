package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.computation.CalculateMap;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @FXML
    public VBox menu;

    private List<Polygon> hexagons = new ArrayList<>();
    private Pane map;

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

        map = new CalculateMap().buildMap(0, true);

        this.mapPane.setContent(map);

        addButtonsOnTiles();
        menu.toFront();
        return parent;
    }

    private void addButtonsOnTiles() {
        for (Node node : map.getChildren()) {
            if (node.getId().endsWith("_harborButton")) {
                Button button = (Button) node;
                button.setOnAction(this::harborButtonPressed);
                button.setStyle("-fx-background-color: #d2dcde; -fx-text-fill: orange;");
            } else if (node.getId().endsWith("_tileButton")) {
                Button button = (Button) node;
                button.setOnAction(this::tileButtonPressed);
                button.setStyle("-fx-background-color: #d2dcde; -fx-text-fill: green;");
            } else {
                this.hexagons.add((Polygon) node);
            }
        }
    }

    private void tileButtonPressed(ActionEvent event) {
        Pattern pattern = Pattern.compile("=(.*?)_");
        Matcher matcher = pattern.matcher(event.getSource().toString());
        Button cancelTileButton = null;
        TextField number = null;
        ChoiceBox choiceBox = null;

        if (matcher.find()) {
            for (Node node : map.getChildren()) {
                // make harbor button invisible
                if (node.getId().equals(matcher.group(1) + "_harborButton")) {
                    Button harborButton = (Button) node;
                    harborButton.setVisible(false);
                }

                if (node.getId().equals(matcher.group(1))) {
                    Polygon hexagon = (Polygon) node;
                    hexagon.setFill(Color.grayRgb(200, 0.5));
                    Button tileButton = (Button) event.getSource();
                    tileButton.setVisible(false);

                    // cancel button
                    cancelTileButton = new Button();
                    cancelTileButton.setPrefWidth(30);
                    cancelTileButton.setPrefHeight(30);
                    cancelTileButton.setText("x");
                    cancelTileButton.setStyle("-fx-text-fill: red");
                    cancelTileButton.setLayoutX(hexagon.getLayoutX() - 21);
                    cancelTileButton.setLayoutY(hexagon.getLayoutY() - 65);
                    cancelTileButton.toFront();
                    cancelTileButton.setOnAction(this::cancelTileButtonPressed);
                    cancelTileButton.setId(hexagon.getId() + "_cancelButton");

                    // number box
                    number = new TextField();
                    number.setPrefWidth(35);
                    number.setPrefHeight(30);
                    number.setText("-");
                    number.setStyle("-fx-text-fill: green");
                    number.setLayoutX(hexagon.getLayoutX() - 18);
                    number.setLayoutY(hexagon.getLayoutY() - 35);
                    number.toFront();
                    number.setId(hexagon.getId() + "_numberField");

                    // choice box
                    choiceBox = new ChoiceBox(FXCollections.observableArrayList(
                            "Random", "desert", "venus", "moon", "mars", "earth", "neptune"
                    ));
                    choiceBox.setPrefWidth(100);
                    choiceBox.setPrefHeight(30);
                    choiceBox.setLayoutX(hexagon.getLayoutX() - 50);
                    choiceBox.setLayoutY(hexagon.getLayoutY() );
                    choiceBox.toFront();
                    choiceBox.setId(hexagon.getId() + "_choiceBox");
                    choiceBox.getSelectionModel().selectFirst();
                }
            }
        }
        if (cancelTileButton != null) {
            map.getChildren().add(cancelTileButton);
            map.getChildren().add(number);
            map.getChildren().add(choiceBox);
        }
    }

    private void harborButtonPressed(ActionEvent event) {

    }

    private void cancelTileButtonPressed(ActionEvent event) {
        Pattern pattern = Pattern.compile("=(.*?)_");
        Matcher matcher = pattern.matcher(event.getSource().toString());
        // elements to remove from map
        Button cancelTileButton = null;
        TextField number = null;
        ChoiceBox choiceBox = null;


        if (matcher.find()) {
            for (Node node : map.getChildren()) {
                if (node.getId().equals(matcher.group(1) + "_cancelButton")) {
                    cancelTileButton = (Button) node;
                }
                if (node.getId().equals(matcher.group(1) + "_numberField")) {
                    number = (TextField) node;
                }
                if (node.getId().equals(matcher.group(1) + "_choiceBox")) {
                    choiceBox = (ChoiceBox) node;
                }
                if (node.getId().equals(matcher.group(1) + "_harborButton")) {
                    Button harborButton = (Button) node;
                    harborButton.setVisible(true);
                }
                if (node.getId().equals(matcher.group(1) + "_tileButton")) {
                    Button tileButton = (Button) node;
                    tileButton.setVisible(true);
                }
                if (node.getId().equals(matcher.group(1))) {
                    Polygon hexagon = (Polygon) node;
                    hexagon.setFill(Color.TRANSPARENT);
                }
            }
        }

        map.getChildren().remove(cancelTileButton);
        map.getChildren().remove(number);
        map.getChildren().remove(choiceBox);
    }

    public void mapSizeMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.mapSizeLabel.getText()) > 0) {
            map = new CalculateMap().buildMap(Integer.parseInt(this.mapSizeLabel.getText()) - 1, true);
            int mapSize = Integer.parseInt(this.mapSizeLabel.getText()) - 1;
            this.mapSizeLabel.setText(String.valueOf(mapSize));
            this.mapPane.setContent(map);
            addButtonsOnTiles();
        }
    }

    public void mapSizePlusButtonPressed(ActionEvent event) {
        map = new CalculateMap().buildMap(Integer.parseInt(this.mapSizeLabel.getText()) + 1, true);
        int mapSize = Integer.parseInt(this.mapSizeLabel.getText()) + 1;
        this.mapSizeLabel.setText(String.valueOf(mapSize));
        this.mapPane.setContent(map);
        addButtonsOnTiles();
    }

    public void saveButtonPressed(ActionEvent event) {
    }

}
