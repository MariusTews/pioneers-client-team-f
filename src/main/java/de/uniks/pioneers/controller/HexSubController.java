package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.PioneersService;
import de.uniks.pioneers.service.UserService;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;
import javafx.stage.StageStyle;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static de.uniks.pioneers.Constants.*;

public class HexSubController implements Controller {

    private final Polygon view;
    private final Tile tile;
    private final String gameID;
    private final String playerID;
    private final PioneersService pioneersService;
    private final UserService userService;
    private final Set<String> owners = new HashSet<>();
    HashMap<String, String> userHash = new HashMap<>();

    @Inject
    public HexSubController(Polygon view, Tile tile, String gameID, String playerID, PioneersService pioneersService,
                            UserService userService) {
        this.view = view;
        this.tile = tile;
        this.gameID = gameID;
        this.pioneersService = pioneersService;
        this.playerID = playerID;
        this.userService = userService;
    }

    @Override
    public void init() {
        // Add mouse listeners
        this.view.setOnMouseEntered(this::onFieldMouseHoverEnter);
        this.view.setOnMouseExited(this::onFieldMouseHoverExit);
        this.view.setOnMouseClicked(this::onTileClicked);
        setPolygonColor();
    }

    @Override
    public void destroy() {
    }

    @Override
    public Parent render() {
        //this method needs to be pressed because this subController implements controller therefore the method is empty
        return null;
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

    // check if the correct player clicked while rob action, check coordinates (if valid field)
    private void onTileClicked(MouseEvent event) {
        Point3D tileCoordinates = new Point3D(tile.x(), tile.y(), tile.z());

        // Make a server request to find out who the current player is and which action is expected
        this.checkSurrounding();
        this.pioneersService.findOneState(gameID)
                .observeOn(FX_SCHEDULER)
                // TODO: how to prevent subscribe warnings?
                .subscribe(move -> {
                    ExpectedMove currentMove = move.expectedMoves().get(0);
                    // check that the current player clicked while rob action and did not choose the same tile for rob again
                    // move.robber() is null when placing the robber for the first time
                    if (currentMove.action().equals(ROB_ACTION) && currentMove.players().get(0).equals(playerID)
                            && (move.robber() == null || !move.robber().equals(tileCoordinates))) {
                        pioneersService.move(gameID, ROB_ACTION, tile.x(), tile.y(), tile.z(), null, null,
                                        this.chooseTarget(), null)
                                .observeOn(FX_SCHEDULER)
                                .subscribe(result -> {
                                    // set cursor back to default for the current player -> the view is updated for all in GameScreenController
                                    view.getScene().setCursor(Cursor.DEFAULT);
                                }, onError -> {

                                });
                    } else if (move.robber() != null && move.robber().equals(tileCoordinates)) {
                        // Show alert if not the current player clicked while "rob" action
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, """
                                You are not able\s
                                to place the robber\s
                                on the same tile again!""");
                        // set style
                        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(Main.class
                                .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                        alert.showAndWait();
                    } else {
                        // Show alert if not the current player clicked while "rob" action
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Not your turn or wrong move!");
                        // set style
                        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(Main.class
                                .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                        alert.showAndWait();
                    }
                });
    }

    // Filter the buildings and check if the settlements or cities touch the clicked tile
    // possible buildings/cities when the owner is not playerID (the player himself/herself)
    private void checkSurrounding() {
        // Get all buildings
        this.pioneersService.findAllBuildings(gameID)
                .observeOn(FX_SCHEDULER)
                // TODO: how to prevent subscribe warnings?
                .subscribe(buildings -> {
                    // Filter all buildings on the map, get the owner of the buildings who surround the clicked tile
                    for (Building building : buildings) {
                        if (!(building.type().equals(ROAD) || building.owner().equals(playerID))) {
                            if (building.x().equals(tile.x()) && building.y().equals(tile.y()) && building.z().equals(tile.z())) {
                                // clicked tile: city or settlement at 0 or 6 o'clock
                                this.owners.add(building.owner());
                            } else if (building.side().equals(6) &&
                                    ((building.x().equals(tile.x().intValue() + 1)
                                            && building.y().equals(tile.y())
                                            && building.z().equals(tile.z().intValue() - 1))
                                            || (building.x().equals(tile.x())
                                            && building.y().equals(tile.y().intValue() + 1)
                                            && building.z().equals(tile.z().intValue() - 1)))) {
                                // upper tiles: settlement or city at 6 o'clock
                                this.owners.add(building.owner());
                            } else if (building.side().equals(0) &&
                                    ((building.x().equals(tile.x().intValue() - 1)
                                            && building.y().equals(tile.y())
                                            && building.z().equals(tile.z().intValue() + 1))
                                            || (building.x().equals(tile.x())
                                            && building.y().equals(tile.y().intValue() - 1)
                                            && building.z().equals(tile.z().intValue() + 1)))) {
                                // below tiles: settlement or city at 0 o'clock
                                this.owners.add(building.owner());
                            }
                        }
                    }

                    // For the names of the players a server request on all users is needed
                    this.userService.findAllUsers()
                            .observeOn(FX_SCHEDULER)
                            // TODO: how to prevent subscribe warnings?
                            .subscribe(users -> {
                                for (User user : users) {
                                    if (this.owners.contains(user._id())) {
                                        this.userHash.put(user.name(), user._id());
                                    }
                                }
                            });
                });
    }

    private String chooseTarget() {
        if (owners.isEmpty()) {
            return null;
        } else if (owners.size() == 1) {
            // return the only name in the list, choice dialog is not needed
            return owners.iterator().next();
        }
        // Pop up with selection of the player's names
        ChoiceDialog choosingTarget = new ChoiceDialog(userHash.keySet().iterator().next(), userHash.keySet());
        // set stylesheet
        choosingTarget.getDialogPane().getStylesheets().add(Objects.requireNonNull(Main.class
                .getResource("view/stylesheets/ChoiceDialogRob.css")).toExternalForm());
        choosingTarget.setHeaderText("Rob from");
        // remove close/maximize/minimize button
        choosingTarget.initStyle(StageStyle.UNDECORATED);
        // Get the chosen target and make a server request with this target
        choosingTarget.showAndWait();
        return this.userHash.get((String) choosingTarget.getSelectedItem());
    }

    private void setPolygonColor() {
        String type = tile.type();
        switch (type) {
            case "desert" -> {
                Image desert = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/2_desert.png")).toExternalForm());
                ImagePattern desertPattern = new ImagePattern(desert);
                view.setFill(desertPattern);
            }
            case "fields" -> {
                Image fields = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/4_venus.png")).toExternalForm());
                ImagePattern fieldPattern = new ImagePattern(fields);
                view.setFill(fieldPattern);
            }
            case "mountains" -> {
                Image hills = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/3_moon.png")).toExternalForm());
                ImagePattern hillPattern = new ImagePattern(hills);
                view.setFill(hillPattern);
            }
            case "hills" -> {
                Image mountains = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/1_mars.png")).toExternalForm());
                ImagePattern mountainPattern = new ImagePattern(mountains);
                view.setFill(mountainPattern);
            }
            case "forest" -> {
                Image forest = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/6_earth.png")).toExternalForm());
                ImagePattern forestPattern = new ImagePattern(forest);
                view.setFill(forestPattern);
            }
            case "pasture" -> {
                Image pasture = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/5_neptun.png")).toExternalForm());
                ImagePattern pasturePattern = new ImagePattern(pasture);
                view.setFill(pasturePattern);
            }
        }
    }
}
