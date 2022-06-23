package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.service.PioneersService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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
    public List<Label> allLabels = List.of(amountEarthCactus,
                                    amountMarsBar, amountMoonRock, amountNeptuneCrystal, amountVenusGrain);

    private PioneersService pioneersService;
    private final int amountToDiscard;
    private String gameID;
    private final Player player;

    @Inject
    public DiscardResourcesController(Player player, String gameID, PioneersService pioneersService) {
        this.player = player;
        this.gameID = gameID;
        this.pioneersService = pioneersService;
        this.amountToDiscard = player.resources().get("unknown") / 2;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/OpponentSubView.fxml"));
        loader.setControllerFactory(c -> this);
        Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        this.discardButton.setText("Discard 0/" + amountToDiscard);
        this.discardButton.setDisable(true);

        return parent;
    }

    // Handle every increase/decrease button separately
    // (all increase buttons in one method is possible, but not recommended)
    public void onIncreaseRes1ButtonPressed() {
        int currentAmount = Integer.parseInt(this.amountEarthCactus.getText());
        if (currentAmount < this.amountToDiscard && currentAmount < this.player.resources().get(EARTH_CACTUS)) {
            currentAmount += 1;
            this.amountEarthCactus.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }

        if (currentAmount > 0) {
            this.decResource1Btn.setDisable(false);
        } else if (currentAmount == player.resources().get(EARTH_CACTUS)) {
            // disable button, when already enough of this resource selected
            this.incResource1Btn.setDisable(true);
        }
    }

    public void onIncreaseRes2ButtonPressed() {
        int currentAmount = Integer.parseInt(this.amountMarsBar.getText());
        if (currentAmount < this.amountToDiscard && currentAmount < this.player.resources().get(MARS_BAR)) {
            currentAmount += 1;
            this.amountMarsBar.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }

        if (currentAmount > 0) {
            this.decResource2Btn.setDisable(false);
        } else if (currentAmount == player.resources().get(MARS_BAR)) {
            // disable button, when already enough of this resource selected
            this.incResource2Btn.setDisable(true);
        }
    }

    public void onIncreaseRes3ButtonPressed() {
        int currentAmount = Integer.parseInt(this.amountMoonRock.getText());
        if (currentAmount < this.amountToDiscard && currentAmount < this.player.resources().get(MOON_ROCK)) {
            currentAmount += 1;
            this.amountMoonRock.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }

        if (currentAmount > 0) {
            this.decResource3Btn.setDisable(false);
        } else if (currentAmount == player.resources().get(MOON_ROCK)) {
            // disable button, when already enough of this resource selected
            this.incResource3Btn.setDisable(true);
        }
    }

    public void onIncreaseRes4ButtonPressed() {
        int currentAmount = Integer.parseInt(this.amountNeptuneCrystal.getText());
        if (currentAmount < this.amountToDiscard && currentAmount < this.player.resources().get(NEPTUNE_CRYSTAL)) {
            currentAmount += 1;
            this.amountNeptuneCrystal.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }

        if (currentAmount > 0) {
            this.decResource4Btn.setDisable(false);
        } else if (currentAmount == player.resources().get(NEPTUNE_CRYSTAL)) {
            // disable button, when already enough of this resource selected
            this.incResource4Btn.setDisable(true);
        }
    }

    public void onIncreaseRes5ButtonPressed() {
        int currentAmount = Integer.parseInt(this.amountVenusGrain.getText());
        if (currentAmount < this.amountToDiscard && currentAmount < this.player.resources().get(VENUS_GRAIN)) {
            currentAmount += 1;
            this.amountVenusGrain.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }

        if (currentAmount > 0) {
            this.decResource5Btn.setDisable(false);
        } else if (currentAmount == player.resources().get(VENUS_GRAIN)) {
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

        // disable button when 0 reached, enable again when decreasing amount from max. number needed for discarding
        if (currentAmount < player.resources().get(EARTH_CACTUS)) {
            this.incResource1Btn.setDisable(false);
        } else if (currentAmount == 0) {
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

        // disable button when 0 reached, enable again when decreasing amount from max. number needed for discarding
        if (currentAmount < player.resources().get(MARS_BAR)) {
            this.incResource2Btn.setDisable(false);
        } else if (currentAmount == 0) {
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

        // disable button when 0 reached, enable again when decreasing amount from max. number needed for discarding
        if (currentAmount < player.resources().get(MOON_ROCK)) {
            this.incResource3Btn.setDisable(false);
        } else if (currentAmount == 0) {
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

        // disable button when 0 reached, enable again when decreasing amount from max. number needed for discarding
        if (currentAmount < player.resources().get(NEPTUNE_CRYSTAL)) {
            this.incResource4Btn.setDisable(false);
        } else if (currentAmount == 0) {
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

        // disable button when 0 reached, enable again when decreasing amount from max. number needed for discarding
        if (currentAmount < player.resources().get(VENUS_GRAIN)) {
            this.incResource5Btn.setDisable(false);
        } else if (currentAmount == 0) {
            this.decResource5Btn.setDisable(true);
        }
    }

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
        HashMap<String, Integer> resources = new HashMap<>() {{
            put(VENUS_GRAIN, -1 * Integer.parseInt(amountVenusGrain.getText()));
            put(MARS_BAR, -1 * Integer.parseInt(amountMarsBar.getText()));
            put(MOON_ROCK, -1 * Integer.parseInt(amountMoonRock.getText()));
            put(EARTH_CACTUS, -1 * Integer.parseInt(amountEarthCactus.getText()));
            put(NEPTUNE_CRYSTAL, -1 * Integer.parseInt(amountNeptuneCrystal.getText()));
        }};

        pioneersService.move(gameID, DROP_STATE, null, null, null, null, null, null, resources)
                .observeOn(FX_SCHEDULER)
                .subscribe(
                        // TODO: result -> after success display a notification and close the stage/window automatically
                );
    }

}
