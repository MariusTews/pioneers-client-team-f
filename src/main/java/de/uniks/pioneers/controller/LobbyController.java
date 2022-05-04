package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.io.IOException;

public class LobbyController implements Controller {
    @FXML
    public Button rulesButton;
    @FXML
    public Label userWelcomeLabel;
    @FXML
    public Button logoutButton;
    @FXML
    public ListView userListView;
    @FXML
    public TabPane tabPane;
    @FXML
    public Tab allTab;
    @FXML
    public TextField chatMessageField;
    @FXML
    public Button sendButton;
    @FXML
    public ListView gameListView;
    @FXML
    public Button editUserButton;
    @FXML
    public Button createGameButton;

    //added an empty Constructor with the Annotation Inject for later use
    @Inject
    public LobbyController() {

    }

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
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        sendButton.setOnAction(this::sendButtonPressed);
        return parent;
    }

    public void rulesButtonPressed(ActionEvent event) {

    }

    public void logoutButtonPressed(ActionEvent event) {
    }

    public void sendButtonPressed(ActionEvent event) {
        checkMessageField();
    }

    public void editButtonPressed(ActionEvent event) {
    }

    public void createGameButtonPressed(ActionEvent event) {
    }

    public void enterKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            checkMessageField();
        }
    }

    private void checkMessageField() {
        if (!chatMessageField.getText().isEmpty()) {
            ((VBox) ((ScrollPane) this.allTab.getContent()).getContent()).getChildren().add(new Label("username: " + chatMessageField.getText()));
            chatMessageField.setText("");
        }
    }
}
