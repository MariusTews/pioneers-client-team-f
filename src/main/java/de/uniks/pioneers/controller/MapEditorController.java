package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.service.AchievementsService;
import de.uniks.pioneers.template.HarborTemplate;
import de.uniks.pioneers.template.TileTemplate;
import de.uniks.pioneers.computation.CalculateMap;
import de.uniks.pioneers.service.HexFillService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.uniks.pioneers.Constants.CREATE_MAP;
import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class MapEditorController implements Controller {
    private final App app;
    private final Provider<MapTemplatesScreenController> mapTemplatesScreenController;
    private final AchievementsService achievementsService;
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

    CompositeDisposable disposables;

    // TODO: needed for later use
    private final List<String> choices = new ArrayList<>();
    private final List<TileTemplate> tiles = new ArrayList<>();
    private final List<HarborTemplate> harbors = new ArrayList<>();

    @Inject
    public MapEditorController(App app,
                               Provider<MapTemplatesScreenController> mapTemplatesScreenController,
                               AchievementsService achievementsService) {
        this.app = app;
        this.mapTemplatesScreenController = mapTemplatesScreenController;
        this.achievementsService = achievementsService;
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
        disposables = new CompositeDisposable();
    }

    @Override
    public void destroy() {
        if (disposables != null) {
            disposables.clear();
        }

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
                    cancelTileButton.getStyleClass().add("cancelTileButton");
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
                    numberField.getStyleClass().add("numberField");
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
        HexFillService hexFillService = new HexFillService();
        TextField numberField = new TextField();

        // get positions
        // TODO: needed for later purpose
        List<Integer> pos = hexFillService.parseID(hexagon.getId());

        // the number field needs to be looked up for the desert tile
        for (Node node : map.getChildren()) {
            if (node.getId().equals(hexagon.getId() + "_numberField")) {
                numberField = (TextField) node;
            }
        }

        // the desert has no number field
        numberField.setVisible(!choices.get(newValue).equals("desert"));

        // fill hexagon by choice
        hexFillService.fillHexagon(hexagon, choices.get(newValue));
    }

    private void harborButtonPressed(ActionEvent event) {
        //TODO: implement
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


        //achievement
        achievementsService.init();
        disposables.add(achievementsService.initUserAchievements().observeOn(FX_SCHEDULER).subscribe());
        disposables.add(achievementsService.putOrUpdateAchievement(CREATE_MAP, 1).observeOn(FX_SCHEDULER).subscribe());


        // for temporary use, to get back
        final MapTemplatesScreenController controller = mapTemplatesScreenController.get();
        this.app.show(controller);
    }
}
