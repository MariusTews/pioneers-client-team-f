package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.io.IOException;

public class TimerController implements Controller {
    private final GameScreenController gameScreenController;
    private Timeline timeline = new Timeline();

    public TimerController(GameScreenController gameScreenController) {
        this.gameScreenController = gameScreenController;
    }

    @FXML
    public Label timerLabel;

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/TimerSubView.fxml"));
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

    public void startTime() {
        if (this.timeline == null) {
            this.timeline = new Timeline();
        }
        // starting time
        final Integer[] startTime = {180};
        final Integer[] seconds = {startTime[0]};

        timeline.setCycleCount(Timeline.INDEFINITE);

        //gets called every second to reduce the timer by one second
        KeyFrame frame = new KeyFrame(Duration.seconds(1), event -> {
            seconds[0]--;
            if (seconds[0] % 60 > 9) {
                timerLabel.setText("" + (seconds[0] / 60) + ":" + seconds[0] % 60);
            } else {
                timerLabel.setText("" + (seconds[0] / 60) + ":0" + seconds[0] % 60);
            }

            if (seconds[0] <= 0) {
                // handle action automatically when time expired
                timeline.stop();
                this.gameScreenController.handleExpiredTime();
            }
        });
        timeline.getKeyFrames().setAll(frame);
        // start timer
        timeline.playFromStart();
    }

    public void countUp() {
        Timeline timeline = new Timeline();
        final Integer[] startTime = {0};
        final Integer[] seconds = {startTime[0]};

        timeline.setCycleCount(Timeline.INDEFINITE);

        //gets called every second to reduce the timer by one second
        KeyFrame frame = new KeyFrame(Duration.seconds(1), event -> {
            seconds[0]++;
            if (seconds[0] % 60 > 9) {
                timerLabel.setText("" + (seconds[0] / 60) + ":" + seconds[0] % 60);
            } else {
                timerLabel.setText("" + (seconds[0] / 60) + ":0" + seconds[0] % 60);
            }
        });

        timeline.getKeyFrames().setAll(frame);
        // start timer
        timeline.playFromStart();
    }
}
