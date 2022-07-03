package de.uniks.pioneers.controller;

import de.uniks.pioneers.websocket.EventListener;
import de.uniks.pioneers.computation.CalculateMap;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Map;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.Tile;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class GameFieldSubController implements Controller {

	private final ObservableList<Player> players = FXCollections.observableArrayList();

	private final List<Node> harbourRoads = new ArrayList<>();
	private final List<Node> roads = new ArrayList<>();
	private final List<Node> roadCircles = new ArrayList<>();
	private final List<Node> buildingCircles = new ArrayList<>();
	private final List<Node> labels = new ArrayList<>();
	private final List<Node> harbourImages = new ArrayList<>();
	private final List<Node> robberImages = new ArrayList<>();
	private final List<Node> hexagons = new ArrayList<>();

	private final IDStorage idStorage;

	private Parent parent;
	private final GameStorage gameStorage;
	private final PioneersService pioneersService;
	private final EventListener eventListener;
	private final CompositeDisposable disposable = new CompositeDisposable();


	// This variable is needed for the starting/stopping of the field controllers
	private final List<HexSubController> hexSubControllers = new ArrayList<>();
	private final List<CircleSubController> circleSubControllers = new ArrayList<>();

	@Inject
	public GameFieldSubController(GameStorage gameStorage,
								  PioneersService pioneersService,
								  IDStorage idStorage,
								  EventListener eventListener) {
		this.gameStorage = gameStorage;
		this.pioneersService = pioneersService;
		this.idStorage = idStorage;
		this.eventListener = eventListener;
	}

	@Override
	public void init() {
		disposable.add(eventListener
				.listen("games." + this.gameStorage.getId() + ".buildings.*.*", Building.class)
				.observeOn(FX_SCHEDULER)
				.subscribe(event -> {
					Building building = event.data();
					this.updateBuildings((int) building.x(), (int) building.y(), (int) building.z(), (int) building.side(), building.owner(), building.type());
				}));

		pioneersService.findAllPlayers(gameStorage.getId())
				.observeOn(FX_SCHEDULER)
				.subscribe(this.players::addAll);
	}


	@Override
	public void destroy() {
		hexSubControllers.forEach(HexSubController::destroy);
		hexSubControllers.clear();
		circleSubControllers.forEach(CircleSubController::destroy);
		circleSubControllers.clear();
		disposable.dispose();
	}

	@Override
	public Parent render() {
		CalculateMap calculateMap = new CalculateMap();
		this.parent = calculateMap.buildMap(this.gameStorage.getSize());

		pioneersService.findAllTiles(gameStorage.getId()).observeOn(FX_SCHEDULER).subscribe(this::loadMap);

		return parent;
	}

	public void loadMap(Map map) {
		Pane gameMap = (Pane) this.parent;

		for (Node node : gameMap.getChildren()) {
			if (node.getId().endsWith("_HarbourRoad")) {
				harbourRoads.add(node);
			} else if (node.getId().endsWith("_Road")) {
				roads.add(node);
			} else if (node.getId().endsWith("_3") || node.getId().endsWith("_7") || node.getId().endsWith("_11")) {
				roadCircles.add(node);
			} else if (node.getId().endsWith("_0") || node.getId().endsWith("_6")) {
				buildingCircles.add(node);
			} else if (node.getId().endsWith("_label")) {
				labels.add(node);
			} else if (node.getId().endsWith("_HarbourImage")) {
				harbourImages.add(node);
			} else if (node.getId().endsWith("_RobberImage")) {
				robberImages.add(node);
			} else {
				hexagons.add(node);
			}
		}

		for (Tile tile : map.tiles()) {

			int x = (int) tile.x();
			int y = (int) tile.y();
			int z = (int) tile.z();
			String stringX = "x" + x;
			String stringY = "y" + y;
			String stringZ = "z" + z;
			if (x < 0) {
				stringX = "xM" + Math.abs(x);
			}
			if (y < 0) {
				stringY = "yM" + Math.abs(y);
			}
			if (z < 0) {
				stringZ = "zM" + Math.abs(z);
			}
			for (Node node: hexagons) {
				if (node.getId().equals(stringX+stringY+stringZ)) {
					HexSubController hexSubController = new HexSubController((Polygon) node, tile);
					hexSubController.init();
					this.hexSubControllers.add(hexSubController);
				}
			}

			// Set number token on tiles
			Label label = (Label) parent.lookup("#" + stringX + stringY + stringZ + "_label");
			if (label != null) {
				label.setText("" + tile.numberToken());

				if (tile.numberToken() == 7) {
					label.toBack();
				}
			}
		}
		List<Node> circles = new ArrayList<>();
		circles.addAll(roadCircles);
		circles.addAll(buildingCircles);

		for (Node node : circles) {
			node.toFront();
			for (Node road : roads) {
				if (road.getId().contains(node.getId())) {
					CircleSubController circleSubController = new CircleSubController(
							(Circle) node, (Polygon) road, pioneersService, gameStorage,
							idStorage, eventListener, buildingCircles, this);
					circleSubController.init();
					this.circleSubControllers.add(circleSubController);
				}
			}
			if (node.getId().endsWith("_0") || node.getId().endsWith("_6")) {
				CircleSubController circleSubController = new CircleSubController(
						(Circle) node, null, pioneersService, gameStorage,
						idStorage, eventListener, buildingCircles, this);
				circleSubController.init();
				this.circleSubControllers.add(circleSubController);
			}
		}
	}

	public void build(String building) {
		for (CircleSubController c : circleSubControllers) {
			c.setBuild(building);
		}
	}

	// needs to be optimist for map sizes larger than 6
	public void updateBuildings(int x, int y, int z, int side, String owner, String type) {
		String color = Color.BLACK.toString();
		for (Player player : players) {
			if (player.userId().equals(owner)) {
				color = player.color();
			}
		}
		for (CircleSubController c : circleSubControllers) {
			if (c.getX() == x && c.getY() == y && c.getZ() == z) {
				switch (type) {
					case "road" -> c.setRoad(x, y, z, side, color);
					case "settlement" -> c.setSettlement(x, y, z, side, color);
					case "city" -> c.setCity(x, y, z, side, color);
				}
			}
		}
	}

	public ObservableList<Player> getPlayers() {
		return players;
	}
}
