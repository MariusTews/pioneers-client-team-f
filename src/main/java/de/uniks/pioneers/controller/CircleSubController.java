package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.websocket.EventListener;
import de.uniks.pioneers.model.ExpectedMove;
import de.uniks.pioneers.model.State;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class CircleSubController implements Controller {

	private final Circle view;
	private final Polygon road;
	private final PioneersService pioneersService;
	private final GameStorage gameStorage;
	private final IDStorage idStorage;
	private final EventListener eventListener;
	private final List<Node> buildingCircles;
	private final GameFieldSubController gameFieldSubController;
	private final CompositeDisposable disposable = new CompositeDisposable();

	private int x;
	private int y;
	private int z;
	private int side;
	private ExpectedMove nextMove;
	private String build = null;

	@Inject
	public CircleSubController(Circle view, Polygon road, PioneersService pioneersService,
							   GameStorage gameStorage, IDStorage idStorage, EventListener eventListener,
							   List<Node> buildingCircles, GameFieldSubController gameFieldSubController) {
		this.view = view;
		this.road = road;
		this.pioneersService = pioneersService;
		this.gameStorage = gameStorage;
		this.idStorage = idStorage;
		this.eventListener = eventListener;
		this.buildingCircles = buildingCircles;
		this.gameFieldSubController = gameFieldSubController;
	}

	@Override
	public void init() {
		// Add mouse listeners
		this.view.setOnMouseEntered(event1 -> onFieldMouseHoverEnter());
		this.view.setOnMouseExited(event1 -> onFieldMouseHoverExit());
		this.view.setOnMouseClicked(mouseEvent -> onFieldClicked());

		//get coordinates from fx:id
		String id = this.view.getId();
		id = (id.replace("M", "-"));
		id = id.substring(1);
		String[] split = id.split("y");
		this.x = Integer.parseInt(split[0]);
		String[] split1 = split[1].split("z");
		this.y = Integer.parseInt(split1[0]);
		String[] split2 = split1[1].split("_");
		this.z = Integer.parseInt(split2[0]);
		this.side = Integer.parseInt(split2[1]);

		this.nextMove = new ExpectedMove("", Collections.singletonList(idStorage.getID()));

		disposable.add(eventListener
				.listen("games." + this.gameStorage.getId() + ".state.*", State.class)
				.observeOn(FX_SCHEDULER)
				.subscribe(event -> {
					State state = event.data();
					//state needs to be checked
					//so index out of bound exception doesnt get thrown
					if(!state.expectedMoves().isEmpty()) {
						this.nextMove = state.expectedMoves().get(0);
					}
				}));
	}



	private void onFieldClicked() {
		//if it's not your turn
		if (!yourTurn(nextMove)) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION, "Not your turn!");
			// set style
			DialogPane dialogPane = alert.getDialogPane();
			dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
					.getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
			alert.showAndWait();
			// if the game is in the founding-phase
		} else if (nextMove.action().startsWith("founding-r") || nextMove.action().startsWith("founding-s") || nextMove.action().equals("")) {
			this.pioneersService.findOneState(gameStorage.getId())
					.observeOn(FX_SCHEDULER)
					.subscribe(move -> {
						String action = move.expectedMoves().get(0).action();
						if (side == 0 || side == 6) {
							this.pioneersService.move(gameStorage.getId(), action, x, y, z, side, "settlement", null, null)
									.observeOn(FX_SCHEDULER)
									.doOnError(error -> notAllowedToPlace())
									.subscribe(onSuc -> {
									}, onError -> {
									});
						} else {
							this.pioneersService.move(gameStorage.getId(), action, x, y, z, side, "road", null, null)
									.observeOn(FX_SCHEDULER)
									.doOnError(error -> notAllowedToPlace())
									.subscribe(onSuc -> {
									}, onError -> {
									});
						}
					});
		} else {//if you build a new building in round-loop
			if (build != null) {

				this.pioneersService.move(gameStorage.getId(), "build", x, y, z, side, build, null, null)
						.observeOn(FX_SCHEDULER)
						.subscribe();

				this.gameFieldSubController.build(null);
			}
		}
	}

	private void notAllowedToPlace() {
		String[] building = nextMove.action().split("-");
		Alert alert = new Alert(Alert.AlertType.INFORMATION, "you can't place that " + building[1] + " here!");
		// Set style
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
				.getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
		alert.showAndWait();
	}

	public Boolean yourTurn(ExpectedMove move) {
		List<String> currPlayer = move.players();
		for (String player : currPlayer) {
			if (player.equals(idStorage.getID())) {
				return true;
			}
		}
		return false;
	}

	public void setRoad(int x, int y, int z, int side, String color) {
		if (this.x == x && this.y == y && this.z == z && this.side == side) {
			// Make road Polygon visible and set the color

			this.road.setFill(Color.valueOf(color));
			this.road.toFront();
			this.road.setVisible(true);

			this.view.setFill(Color.TRANSPARENT);
			this.view.setStroke(Color.TRANSPARENT);
			this.view.setOnMouseClicked(null);
			buildingCircles.forEach(Node::toFront);
		}
	}

	public void setSettlement(int x, int y, int z, int side, String color) {
		if (this.x == x && this.y == y && this.z == z && this.side == side) {
			// Set style and image on the coordinates
			this.view.setRadius(20);
			this.view.toFront();
			this.view.setStroke(Color.TRANSPARENT);
			Image settlement = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/settlement_" + color + ".png")).toExternalForm());
			ImagePattern settlementPattern = new ImagePattern(settlement);
			this.view.setFill(settlementPattern);
		}
	}

	public void setCity(int x, int y, int z, int side, String color) {
		if (this.x == x && this.y == y && this.z == z && this.side == side) {
			// Set style and image on the coordinates
			this.view.setRadius(20);
			this.view.setStroke(Color.TRANSPARENT);
			Image city = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/city_" + color + ".png")).toExternalForm());
			ImagePattern cityPattern = new ImagePattern(city);
			this.view.setFill(cityPattern);
		}
	}

	@Override
	public void destroy() {
		disposable.dispose();
	}


	@Override
	public Parent render() {
		//this method needs to be pressed because this subController implements controller therefore the method is empty
		return null;
	}

	// Mouse hovers over field
	private void onFieldMouseHoverEnter() {
		// Change the view
		if (this.view.getFill().equals(Color.TRANSPARENT)) {
			this.view.setFill(Color.GRAY);
		}
	}

	// Mouse leaves the field
	private void onFieldMouseHoverExit() {
		// Change the view
		if (this.view.getFill().equals(Color.GRAY)) {
			this.view.setFill(Color.TRANSPARENT);
		}
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}
}
