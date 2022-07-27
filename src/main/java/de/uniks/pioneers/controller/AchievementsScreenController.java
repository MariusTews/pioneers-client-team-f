package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import javax.inject.Inject;
import java.io.IOException;

public class AchievementsScreenController implements Controller{

    @FXML
    public Button backButton;
    @FXML
    public ListView<HBox> achievementsList;

    @Inject
    public AchievementsScreenController() {

    }


    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/AchievementsScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        achievementsList.setPrefHeight(400);
        return parent;
    }

    public void OnBackClicked(ActionEvent actionEvent) {

    }
}
