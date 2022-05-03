package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;

public class JoinGameController implements Controller {
    @FXML public Button backButtonJoinGame;
    @FXML public Button joinButton;
    @FXML public TextField passwordTextField;

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public Parent render() {

        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/JoinGameScreen.fxml"));
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

    public void backButtonJoinGamePressed(ActionEvent event) {
    }

    public void joinButtonPressed(ActionEvent event) {
    }
}
