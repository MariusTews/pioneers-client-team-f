package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.service.GameLobbyService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class GameLobbyController implements Controller{

    private final GameLobbyService gameLobbyService;

    @FXML public Text idTitle;
    @FXML public Button idLeaveButton;
    @FXML public ScrollPane idUserInLobby;
    @FXML public AnchorPane idMessageArea;
    @FXML public TextField idMessageField;
    @FXML public Button idSendButton;
    @FXML public Button idReadyButton;
    @FXML public Button idStartGameButton;

    public GameLobbyController(GameLobbyService gameLobbyService) {
        this.gameLobbyService = gameLobbyService;
    }
    @Override
    public void init() {
    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/GameLobbyScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;

        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // disable start game button when entering lobby
        idStartGameButton.disableProperty().set(true);

        return parent;
    }

    public void leave(ActionEvent event) {
        System.out.println("leave pressed");
    }

    public void send(ActionEvent event) {
        System.out.println("send pressed");
    }

    public void ready(ActionEvent event) {
        System.out.println("ready pressed");
    }

    public void startGame(ActionEvent event) {
        System.out.println("startGame pressed");
    }
}