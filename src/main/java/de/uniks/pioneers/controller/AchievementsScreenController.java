package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Achievement;
import de.uniks.pioneers.service.AchievementsService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.uniks.pioneers.Constants.*;

public class AchievementsScreenController implements Controller {

    CompositeDisposable disposable;

    @FXML
    public Button backButton;
    @FXML
    public ListView<HBox> achievementsList;
    private final App app;
    private final AchievementsService achievementsService;
    private final Provider<LobbyController> lobbyController;
    private HashMap<String, Boolean> achievementExits;

    @Inject
    public AchievementsScreenController(App app, AchievementsService achievementsService, Provider<LobbyController> lobbyController) {
        this.app = app;
        this.achievementsService = achievementsService;
        this.lobbyController = lobbyController;
    }


    @Override
    public void init() {
        disposable = new CompositeDisposable();
        achievementExits = new HashMap<>();
        {
            achievementExits.put(FIRST_ROAD, false);
            achievementExits.put(ROAD_BUILDER, false);
            achievementExits.put(CITY_BUILDER, false);
            achievementExits.put(SETTLEMENT_BUILDER, false);
            achievementExits.put(TRADE_BANK, false);
            achievementExits.put(TRADE_PLAYER, false);
            achievementExits.put(CREATE_MAP, false);
            achievementExits.put(WIN_GAME, false);
            achievementExits.put(VENUS_GRAIN_PICKER, false);
            achievementExits.put(MARS_BAR_PICKER, false);
            achievementExits.put(MOON_ROCK_PICKER, false);
            achievementExits.put(EARTH_CACTUS_PICKER, false);
            achievementExits.put(NEPTUNE_CRYSTAL_PICKER, false);
        }

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

        this.initAchievementsList();
        return parent;
    }

    private void initAchievementsList() {
        disposable.add(achievementsService.listUserAchievements()
                .observeOn(FX_SCHEDULER)
                .subscribe(achievements -> {
                    for (Achievement achievement : achievements) {
                        if (ACHIEVEMENT_NAMES.containsKey(achievement.id())) {
                            this.achievementExits.replace(achievement.id(), true);
                            this.showAchievement(achievement, ACHIEVEMENT_NAMES.get(achievement.id()), ACHIEVEMENT_PATHS.get(achievement.id()));
                        }
                    }

                    for (Map.Entry<String, Boolean> entry : achievementExits.entrySet()) {
                        if (!entry.getValue()) {
                            Achievement achievement = new Achievement(null, null, null, entry.getKey(), null, 0);
                            this.showAchievement(achievement, ACHIEVEMENT_NAMES.get(entry.getKey()), ACHIEVEMENT_PATHS.get(entry.getKey()));
                        }
                    }
                }));

    }

    private void showAchievement(Achievement achievement, String achievementName, String path) {
        HBox achievementHBox = new HBox();

        // achievement image and unlocked image
        ImageView icon = new ImageView();
        ImageView unlockedImage = new ImageView();
        Image achievementImage;
        Image unlocked;
        if (Objects.equals(path, "")) {
            achievementImage = new Image(Objects.requireNonNull(App.class.getResource("view/assets/venus_grain_harbor.png")).toString());
        } else if (achievement.unlockedAt() != null) {
            achievementImage = new Image(Objects.requireNonNull(App.class.getResource("view/assets/achievements/" + path + "_unlocked" + ".png")).toString());
            unlocked = new Image(Objects.requireNonNull(App.class.getResource("view/assets/achievements/hook.png")).toString());
            unlockedImage.setImage(unlocked);
        } else {
            achievementImage = new Image(Objects.requireNonNull(App.class.getResource("view/assets/achievements/" + path + ".png")).toString());
            unlocked = new Image(Objects.requireNonNull(App.class.getResource("view/assets/achievements/x.png")).toString());
            unlockedImage.setImage(unlocked);
        }
        icon.setImage(achievementImage);
        icon.setFitHeight(40.0);
        icon.setFitWidth(40.0);
        unlockedImage.setFitHeight(40.0);
        unlockedImage.setFitWidth(40.0);

        // label of achievement name
        Label achievementNameLabel = new Label(achievementName);
        achievementNameLabel.setPrefWidth(380);
        achievementNameLabel.setAlignment(Pos.CENTER_LEFT);

        //needed label
        Label needed = new Label(ACHIEVEMENT_UNLOCK_VALUES.get(achievement.id()).toString());
        needed.setMinWidth(50);
        needed.setAlignment(Pos.CENTER);

        //current score label
        Label currentScore = new Label(achievement.progress().toString());
        currentScore.setMinWidth(50);
        currentScore.setAlignment(Pos.CENTER);

        //show in listview
        Pane buffer = new Pane();
        buffer.setPrefWidth(30);
        Pane buffer1 = new Pane();
        buffer1.setPrefWidth(50);
        achievementHBox.getChildren().addAll(icon, achievementNameLabel, unlockedImage, buffer, needed, buffer1, currentScore);
        achievementHBox.setAlignment(Pos.CENTER_LEFT);
        achievementHBox.setSpacing(30);
        achievementsList.getItems().add(achievementHBox);
    }

    public void OnBackClicked() {
        app.show(lobbyController.get());
    }
}
