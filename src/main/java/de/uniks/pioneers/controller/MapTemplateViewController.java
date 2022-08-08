package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.computation.LoadMapTemplate;
import de.uniks.pioneers.model.Harbor;
import de.uniks.pioneers.model.Map;
import de.uniks.pioneers.model.Tile;
import de.uniks.pioneers.template.HarborTemplate;
import de.uniks.pioneers.template.MapTemplate;
import de.uniks.pioneers.template.TileTemplate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapTemplateViewController implements Controller {


	@FXML
	public Label mapNameLabel;
	@FXML
	public ScrollPane mapPane;
	private final App app;
	private final MapTemplatesScreenController mapTemplatesScreenController;
	private final MapTemplate mapTemplate;

	private Map map;

	private LoadMapTemplate loadMapTemplate = new LoadMapTemplate();
	private final List<Tile> tiles = new ArrayList<>();
	private final List<Harbor> harbors = new ArrayList<>();

	public MapTemplateViewController(App app, MapTemplate mapTemplate, MapTemplatesScreenController mapTemplatesScreenController) {
		this.app = app;
		this.mapTemplate = mapTemplate;
		this.mapTemplatesScreenController = mapTemplatesScreenController;
	}

	@Override
	public void init() {
		for (TileTemplate tileTemplate : mapTemplate.tiles()) {
			tiles.add(new Tile(tileTemplate.x(), tileTemplate.y(), tileTemplate.z(), tileTemplate.type(), tileTemplate.numberToken()));
		}

		for (HarborTemplate harborTemplate : mapTemplate.harbors()) {
			harbors.add(new Harbor(harborTemplate.x(), harborTemplate.y(), harborTemplate.z(), harborTemplate.type(), harborTemplate.side()));
		}
		map = new Map(null, tiles, harbors);
	}

	@Override
	public void destroy() {

	}

	@Override
	public Parent render() {
		// load UI elements
		final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/MapTemplateView.fxml"));
		loader.setControllerFactory(c -> this);
		final Parent parent;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		this.mapPane.setContent(this.loadMapTemplate.loadMap(map, false));
		this.mapNameLabel.setText(this.mapTemplate.name());

		return parent;
	}

	public void backButtonPressed() {
		this.app.show(mapTemplatesScreenController);
	}
}
