package de.uniks.pioneers.computation;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.ColorController;
import de.uniks.pioneers.controller.GameScreenController;
import de.uniks.pioneers.controller.MemberListSubController;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.MemberService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class GameLobbyInformation {
    @Inject
    public GameLobbyInformation() {

    }

    public void giveYourSelfColour(ObservableList<Member> members, MemberService memberService, IDStorage idStorage) {

        ColorController controller = new ColorController();
        List<Label> colors = controller.getColor();

        List<String> allColors = new ArrayList<>(createdColors(colors));

        //remove color from allColors if it belongs to a member
        for (Member member : members) {
            if (member.color() != null && !member.spectator()) {
                allColors.remove(member.color());
            }
        }


        //give color to member that do not have colors
        for (Member member : members) {
            if ((member.color() == null || member.color().equals("#000000") )
                    && member.userId().equals(idStorage.getID()) && !member.spectator()) {
                memberService.statusUpdate(member.gameId(), member.userId(), member.ready(), allColors.get(0), false).observeOn(FX_SCHEDULER)
                        .subscribe();
                break;
            }
        }
    }

    private List<String> createdColors(List<Label> createdColors) {
        List<String> colorNames = new ArrayList<>();
        for (Label label : createdColors) {
            String colorInString = "#" + Color.web(label.getText().toLowerCase()).toString().substring(2, 8);
            colorNames.add(colorInString);
        }

        return colorNames;
    }

    public void addColourOnComboBox(ComboBox<Label> comboBox) {
        ObservableList<Label> items = FXCollections.observableArrayList(color());
        comboBox.getItems().addAll(items);
        comboBox.getSelectionModel().clearSelection(0);
        comboBox.setVisibleRowCount(300);
        //This makes sure the color are presented in the strings and
        //border will be shown on Labels
        comboBox.setCellFactory(listView -> new ListCell<>() {
            public void updateItem(Label label, boolean empty) {
                super.updateItem(label, empty);
                if (label != null) {
                    if (label.getText().equals("Select Color")) {
                        setText(null);
                    }
                    setText(label.getText());
                    setTextFill(label.getTextFill());
                    setMinWidth(label.getMinWidth());
                } else {
                    setText(null);
                }

            }
        });
    }

    private List<Label> color() {
        final ColorController controller = new ColorController();
        return controller.getColor();
    }

    public void colorPicked(ComboBox<Label> colorPicker, MemberService memberService, GameStorage gameStorage, IDStorage idStorage) {
        Label label = colorPicker.getSelectionModel().getSelectedItem();

        Color c = Color.web(label.getText().toLowerCase());
        String pickedColor = "#" + c.toString().substring(2, 8);

        boolean chose = true;
        boolean ready = false;
        boolean spectator = false;
        List<Member> memberList = memberService.getAllGameMembers(gameStorage.getId()).blockingFirst();
        for (Member member : memberList) {
            if (member.color() != null && member.color().equals(pickedColor)) {
                chose = false;
            }
            if (member.userId().equals(idStorage.getID())) {
                ready = member.ready();
                spectator = member.spectator();
            }
        }
        if (chose) {
            memberService.statusUpdate(gameStorage.getId(), idStorage.getID(), ready, pickedColor, spectator).
                    observeOn(FX_SCHEDULER).subscribe();
        }
    }

    public void checkBoxClicked(MemberService memberService, GameStorage gameStorage, IDStorage idStorage, ComboBox<Label> colorPicker, Button idReadyButton) {
        //get all the members that are currently in the game
        List<Member> memberList = memberService.getAllGameMembers(gameStorage.getId()).blockingFirst();
        boolean ready = false;
        boolean spectator = false;
        for (Member member : memberList) {
            if (member.userId().equals(idStorage.getID())) {
                ready = member.ready();
                spectator = member.spectator();
            }
        }

        if (!spectator) {
            //makes ready button invisible
            idReadyButton.setText("Not Ready");
            idReadyButton.setDisable(true);

            colorPicker.setDisable(true);
            memberService.statusUpdate(gameStorage.getId(), idStorage.getID(), true, "#000000", true)
                    .subscribe();
        } else {
            //makes ready button visible
            idReadyButton.setText("Ready");
            idReadyButton.setDisable(false);
            colorPicker.setDisable(false);
            memberService.statusUpdate(gameStorage.getId(), idStorage.getID(), !ready, null, false)
                    .subscribe();
        }
    }

    public void changeView(Provider<GameScreenController> gameScreenController, Button idStartGameButton, App app) {
        Timeline timeline = new Timeline();
        final Integer[] countdown = {4};
        timeline.setCycleCount(10);

        //gets called every second to reduce the timer by one second
        KeyFrame frame = new KeyFrame(Duration.seconds(1), ev -> {

            if (countdown[0] >= 2) {
                idStartGameButton.setText(String.valueOf(countdown[0] - 1));

            }
            if (countdown[0] < 2) {
                idStartGameButton.setText("GO");

            }
            if (countdown[0] == 0) {
                timeline.stop();
                final GameScreenController controller = gameScreenController.get();
                app.show(controller);
            }
            countdown[0]--;
        });

        timeline.getKeyFrames().setAll(frame);
        // start timer
        timeline.playFromStart();
    }

    public Node renderMember(Member member, ObservableList<User> playerList, Label playersNumberId, ObservableList<Member> members) {
        //sets the size of player
        //This flag will make sure,
        //Null Pointer exception will not be thrown
        //,if there are more Players
        MemberListSubController memberListSubcontroller = null;
        boolean ch = false;
        playersNumberId.setText("Players " + members.size() + "/6");
        for (User user : playerList) {
            if (user._id().equals(member.userId()) && !member.spectator()) {
                ch = true;
                memberListSubcontroller = new MemberListSubController(member, user);
                break;
            }
        }
        if (!ch) {
            memberListSubcontroller = new MemberListSubController(null, null);
        }
        return memberListSubcontroller.render();

    }

    public Node renderSpectatorMember(Member member, ObservableList<User> playerList) {
        boolean ch = false;
        MemberListSubController memberListSpectatorSubController = null;
        for (User user : playerList) {
            if (user._id().equals(member.userId()) && member.spectator()) {
                ch = true;
                memberListSpectatorSubController = new MemberListSubController(member, user);
                break;
            }
        }
        if (!ch) {
            memberListSpectatorSubController = new MemberListSubController(null, null);
        }
        return memberListSpectatorSubController.render();
    }
}
