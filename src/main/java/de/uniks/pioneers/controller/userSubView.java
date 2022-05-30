package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.io.IOException;

public class userSubView implements Controller {
    public Label name;
    public Label victoryPoints;
    public Label item1;
    public Label item2;
    public Label item3;
    public Label item4;
    public Label item5;

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/UserSubView.fxml"));
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

    public void onSett(ActionEvent actionEvent) {
    }

    public void onRoad(ActionEvent actionEvent) {
    }

    public void onCity(ActionEvent actionEvent) {
    }
}
