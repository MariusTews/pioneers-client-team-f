package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.service.PioneersService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static de.uniks.pioneers.Constants.*;

public class DiscardResourcesController implements Controller {
    @FXML
    public Button incResource1Btn;
    @FXML
    public Button incResource2Btn;
    @FXML
    public Button incResource3Btn;
    @FXML
    public Button incResource4Btn;
    @FXML
    public Button incResource5Btn;
    @FXML
    public Button decResource1Btn;
    @FXML
    public Button decResource2Btn;
    @FXML
    public Button decResource3Btn;
    @FXML
    public Button decResource4Btn;
    @FXML
    public Button decResource5Btn;
    @FXML
    public Button discardButton;
    @FXML
    public Label amountEarthCactus;
    @FXML
    public Label amountMarsBar;
    @FXML
    public Label amountMoonRock;
    @FXML
    public Label amountNeptuneCrystal;
    @FXML
    public Label amountVenusGrain;
    private final List<Label> allLabels = new ArrayList<>();
    private final HashMap<String, Integer> resources = new HashMap<>();
    private final PioneersService pioneersService;
    private final int amountToDiscard;
    private final String gameID;
    private final Player player;
    private final Window owner;
    private Stage primaryStage;

    @Inject
    public DiscardResourcesController(Player player, String gameID, PioneersService pioneersService, Window owner) {
        this.player = player;
        this.gameID = gameID;
        this.pioneersService = pioneersService;
        this.owner = owner;
        int amountResources = 0;

        // Compute the number of resources which have to be dropped
        for (int amount : this.player.resources().values()) {
            amountResources += amount;
        }
        this.amountToDiscard = amountResources / 2;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/DiscardResourcesView.fxml"));
        loader.setControllerFactory(c -> this);
        Parent root;
        try {
            root = loader.load();
            this.primaryStage = new Stage();
            // TODO: Set to UNDECORATED or TRANSPARENT (without white background) to remove minimize, maximize AND close button of stage
            primaryStage.initStyle(StageStyle.UTILITY);
            Scene scene = new Scene(root, 200, 350);
            scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/DiscardResourcesStyle.css")).toString());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Drop");
            // Specify modality of the new window: interactions are only possible on the second window
            primaryStage.initModality(Modality.WINDOW_MODAL);
            primaryStage.initOwner(this.owner);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        this.discardButton.setText("Discard 0/" + amountToDiscard);
        this.discardButton.setDisable(true);
        this.decResource1Btn.setDisable(true);
        this.decResource2Btn.setDisable(true);
        this.decResource3Btn.setDisable(true);
        this.decResource4Btn.setDisable(true);
        this.decResource5Btn.setDisable(true);


        // Create HashMap because of possible NullPointerExceptions and disable increase button if player does not
        // have the particular resource
        if (player.resources().get(EARTH_CACTUS) != null) {
            this.resources.put(EARTH_CACTUS, player.resources().get(EARTH_CACTUS));
        } else {
            this.resources.put(EARTH_CACTUS, 0);
            incResource1Btn.setDisable(true);
        }

        if (player.resources().get(MARS_BAR) != null) {
            this.resources.put(MARS_BAR, player.resources().get(MARS_BAR));
        } else {
            this.resources.put(MARS_BAR, 0);
            incResource2Btn.setDisable(true);
        }

        if (player.resources().get(MOON_ROCK) != null) {
            this.resources.put(MOON_ROCK, player.resources().get(MOON_ROCK));
        } else {
            this.resources.put(MOON_ROCK, 0);
            incResource3Btn.setDisable(true);
        }

        if (player.resources().get(NEPTUNE_CRYSTAL) != null) {
            this.resources.put(NEPTUNE_CRYSTAL, player.resources().get(NEPTUNE_CRYSTAL));
        } else {
            this.resources.put(NEPTUNE_CRYSTAL, 0);
            incResource4Btn.setDisable(true);
        }

        if (player.resources().get(VENUS_GRAIN) != null) {
            this.resources.put(VENUS_GRAIN, player.resources().get(VENUS_GRAIN));
        } else {
            this.resources.put(VENUS_GRAIN, 0);
            incResource5Btn.setDisable(true);
        }

        allLabels.add(amountEarthCactus);
        allLabels.add(amountMarsBar);
        allLabels.add(amountMoonRock);
        allLabels.add(amountNeptuneCrystal);
        allLabels.add(amountVenusGrain);

        return root;
    }

