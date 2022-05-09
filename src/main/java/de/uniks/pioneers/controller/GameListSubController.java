package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.inject.Inject;
import java.io.IOException;

public class GameListSubController implements Controller {
	@FXML
	public Label gameNameLabel;
	@FXML
	public Button joinButton;
	private Game game;
	private App app;

	@Inject
	public GameListSubController(App app, Game game){
		this.app = app;
		this.game = game;
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}

	@Override
	public Parent render() {
		final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/GamelistSubView.fxml"));
		loader.setControllerFactory(c -> this);
		final Parent parent;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		if(game != null) {
			this.gameNameLabel.setText(this.game.name());
		}

		return parent;
	}

	public void joinButtonPressed(ActionEvent event) {
	}
}

