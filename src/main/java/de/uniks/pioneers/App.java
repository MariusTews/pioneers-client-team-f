package de.uniks.pioneers;

import de.uniks.pioneers.controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class App extends Application {

    private Stage stage;

    private Controller controller;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setWidth(640);
        stage.setHeight(480);
        stage.setTitle("Pioneers");

        final Scene scene = new Scene(new Label("Loading ... "));
        stage.setScene(scene);

        primaryStage.show();
    }

    @Override
    public void stop() {
        cleanup();
    }

    public void show(Controller controller) {
        cleanup();
        this.controller = controller;
        controller.init();
        stage.getScene().setRoot(controller.render());
    }
    private void cleanup() {
        if (controller != null) {
            controller.destroy();
            controller = null;
        }
    }
}