    // Handle every increase/decrease button separately
    // (all increase buttons in one method is possible, but not recommended)
    // Disable increasing when the player has not more than the chosen amount of resources to avoid wrong requests
    public void onIncreaseRes1ButtonPressed() {
        int currentAmount = Integer.parseInt(this.amountEarthCactus.getText());
        if (currentAmount < this.amountToDiscard && currentAmount < resources.get(EARTH_CACTUS)) {
            currentAmount += 1;
            this.amountEarthCactus.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }

        if (currentAmount > 0) {
            this.decResource1Btn.setDisable(false);
        }
        if (currentAmount == resources.get(EARTH_CACTUS)) {
            // disable button, when already enough of this resource selected
            this.incResource1Btn.setDisable(true);
        }
    }

    public void onIncreaseRes2ButtonPressed() {
        int currentAmount = Integer.parseInt(this.amountMarsBar.getText());
        if (currentAmount < this.amountToDiscard && currentAmount < resources.get(MARS_BAR)) {
            currentAmount += 1;
            this.amountMarsBar.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }

        if (currentAmount > 0) {
            this.decResource2Btn.setDisable(false);
        }
        if (currentAmount == resources.get(MARS_BAR)) {
            // disable button, when already enough of this resource selected
            this.incResource2Btn.setDisable(true);
        }
    }

    public void onIncreaseRes3ButtonPressed() {
        int currentAmount = Integer.parseInt(this.amountMoonRock.getText());
        if (currentAmount < this.amountToDiscard && currentAmount < resources.get(MOON_ROCK)) {
            currentAmount += 1;
            this.amountMoonRock.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }

        if (currentAmount > 0) {
            this.decResource3Btn.setDisable(false);
        }
        if (currentAmount == resources.get(MOON_ROCK)) {
            // disable button, when already enough of this resource selected
            this.incResource3Btn.setDisable(true);
        }
    }

    public void onIncreaseRes4ButtonPressed() {
        int currentAmount = Integer.parseInt(this.amountNeptuneCrystal.getText());
        if (currentAmount < this.amountToDiscard && currentAmount < resources.get(NEPTUNE_CRYSTAL)) {
            currentAmount += 1;
            this.amountNeptuneCrystal.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }

        if (currentAmount > 0) {
            this.decResource4Btn.setDisable(false);
        }
        if (currentAmount == resources.get(NEPTUNE_CRYSTAL)) {
            // disable button, when already enough of this resource selected
            this.incResource4Btn.setDisable(true);
        }
    }

    public void onIncreaseRes5ButtonPressed() {
        int currentAmount = Integer.parseInt(this.amountVenusGrain.getText());
        if (currentAmount < this.amountToDiscard && currentAmount < resources.get(VENUS_GRAIN)) {
            currentAmount += 1;
            this.amountVenusGrain.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }

        if (currentAmount > 0) {
            this.decResource5Btn.setDisable(false);
        }
        if (currentAmount == resources.get(VENUS_GRAIN)) {
            // disable button, when already enough of this resource selected
            this.incResource5Btn.setDisable(true);
        }
    }

    public void onDecreaseRes1ButtonPressed() {
        int currentAmount = Integer.parseInt(this.amountEarthCactus.getText());
        if (currentAmount > 0) {
            currentAmount -= 1;
            this.amountEarthCactus.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }

        // disable button when 0 reached, enable increasing again when decreasing amount from max. number needed
        // for discarding
        if (currentAmount < resources.get(EARTH_CACTUS)) {
            this.incResource1Btn.setDisable(false);
        }
        if (currentAmount == 0) {
            this.decResource1Btn.setDisable(true);
        }
    }

