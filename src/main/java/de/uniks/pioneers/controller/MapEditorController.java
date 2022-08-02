package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.service.AlertService;
import de.uniks.pioneers.template.HarborTemplate;
import de.uniks.pioneers.template.TileTemplate;
import de.uniks.pioneers.computation.CalculateMap;
import de.uniks.pioneers.service.HexFillService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapEditorController implements Controller {
    private final App app;
    private final Provider<MapTemplatesScreenController> mapTemplatesScreenController;
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

    private final HexFillService hexFillService = new HexFillService();
    private final List<String> choices = new ArrayList<>();
    private List<TileTemplate> tiles = new ArrayList<>();
    private List<HarborTemplate> harbors = new ArrayList<>();

    @Inject
    public MapEditorController(App app,
                               Provider<MapTemplatesScreenController> mapTemplatesScreenController) {
        this.app = app;
        this.mapTemplatesScreenController = mapTemplatesScreenController;
    }

    @Override
    public void init() {
        // init
        choices.add(0, "random");
        choices.add(1, "desert");
        choices.add(2, "fields");
        choices.add(3, "mountains");
        choices.add(4, "hills");
        choices.add(5, "forest");
        choices.add(6, "pasture");
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
            } else if (node.getId().endsWith("_tileButton")) {
                Button button = (Button) node;
                button.setOnAction(this::tileButtonPressed);
            }
        }
    }

    private Button initCancelButton(Polygon hexagon) {
        Button cancelTileButton = new Button();
        cancelTileButton.setPrefWidth(30);
        cancelTileButton.setPrefHeight(30);
        cancelTileButton.setText("x");
        cancelTileButton.getStyleClass().add("cancelTileButton");
        cancelTileButton.setLayoutX(hexagon.getLayoutX() - 21);
        cancelTileButton.setLayoutY(hexagon.getLayoutY() - 65);
        cancelTileButton.toFront();
        cancelTileButton.setOnAction(this::cancelTileButtonPressed);
        cancelTileButton.setId(hexagon.getId() + "_cancelButton");

        return cancelTileButton;
    }

    private void tileButtonPressed(ActionEvent event) {
        // regex to filter the substring with the id from the event
        Pattern pattern = Pattern.compile("=(.*?)_");
        Matcher matcher = pattern.matcher(event.getSource().toString());
        Button cancelTileButton = null;
        TextField numberField = null;
        ChoiceBox<String> choiceBox = null;

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
                    Button tileButton = (Button) event.getSource();
                    tileButton.setVisible(false);

                    // cancel button
                    cancelTileButton = initCancelButton(hexagon);

                    // number box
                    numberField = new TextField();
                    numberField.setPrefWidth(35);
                    numberField.setPrefHeight(30);
                    numberField.setAlignment(Pos.CENTER);
                    numberField.setPromptText("-");
                    numberField.getStyleClass().add("numberField");
                    numberField.setLayoutX(hexagon.getLayoutX() - 18);
                    numberField.setLayoutY(hexagon.getLayoutY() - 35);
                    numberField.toFront();
                    numberField.setId(hexagon.getId() + "_numberField");

                    // choice box
                    choiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
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

    // checks the number token
    private boolean isNumeric(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void selectedChoice(Polygon hexagon, int newValue) {
        TextField numberField = new TextField();
        TileTemplate tileTemplate;
        boolean numberValidFlag = false;

        // get positions
        List<Integer> pos = hexFillService.parseID(hexagon.getId());
        int x = pos.get(0);
        int y = pos.get(1);
        int z = pos.get(2);

        // the number field needs to be looked up for the desert tile
        for (Node node : map.getChildren()) {
            if (node.getId().equals(hexagon.getId() + "_numberField")) {
                numberField = (TextField) node;
                if (isNumeric(numberField.getText())) {
                    if (Integer.parseInt(numberField.getText()) > 1 && Integer.parseInt(numberField.getText()) < 13) {
                        numberValidFlag = true;
                    }
                }
            }
        }

        // the desert has no number field
        numberField.setVisible(!choices.get(newValue).equals("desert"));

        // fill hexagon by choice
        hexFillService.fillHexagon(hexagon, choices.get(newValue));

        // create tile template
        if (choices.get(newValue).equals("random")) {
            if (numberValidFlag) {
                tileTemplate = new TileTemplate(x, y, z, null, Integer.parseInt(numberField.getText()));
            } else {
                tileTemplate = new TileTemplate(x, y, z, null, 0);
            }
        } else {
            if (numberValidFlag) {
                tileTemplate = new TileTemplate(x, y, z, choices.get(newValue), Integer.parseInt(numberField.getText()));
            } else {
                tileTemplate = new TileTemplate(x, y, z, choices.get(newValue), 0);
            }
        }

        // set tile template if it does not already exist
        tiles.removeIf(tile -> tile.x().intValue() == x && tile.y().intValue() == y && tile.z().intValue() == z);

        tiles.add(tileTemplate);

        System.out.println(tiles);
    }

    private void harborButtonPressed(ActionEvent event) {
        // regex to filter the substring with the id from the event
        Pattern pattern = Pattern.compile("=(.*?)_");
        Matcher matcher = pattern.matcher(event.getSource().toString());
        Button cancelTileButton = null;
        ChoiceBox<String> chooseResource = null;
        ChoiceBox<String> chooseSide = null;
        ImageView imageView = new ImageView();
        boolean flag = true;

        if (matcher.find()) {
            for (Node node : map.getChildren()) {
                // make buttons invisible
                if (node.getId().equals(matcher.group(1) + "_tileButton")) {
                    Button tileButton = (Button) node;
                    if (flag) {
                        tileButton.setVisible(false);
                        Button harborButton = (Button) event.getSource();
                        harborButton.setVisible(false);

                    }
                }


                if (node.getId().equals(matcher.group(1))) {
                    assert node instanceof Polygon;
                    Polygon hexagon = (Polygon) node;
                    hexagon.setFill(Color.TRANSPARENT);

                    imageView = new ImageView();
                    Image image = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/harbor.png")).toString());
                    imageView.setImage(image);
                    imageView.setLayoutX(hexagon.getLayoutX() - 17);
                    imageView.setLayoutY(hexagon.getLayoutY() - 38);
                    imageView.toFront();
                    imageView.setFitWidth(25);
                    imageView.setFitHeight(25);
                    imageView.setId(hexagon.getId() + "_imageView");

                    cancelTileButton = initCancelButton(hexagon);

                    chooseResource = new ChoiceBox<>(FXCollections.observableArrayList(
                            "3:1", "mars_bar", "moon_rock", "earth_cactus", "venus_grain", "neptune_crystals"
                    ));

                    chooseResource.getSelectionModel().selectedIndexProperty().addListener(
                            (observable, oldValue, newValue) -> selectedResource(hexagon, newValue.intValue())
                    );

                    chooseResource.setPrefWidth(100);
                    chooseResource.setPrefHeight(20);
                    chooseResource.setLayoutX(hexagon.getLayoutX() - 50);
                    chooseResource.setLayoutY(hexagon.getLayoutY() - 16);
                    chooseResource.toFront();
                    chooseResource.setId(hexagon.getId() + "_chooseResource");
                    chooseResource.getSelectionModel().selectFirst();

                    chooseSide = new ChoiceBox<>(FXCollections.observableArrayList());


                    // check, if around the harbor is a normal tile and add option to choice box
                    if (!initSides(chooseSide, hexagon)) {
                        flag = false;
                        hexagon.setFill(Color.grayRgb(100, 0.5));
                        new AlertService().showAlert("You can only place a harbor next to a tile with a terrain!");
                    } else {
                        chooseSide.setPrefWidth(100);
                        chooseSide.setPrefHeight(20);
                        chooseSide.setLayoutX(hexagon.getLayoutX() - 50);
                        chooseSide.setLayoutY(hexagon.getLayoutY() + 11);
                        chooseSide.toFront();
                        chooseSide.setId(hexagon.getId() + "_chooseSide");
                        chooseSide.getSelectionModel().selectFirst();

                        chooseSide.getSelectionModel().selectedIndexProperty().addListener(
                                (observable, oldValue, newValue) -> selectedResource(hexagon, newValue.intValue())
                        );
                    }
                }
            }
        }

        if (flag) {
            map.getChildren().add(imageView);
            map.getChildren().add(cancelTileButton);
            map.getChildren().add(chooseResource);
            map.getChildren().add(chooseSide);
        }
    }

    private boolean initSides(ChoiceBox<String> chooseSide, Polygon hexagon) {
        HashMap<Integer, Boolean> sideHash = new HashMap<>();
        List<Integer> pos = hexFillService.parseID(hexagon.getId());
        int x = pos.get(0);
        int y = pos.get(1);
        int z = pos.get(2);

        // change the coordinates of the harbor to the neighbor tile and check, if such exists
        sideHash.put(1, checkSides(x + 1, y, z - 1));
        sideHash.put(3, checkSides(x + 1, y - 1, z));
        sideHash.put(5, checkSides(x, y - 1, z + 1));
        sideHash.put(7, checkSides(x - 1, y, z + 1));
        sideHash.put(9, checkSides(x - 1, y + 1, z));
        sideHash.put(11, checkSides(x, y + 1, z - 1));

        for (Integer key : sideHash.keySet()) {
            if (sideHash.get(key)) {
                switch (key) {
                    case 1 -> chooseSide.getItems().add("Top_right");
                    case 3 -> chooseSide.getItems().add("Middle_right");
                    case 5 -> chooseSide.getItems().add("Bottom_right");
                    case 7 -> chooseSide.getItems().add("Bottom_left");
                    case 9 -> chooseSide.getItems().add("Middle_left");
                    case 11 -> chooseSide.getItems().add("Top_left");
                    default -> {
                    }
                }
            }
        }

        return !chooseSide.getItems().isEmpty();
    }

    private boolean checkSides(int x, int y, int z) {
        for (TileTemplate tile : tiles) {
            if (tile.x().intValue() == x && tile.y().intValue() == y && tile.z().intValue() == z) {
                return true;
            }
        }
        return false;
    }

    private void selectedResource(Polygon hexagon, int newValue) {

    }

    private void cancelTileButtonPressed(ActionEvent event) {
        Pattern pattern = Pattern.compile("=(.*?)_");
        Matcher matcher = pattern.matcher(event.getSource().toString());
        List<Integer> pos;
        int x;
        int y;
        int z;

        // dummy elements to remove from map
        Button cancelTileButton = null;
        TextField number = null;
        ChoiceBox<String> choiceBox = null;
        ImageView imageView = null;
        ChoiceBox<String> chooseResource = null;
        ChoiceBox<String> chooseSide = null;

        // reset the tile
        if (matcher.find()) {
            // get id to remove from tile template list
            pos = hexFillService.parseID(matcher.group(1));
            x = pos.get(0);
            y = pos.get(1);
            z = pos.get(2);

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
                if (node.getId().equals(matcher.group(1) + "_imageView")) {
                    assert node instanceof ImageView;
                    imageView = (ImageView) node;
                }
                if (node.getId().equals(matcher.group(1) + "_chooseResource")) {
                    chooseResource = (ChoiceBox) node;
                }
                if (node.getId().equals(matcher.group(1) + "_chooseSide")) {
                    chooseSide = (ChoiceBox) node;
                }
                if (node.getId().equals(matcher.group(1))) {
                    assert node instanceof Polygon;
                    Polygon hexagon = (Polygon) node;
                    hexagon.setFill(Color.grayRgb(100, 0.5));
                }
            }

            map.getChildren().remove(cancelTileButton);
            map.getChildren().remove(number);
            map.getChildren().remove(choiceBox);
            map.getChildren().remove(imageView);
            map.getChildren().remove(chooseResource);
            map.getChildren().remove(chooseSide);

            // remove tile from list
            tiles.removeIf(tile -> tile.x().intValue() == x && tile.y().intValue() == y && tile.z().intValue() == z);
        }

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
            tiles.clear();
        }
    }

    public void mapSizePlusButtonPressed(ActionEvent event) {
        map = new CalculateMap().buildMap(Integer.parseInt(this.mapSizeLabel.getText()) + 1, true);
        int mapSize = Integer.parseInt(this.mapSizeLabel.getText()) + 1;
        this.mapSizeLabel.setText(String.valueOf(mapSize));
        this.mapPane.setContent(map);
        addButtonsOnTiles();
        tiles.clear();
    }

    public void saveButtonPressed(ActionEvent event) {
        //TODO: pressing the save button creates a new map template

        // for temporary use, to get back
        final MapTemplatesScreenController controller = mapTemplatesScreenController.get();
        this.app.show(controller);
    }
}
