package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.io.IOException;


public class MessageViewSubController implements Controller {

    @FXML
    Button sendButton;
    @FXML
    ScrollPane messageScrollPane;
    @FXML
    VBox messageView;
    @FXML
    TextField messageTextField;

    @Inject
    public MessageViewSubController() {
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        // Show the chat with text field and send button
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/MessageSubView.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return parent;
    }

}
