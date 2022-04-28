package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;

public class LobbyController implements Controller {
	@FXML public Button rulesButton;
	@FXML public Label userWelcomeLabel;
	@FXML public Button logoutButton;
	@FXML public ListView userListView;
	@FXML public TabPane tabPane;
	@FXML public Tab allTab;
	@FXML public TextField chatMessageField;
	@FXML public Button sendButton;
	@FXML public ListView gameListView;
	@FXML public Button editUserButton;
	@FXML public Button createGameButton;

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}

	@Override
	public Parent render() {

		final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/LobbyScreen.fxml"));
		loader.setControllerFactory(c -> this);
		final Parent parent;
		try{
			parent = loader.load();
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return parent;
	}

	public void rulesButtonPressed(ActionEvent event) {

	}

	public void logoutButtonPressed(ActionEvent event) {
	}

	public void sendButtonPressed(ActionEvent event) {
	}

	public void editButtonPressed(ActionEvent event) {
	}

	public void createGameButtonPressed(ActionEvent event) {
	}
}
