package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.service.PioneersService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
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

@SuppressWarnings("ResultOfMethodCallIgnored")
public class YearOfPlentyController implements Controller {
    @FXML
    public Pane discardView;
    @FXML
    public Button increment_lumber;
    @FXML
    public Button increment_brick;
    @FXML
    public Button increment_ore;
    @FXML
    public Button increment_wool;
    @FXML
    public Button increment_grain;
    @FXML
    public Button decrement_lumber;
    @FXML
    public Button decrement_brick;
    @FXML
    public Button decrement_ore;
    @FXML
    public Button decrement_wool;
    @FXML
    public Button decrement_grain;
    @FXML
    public Button discardButton;
    @FXML
    public Label lumber;
    @FXML
    public Label brick;
    @FXML
    public Label ore;
    @FXML
    public Label wool;
    @FXML
    public Label grain;
    private final List<Label> allLabels = new ArrayList<>();
    private final PioneersService pioneersService;
    private final String gameID;
    private final Player player;
    private final Window owner;
    private Stage primaryStage;
    private final HashMap<String, Integer> resourcesMap = new HashMap<>() {{
        put(VENUS_GRAIN, 0);
        put(MOON_ROCK, 0);
        put(MARS_BAR, 0);
        put(NEPTUNE_CRYSTAL, 0);
        put(EARTH_CACTUS, 0);
    }};

    @Inject
    public YearOfPlentyController(Player player, String gameID, PioneersService pioneersService, Window owner) {
        this.player = player;
        this.gameID = gameID;
        this.pioneersService = pioneersService;
        this.owner = owner;
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
            // Set to UNDECORATED or TRANSPARENT (without white background) to remove minimize, maximize and close button of stage
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root, 200, 350);
            scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/DiscardResourcesStyle.css")).toString());
            primaryStage.setScene(scene);
            // Specify modality of the new window: interactions are only possible on the second window
            primaryStage.initModality(Modality.WINDOW_MODAL);
            primaryStage.initOwner(this.owner);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Load UI elements
        this.discardButton.setText("Get 0/2");
        this.discardButton.setDisable(true);

        allLabels.add(lumber);
        allLabels.add(brick);
        allLabels.add(ore);
        allLabels.add(wool);
        allLabels.add(grain);

        return root;
    }

    // Get the id of the clicked button and check dynamically which resource has to be increased or decreased
    @FXML
    private void onAmountButtonPressed(ActionEvent event) {
        Node node = (Node) event.getTarget();
        String buttonId = node.getId();
        if (buttonId != null) {
            String[] expected = buttonId.split("_");

            // Labels are not named after the space theme because the regular names of the resources are
            // used for the hashMap which compares the player's resources (server = default names) with the local map
            Label amount = (Label) discardView.lookup("#" + expected[1]);

            if (expected[0].equals("increment")) {
                this.increaseResource(amount);
            } else if (expected[0].equals("decrement")) {
                this.decreaseResource(amount);
            }
        }
    }

    private void increaseResource(Label amount) {
        int currentAmount = Integer.parseInt(amount.getText());
        if (currentAmount < 2) {
            currentAmount += 1;
            amount.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }
    }

    private void decreaseResource(Label amount) {
        int currentAmount = Integer.parseInt(amount.getText());
        if (currentAmount > 0) {
            currentAmount -= 1;
            amount.setText(String.valueOf(currentAmount));
            this.checkChosenResources();
        }
    }

    public void checkChosenResources() {
        int quantity = 0;
        for (Label amountLabel : allLabels) {
            quantity += Integer.parseInt(amountLabel.getText());
        }

        this.discardButton.setText("Get " + quantity + "/" + 2);
        this.discardButton.setDisable(quantity != 2);
    }

    // The Get button is only enabled when the correct amount of resources is chosen
    public void onDiscardButtonPressed() {
        this.resourcesMap.put(VENUS_GRAIN, Integer.parseInt(grain.getText()));
        this.resourcesMap.put(MARS_BAR, Integer.parseInt(brick.getText()));
        this.resourcesMap.put(MOON_ROCK, Integer.parseInt(ore.getText()));
        this.resourcesMap.put(EARTH_CACTUS, Integer.parseInt(lumber.getText()));
        this.resourcesMap.put(NEPTUNE_CRYSTAL, Integer.parseInt(wool.getText()));

        pioneersService.yearOfPlentyCard(gameID, resourcesMap)
                .observeOn(FX_SCHEDULER)
                .subscribe(
                        // after success display a notification and close the stage/window automatically
                        result -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully got resources");
                            // Change style of alert
                            DialogPane dialogPane = alert.getDialogPane();
                            dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                            alert.showAndWait();
                            primaryStage.close();
                        }
                );
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }
}
