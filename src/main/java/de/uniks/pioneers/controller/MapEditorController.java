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

        // load initial transparent map with size zero
        map = new CalculateMap().buildMap(0, true);
        this.mapPane.setContent(map);
        addButtonsOnTiles();

        // place the menu to front, so the hexagons don't overlay the buttons
        menu.toFront();

        return parent;
    }

    private void addButtonsOnTiles() {
        // look for the buttons with matching id's and initialize
        for (Node node : map.getChildren()) {
            if (node.getId().endsWith("_harborButton")) {
                Button button = (Button) node;
                button.setOnAction(this::harborButtonPressed);
                button.setStyle("-fx-background-color: #d2dcde; -fx-text-fill: orange;");
            } else if (node.getId().endsWith("_tileButton")) {
                Button button = (Button) node;
                button.setOnAction(this::tileButtonPressed);
                button.setStyle("-fx-background-color: #d2dcde; -fx-text-fill: green;");
            }
        }
    }

    private void tileButtonPressed(ActionEvent event) {
        // regex to filter the substring with the id from the event
        Pattern pattern = Pattern.compile("=(.*?)_");
        Matcher matcher = pattern.matcher(event.getSource().toString());
        Button cancelTileButton = null;
        TextField numberField = null;
        ChoiceBox choiceBox = null;

        if (matcher.find()) {
            for (Node node : map.getChildren()) {
                // make harbor button invisible
                if (node.getId().equals(matcher.group(1) + "_harborButton")) {
                    Button harborButton = (Button) node;
                    harborButton.setVisible(false);
                }

                // look for the matching hexagon
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
                    numberField = new TextField();
                    numberField.setPrefWidth(35);
                    numberField.setPrefHeight(30);
                    numberField.setText("-");
                    numberField.setStyle("-fx-text-fill: green");
                    numberField.setLayoutX(hexagon.getLayoutX() - 18);
                    numberField.setLayoutY(hexagon.getLayoutY() - 35);
                    numberField.toFront();
                    numberField.setId(hexagon.getId() + "_numberField");

                    // choice box
                    choiceBox = new ChoiceBox(FXCollections.observableArrayList(
                            "random", "desert", "venus", "moon", "mars", "earth", "neptune"
                    ));

                    choiceBox.getSelectionModel().selectedIndexProperty().addListener(
                            (observable, oldValue, newValue) -> selectedChoice(hexagon, newValue.intValue())
                    );

                    choiceBox.setPrefWidth(100);
                    choiceBox.setPrefHeight(30);
                    choiceBox.setLayoutX(hexagon.getLayoutX() - 50);
                    choiceBox.setLayoutY(hexagon.getLayoutY());
                    choiceBox.toFront();
                    choiceBox.setId(hexagon.getId() + "_choiceBox");
                    choiceBox.getSelectionModel().selectFirst();
                }
            }
        }
        // add the objects to the map
        if (cancelTileButton != null) {
            map.getChildren().add(cancelTileButton);
            map.getChildren().add(numberField);
            map.getChildren().add(choiceBox);
        }
    }

    private void selectedChoice(Polygon hexagon, int newValue) {
        final List<String> choices = new ArrayList<>();
        choices.add(0, "random");
        choices.add(1, "desert");
        choices.add(2, "venus");
        choices.add(3, "moon");
        choices.add(4, "mars");
        choices.add(5, "earth");
        choices.add(6, "neptune");

        TextField numberField = new TextField();

        // the number field needs to be looked up for the desert tile
        for (Node node : map.getChildren()) {
            if (node.getId().equals(hexagon.getId() + "_numberField")) {
                numberField = (TextField) node;
            }
        }

        // initialize the choosen tile
        switch (choices.get(newValue)) {
            case "random" -> {
                numberField.setVisible(true);
                hexagon.setFill(Color.grayRgb(200, 0.5));
            }
            case "desert" -> {
                numberField.setVisible(false);
                hexagon.setFill(
                        new ImagePattern(
                                new Image(Objects.requireNonNull(
                                        Main.class.getResource("view/assets/2_desert.png")).toExternalForm())
                        ));
            }
            case "venus" -> {
                numberField.setVisible(true);
                hexagon.setFill(
                        new ImagePattern(
                                new Image(Objects.requireNonNull(
                                        Main.class.getResource("view/assets/4_venus.png")).toExternalForm())
                        ));
            }
            case "moon" -> {
                numberField.setVisible(true);
                hexagon.setFill(
                        new ImagePattern(
                                new Image(Objects.requireNonNull(
                                        Main.class.getResource("view/assets/3_moon.png")).toExternalForm())
                        ));
            }
            case "mars" -> {
                numberField.setVisible(true);
                hexagon.setFill(
                        new ImagePattern(
                                new Image(Objects.requireNonNull(
                                        Main.class.getResource("view/assets/1_mars.png")).toExternalForm())
                        ));
            }
            case "earth" -> {
                numberField.setVisible(true);
                hexagon.setFill(
                        new ImagePattern(
                                new Image(Objects.requireNonNull(
                                        Main.class.getResource("view/assets/6_earth.png")).toExternalForm())
                        ));
            }
            case "neptune" -> {
                numberField.setVisible(true);
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

    private void harborButtonPressed(ActionEvent event) {
        //TODO: implement the placing of harbors
    }

    private void cancelTileButtonPressed(ActionEvent event) {
        Pattern pattern = Pattern.compile("=(.*?)_");
        Matcher matcher = pattern.matcher(event.getSource().toString());

        // dummy elements to remove from map
        Button cancelTileButton = null;
        TextField number = null;
        ChoiceBox choiceBox = null;

        // reset the tile
        if (matcher.find()) {
            for (Node node : map.getChildren()) {
                if (node.getId().equals(matcher.group(1) + "_cancelButton")) {
                    cancelTileButton = (Button) node;
                }
                if (node.getId().equals(matcher.group(1) + "_numberField")) {
                    assert node instanceof TextField;
                    number = (TextField) node;
                }
                if (node.getId().equals(matcher.group(1) + "_choiceBox")) {
                    assert node instanceof ChoiceBox;
                    choiceBox = (ChoiceBox) node;
                }
                if (node.getId().equals(matcher.group(1) + "_harborButton")) {
                    assert node instanceof Button;
                    Button harborButton = (Button) node;
                    harborButton.setVisible(true);
                }
                if (node.getId().equals(matcher.group(1) + "_tileButton")) {
                    assert node instanceof Button;
                    Button tileButton = (Button) node;
                    tileButton.setVisible(true);
                }
                if (node.getId().equals(matcher.group(1))) {
                    assert node instanceof Polygon;
                    Polygon hexagon = (Polygon) node;
                    hexagon.setFill(Color.TRANSPARENT);
                }
            }
        }

        map.getChildren().remove(cancelTileButton);
        map.getChildren().remove(number);
        map.getChildren().remove(choiceBox);
    }

    /*
    * increase or decrease the map size and place the buttons
    * */
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
        //TODO: pressing the save button creates a new map template
    }

}
