package de.uniks.pioneers;

import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.controller.GameLobbyController;
import de.uniks.pioneers.controller.GameScreenController;
import de.uniks.pioneers.controller.LobbyController;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Locale;
import java.util.Objects;

public class App extends Application {

    private Stage stage;

    private Controller controller;

    public App() {
        final MainComponent mainComponent = DaggerMainComponent.builder().mainapp(this).build();
        controller = mainComponent.loginController();
    }

    public App(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void start(Stage primaryStage) {
        Locale.setDefault(new Locale("English", "England"));
        this.stage = primaryStage;
        stage.setWidth(900);
        stage.setHeight(600);
        stage.setTitle("Pioneers");

        final Scene scene = new Scene(new Label("Loading ... "));
        stage.setScene(scene);

        // Load css into the scene for the design
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/MainStyle.css")).toString());
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/LoginSignupStyle.css")).toString());
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/LobbyStyle.css")).toString());
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/EditUserStyle.css")).toString());
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/RulesStyle.css")).toString());
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/CreateGameStyle.css")).toString());
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/GameLobbyStyle.css")).toString());
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/ChatStyle.css")).toString());
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/IngameStyle.css")).toString());

        setAppIcon(stage);

        primaryStage.show();
        if (controller != null) {
            show(controller);
        }
    }

    private void setAppIcon(Stage stage) {
        final Image image = new Image(Objects.requireNonNull(App.class.getResource("FATARI_logo.png")).toString());
        stage.getIcons().add(image);
    }

    @SuppressWarnings("unused")
    private void setTaskbarIcon() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        try {
            final Taskbar taskbar = Taskbar.getTaskbar();
            final java.awt.Image image = ImageIO.read(Objects.requireNonNull(Main.class.getResource("FATARI_logo.png")));
            taskbar.setIconImage(image);
        } catch (Exception ignored) {

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
        if (controller.getClass().equals(GameScreenController.class)) {
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            Scale scale = new Scale();
            Pane pane = (Pane) controller.render();
            // wide screens
            if ((bounds.getHeight() / 950) * 1600 < bounds.getWidth()) {
                double scaleFactor = bounds.getHeight() / 950;
                scale.setX(scaleFactor);
                scale.setY(scaleFactor);
                Group group = new Group(pane);
                group.getTransforms().add(scale);
                stage.getScene().setRoot(group);
                stage.setWidth(scaleFactor * 1600);
                stage.setHeight(bounds.getHeight());
                stage.centerOnScreen();

            }
            // "higher" screens
            else {
                double scaleFactor = bounds.getWidth() / 1600;
                scale.setX(scaleFactor);
                scale.setY(scaleFactor);
                Group group = new Group(pane);
                group.getTransforms().add(scale);
                stage.getScene().setRoot(group);
                stage.setWidth(bounds.getWidth());
                stage.setHeight(scaleFactor * 950);
                stage.centerOnScreen();
            }
        } else {
            stage.getScene().setRoot(controller.render());
            if (controller.getClass().equals(LobbyController.class)) {
                stage.setWidth(900);
                stage.setHeight(600);
            } else if (controller.getClass().equals(GameLobbyController.class)) {
                stage.setWidth(1010);
                stage.setHeight(600);
            }
        }
    }

    private void cleanup() {
        if (controller != null) {
            controller.destroy();
            controller = null;
        }
    }

    public Stage getStage() {
        return this.stage;
    }
}
