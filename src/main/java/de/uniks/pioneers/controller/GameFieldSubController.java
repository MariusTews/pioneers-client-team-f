package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.websocket.EventListener;
import de.uniks.pioneers.computation.CalculateMap;
import de.uniks.pioneers.computation.LoadMapTemplate;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.*;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.uniks.pioneers.Constants.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class GameFieldSubController implements Controller {

	private final ObservableList<Player> players = FXCollections.observableArrayList();

	private final List<Node> harbourRoads = new ArrayList<>();
	private final List<Node> roads = new ArrayList<>();
	private final List<Node> roadCircles = new ArrayList<>();
	private final List<Node> buildingCircles = new ArrayList<>();
	private final List<Node> hexagons = new ArrayList<>();

	private final IDStorage idStorage;
	private final UserService userService;

	private Parent parent;
	private final GameStorage gameStorage;
	private final PioneersService pioneersService;
	private final EventListener eventListener;
	private final AchievementsService achievementsService;
    private final CompositeDisposable disposable = new CompositeDisposable();


	// This variable is needed for the starting/stopping of the field controllers
	private final List<HexSubController> hexSubControllers = new ArrayList<>();
	private final List<CircleSubController> circleSubControllers = new ArrayList<>();

	@Inject
	public GameFieldSubController(GameStorage gameStorage,
								  PioneersService pioneersService,
								  UserService userService,
								  IDStorage idStorage,
								  EventListener eventListener,
                                  AchievementsService achievementsService) {
        this.gameStorage = gameStorage;
        this.pioneersService = pioneersService;
        this.userService = userService;
        this.idStorage = idStorage;
        this.eventListener = eventListener;
        this.achievementsService = achievementsService;
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
        disposable.clear();
    }

	@Override
	public Parent render() {
		Map map = pioneersService.findAllTiles(gameStorage.getId()).blockingFirst();
		if (this.gameStorage.getMapTemplate() == null) {
			CalculateMap calculateMap = new CalculateMap();
			this.parent = calculateMap.buildMap(this.gameStorage.getSize(), false);
		} else {
			LoadMapTemplate loadMapTemplate = new LoadMapTemplate();
			this.parent = loadMapTemplate.loadMap(map, true);
		}
		this.loadMap(pioneersService.findAllTiles(gameStorage.getId()).blockingFirst());
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
			} else if (!node.getId().endsWith("_label") && !node.getId().endsWith("_HarbourImage") && !node.getId().endsWith("_RobberImage")) {
				hexagons.add(node);
			}
		}

		for (Tile tile : map.tiles()) {

			String id = parseId((Integer) tile.x(), (Integer) tile.y(), (Integer) tile.z());
			for (Node node : hexagons) {
				if (node.getId().equals(id)) {
					HexSubController hexSubController = new HexSubController((Polygon) node, tile,
							this.gameStorage.getId(), this.idStorage.getID(), this.pioneersService, this.userService);
					hexSubController.init();
					this.hexSubControllers.add(hexSubController);
				}
			}

			// Set number token on tiles
			Label label = (Label) parent.lookup("#" + id + "_label");
			if (label != null) {
				label.setText("" + tile.numberToken());
				label.setStyle("-fx-background-color: GHOSTWHITE; -fx-background-radius: 5px; ");

				if (tile.numberToken() == 7) {
					label.toBack();
				} else if (tile.numberToken() == 6 || tile.numberToken() == 8) {
					label.setTextFill(Color.RED);
				}
			}
		}

		loadHarbors(map);

		List<Node> circles = new ArrayList<>();
		circles.addAll(roadCircles);
		circles.addAll(buildingCircles);

		for (Node node : circles) {
			node.toFront();
			for (Node road : roads) {
				if (road.getId().contains(node.getId())) {
					CircleSubController circleSubController = new CircleSubController(
							(Circle) node, (Polygon) road, pioneersService, gameStorage,
							idStorage, buildingCircles, this, achievementsService);
					circleSubController.init();
					this.circleSubControllers.add(circleSubController);
				}
			}
			if (node.getId().endsWith("_0") || node.getId().endsWith("_6")) {
				CircleSubController circleSubController = new CircleSubController(
						(Circle) node, null, pioneersService, gameStorage,
						idStorage, buildingCircles, this, achievementsService);
				circleSubController.init();
				this.circleSubControllers.add(circleSubController);
			}
		}
	}

	private void loadHarbors(Map map) {
		for (Harbor harbor : map.harbors()) {
			String id = "";

			switch ((int) harbor.side()) {
				case 1 -> {
					id = parseId((Integer) harbor.x() + 1, (Integer) harbor.y(), (Integer) harbor.z() - 1);
					for (Node harborRoad : harbourRoads) {
						if (harborRoad.getId().equals(id + "_0_HarbourRoad") || harborRoad.getId().equals(id + "_2_HarbourRoad")) {
							harborRoad.setVisible(true);
						}
					}
				}
				case 3 -> {
					id = parseId((Integer) harbor.x() + 1, (Integer) harbor.y() - 1, (Integer) harbor.z());
					for (Node harborRoad : harbourRoads) {
						if (harborRoad.getId().equals(id + "_2_HarbourRoad") || harborRoad.getId().equals(id + "_4_HarbourRoad")) {
							harborRoad.setVisible(true);
						}
					}
				}
				case 5 -> {
					id = parseId((Integer) harbor.x(), (Integer) harbor.y() - 1, (Integer) harbor.z() + 1);
					for (Node harborRoad : harbourRoads) {
						if (harborRoad.getId().equals(id + "_4_HarbourRoad") || harborRoad.getId().equals(id + "_6_HarbourRoad")) {
							harborRoad.setVisible(true);
						}
					}
				}
				case 7 -> {
					id = parseId((Integer) harbor.x() - 1, (Integer) harbor.y(), (Integer) harbor.z() + 1);
					for (Node harborRoad : harbourRoads) {
						if (harborRoad.getId().equals(id + "_6_HarbourRoad") || harborRoad.getId().equals(id + "_8_HarbourRoad")) {
							harborRoad.setVisible(true);
						}
					}
				}
				case 9 -> {
					id = parseId((Integer) harbor.x() - 1, (Integer) harbor.y() + 1, (Integer) harbor.z());
					for (Node harborRoad : harbourRoads) {
						if (harborRoad.getId().equals(id + "_8_HarbourRoad") || harborRoad.getId().equals(id + "_10_HarbourRoad")) {
							harborRoad.setVisible(true);
						}
					}
				}
				case 11 -> {
					id = parseId((Integer) harbor.x(), (Integer) harbor.y() + 1, (Integer) harbor.z() - 1);
					for (Node harborRoad : harbourRoads) {
						if (harborRoad.getId().equals(id + "_10_HarbourRoad") || harborRoad.getId().equals(id + "_0_HarbourRoad")) {
							harborRoad.setVisible(true);
						}
					}
				}
			}

			if (!id.equals("")) {

				ImageView imageView = (ImageView) parent.lookup("#" + id + "_HarbourImage");
				if (harbor.type() != null) {
					switch (harbor.type()) {
						case MOON_ROCK -> {
							Image image = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/moon_rock_harbor.png")).toString());
							imageView.setImage(image);
						}
						case NEPTUNE_CRYSTAL -> {
							Image image = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/neptun_crystals_harbor.png")).toString());
							imageView.setImage(image);
						}
						case MARS_BAR -> {
							Image image = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/mars_bar_harbor.png")).toString());
							imageView.setImage(image);
						}
						case VENUS_GRAIN -> {
							Image image = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/venus_grain_harbor.png")).toString());
							imageView.setImage(image);
						}
						case EARTH_CACTUS -> {
							Image image = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/earth_cactus_harbor.png")).toString());
							imageView.setImage(image);
						}
					}
				} else {
					Image image = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/harbor.png")).toString());
					imageView.setImage(image);
				}
				imageView.toFront();
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

	private String parseId(int x, int y, int z) {
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

		return stringX + stringY + stringZ;
	}

	public void zoomIn() {
		if (this.parent.getScaleX() < 2.0) {
			this.parent.setScaleX(this.parent.getScaleX() + 0.1);
			this.parent.setScaleY(this.parent.getScaleY() + 0.1);
		}
	}

	public void zoomOut() {
		if (this.parent.getScaleX() > 0.5) {
			this.parent.setScaleX(this.parent.getScaleX() - 0.1);
			this.parent.setScaleY(this.parent.getScaleY() - 0.1);
		}
	}

	public List<CircleSubController> getCirclesSubCons() {
		return this.circleSubControllers;
	}
}