    public void onDecreaseRes2ButtonPressed() {
        int currentAmount = Integer.parseInt(this.amountMarsBar.getText());
        if (currentAmount > 0) {
            currentAmount -= 1;
            this.amountMarsBar.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }

        // disable button when 0 reached, enable increasing again when decreasing amount from max. number
        // needed for discarding
        if (currentAmount < resources.get(MARS_BAR)) {
            this.incResource2Btn.setDisable(false);
        }
        if (currentAmount == 0) {
            this.decResource2Btn.setDisable(true);
        }
    }

    public void onDecreaseRes3ButtonPressed() {
        int currentAmount = Integer.parseInt(this.amountMoonRock.getText());
        if (currentAmount > 0) {
            currentAmount -= 1;
            this.amountMoonRock.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }

        // disable button when 0 reached, enable increasing again when decreasing
        // amount from max. number needed for discarding
        if (currentAmount < resources.get(MOON_ROCK)) {
            this.incResource3Btn.setDisable(false);
        }
        if (currentAmount == 0) {
            this.decResource3Btn.setDisable(true);
        }
    }

    public void onDecreaseRes4ButtonPressed() {
        int currentAmount = Integer.parseInt(this.amountNeptuneCrystal.getText());
        if (currentAmount > 0) {
            currentAmount -= 1;
            this.amountNeptuneCrystal.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }

        // disable button when 0 reached, enable increasing again when decreasing amount from max. number needed
        // for discarding
        if (currentAmount < resources.get(NEPTUNE_CRYSTAL)) {
            this.incResource4Btn.setDisable(false);
        }
        if (currentAmount == 0) {
            this.decResource4Btn.setDisable(true);
        }
    }

    public void onDecreaseRes5ButtonPressed() {
        int currentAmount = Integer.parseInt(this.amountVenusGrain.getText());
        if (currentAmount > 0) {
            currentAmount -= 1;
            this.amountVenusGrain.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }

        // enable increase button again, when the required amount of resources to drop is not reached yet
        if (currentAmount < resources.get(VENUS_GRAIN)) {
            this.incResource5Btn.setDisable(false);
        }
        if (currentAmount == 0) {
            // disable button when 0 reached
            this.decResource5Btn.setDisable(true);
        }
    }

    // Check the resources everytime an increment/decrement button is pressed and refresh the discard button text
    // Pressing the discard button is only possible when all requirements are satisfied.
    public void checkChosenResources() {
        int quantity = 0;
        for (Label amountLabel : allLabels) {
            quantity += Integer.parseInt(amountLabel.getText());
        }

        this.discardButton.setText("Discard " + quantity + "/" + amountToDiscard);
        this.discardButton.setDisable(quantity != amountToDiscard);
    }

    // The discard button is only enabled when the correct amount of resources is chosen
    public void onDiscardButtonPressed() {
        // Values have to be negative for taking off resources
        this.resources.put(VENUS_GRAIN, (-1) * Integer.parseInt(amountVenusGrain.getText()));
        this.resources.put(MARS_BAR, (-1) * Integer.parseInt(amountMarsBar.getText()));
        this.resources.put(MOON_ROCK, (-1) * Integer.parseInt(amountMoonRock.getText()));
        this.resources.put(EARTH_CACTUS, (-1) * Integer.parseInt(amountEarthCactus.getText()));
        this.resources.put(NEPTUNE_CRYSTAL, (-1) * Integer.parseInt(amountNeptuneCrystal.getText()));

        pioneersService.move(gameID, DROP_ACTION, null, null, null, null, null, null, resources)
                .observeOn(FX_SCHEDULER)
                .subscribe(
                        // after success display a notification and close the stage/window automatically
                        result -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully dropped resources");
                            // Change style of alert
                            DialogPane dialogPane = alert.getDialogPane();
                            dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                            alert.showAndWait();
                            primaryStage.close();
                        }
                );
    }

}
