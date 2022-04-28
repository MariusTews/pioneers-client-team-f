package de.uniks.pioneers;

import de.uniks.pioneers.controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;

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

        setAppIcon(stage);

        primaryStage.show();
    }

    private void setAppIcon(Stage stage)
    {
        final Image image = new Image(App.class.getResource("FATARI_logo.png").toString());
        stage.getIcons().add(image);
    }

    private void setTaskbarIcon()
    {
        if (GraphicsEnvironment.isHeadless())
        {
            return;
        }

        try {
            final Taskbar taskbar = Taskbar.getTaskbar();
            final java.awt.Image image = ImageIO.read(Main.class.getResource("FATARI_logo.png"));
            taskbar.setIconImage(image);
        }
        catch (Exception ignored)
        {

        }
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
