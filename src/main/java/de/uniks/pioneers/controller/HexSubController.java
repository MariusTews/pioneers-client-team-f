package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.AlertService;
import de.uniks.pioneers.service.HexFillService;
import de.uniks.pioneers.service.PioneersService;
import de.uniks.pioneers.service.UserService;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.StageStyle;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static de.uniks.pioneers.Constants.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class HexSubController implements Controller {

    private final Polygon view;
    private final Tile tile;
    private final String gameID;
    private final String playerID;
    private final PioneersService pioneersService;
    private final UserService userService;
    private final Set<String> owners = new HashSet<>();
    final HashMap<String, String> userHash = new HashMap<>();

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
        this.view.setOnMouseEntered(event -> onFieldMouseHoverEnter());
        this.view.setOnMouseExited(event -> onFieldMouseHoverExit());
        this.view.setOnMouseClicked(event -> onTileClicked());
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
    private void onFieldMouseHoverEnter() {
        // Change the view
        this.view.setStroke(Color.RED);
    }

    // Mouse leaves the field
    private void onFieldMouseHoverExit() {
        // Change the view
        this.view.setStroke(Color.BLACK);
    }

    // check if the correct player clicked while rob action, check coordinates (if valid field)
    private void onTileClicked() {
        Point3D tileCoordinates = new Point3D(tile.x(), tile.y(), tile.z());

        // Make a server request to find out who the current player is and which action is expected
        this.checkSurrounding();
        // For the names of the players a server request on all users is needed
        this.userService.findAllUsers()
                .observeOn(FX_SCHEDULER)
                .subscribe(users -> {
                    for (User user : users) {
                        if (this.owners.contains(user._id())) {
                            this.userHash.put(user.name(), user._id());
                        }
                    }

                    this.pioneersService.findOneState(gameID)
                            .observeOn(FX_SCHEDULER)
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
                                    AlertService alert = new AlertService();
                                    alert.showAlert("""
                                            You are not able\s
                                            to place the robber\s
                                            on the same tile again!""");
                                }
                            });
                });
    }

    // Filter the buildings and check if the settlements or cities touch the clicked tile
    // possible buildings/cities when the owner is not playerID (the player himself/herself)
    private void checkSurrounding() {
        // Get all buildings
        this.pioneersService.findAllBuildings(gameID)
                .observeOn(FX_SCHEDULER)
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

                });
    }

    private String chooseTarget() {
        if (owners.isEmpty()) {
            return null;
        } else if (owners.size() == 1) {
            // return the only name in the list, choice dialog is not needed
            return owners.iterator().next();
        }
        // Pop up with selection of the player's names, remove cancel button
        ChoiceDialog<String> choosingTarget = new ChoiceDialog<>(this.userHash.keySet().iterator().next(), this.userHash.keySet());
        choosingTarget.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);
        // set stylesheet
        choosingTarget.getDialogPane().getStylesheets().add(Objects.requireNonNull(Main.class
                .getResource("view/stylesheets/ChoiceDialogRob.css")).toExternalForm());
        choosingTarget.setHeaderText("Rob from");
        // remove close/maximize/minimize button
        choosingTarget.initStyle(StageStyle.UNDECORATED);
        // Get the chosen target and make a server request with this target
        choosingTarget.showAndWait();
        return this.userHash.get(choosingTarget.getSelectedItem());
    }

    private void setPolygonColor() {
        HexFillService hexFillService = new HexFillService();
        hexFillService.fillHexagon(view, tile.type());
    }
}
