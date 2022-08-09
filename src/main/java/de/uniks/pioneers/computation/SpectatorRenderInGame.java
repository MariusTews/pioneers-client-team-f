package de.uniks.pioneers.computation;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.SpectatorViewController;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class SpectatorRenderInGame {

    List<User> allUsers = new ArrayList<>();
    private SpectatorViewController spectatorViewController;

    @Inject
    public SpectatorRenderInGame() {

    }

    public void renderSingleSpectator(List<User> allUser, String s, Label spectatorTitleId) {
        for (User user : allUser) {
            if (s.equals(user._id())) {
                spectatorTitleId.setText(user.name());
            }
        }
    }

    public void checkMember(ObservableList<Member> spectatorMember, Pane spectatorPaneId, List<User> allUser, Label spectatorTitleId, ImageView arrowImageId) {
        this.allUsers = allUser;
        if (spectatorTitleId.getText().equals("Spectator")) {
            if (spectatorMember.size() == 1) {
                arrowImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/left.png"))));
                renderSingleSpectator(allUser, spectatorMember.get(0).userId(), spectatorTitleId);
            } else {
                renderSingleSpectator(allUser, spectatorMember.get(0).userId(), spectatorTitleId);
                arrowImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/left.png"))));
                Member member = spectatorMember.get(0);
                spectatorMember.remove(0);
                spectatorPaneId.getChildren().setAll(spectatorMember.stream().map(this::renderSpectator).toList());
                spectatorMember.add(0, member);
            }
        } else {
            spectatorTitleId.setText("Spectator");
            arrowImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/right.png"))));
            spectatorPaneId.getChildren().clear();
        }
    }

    private Node renderSpectator(Member member) {
        for (User user : allUsers) {
            if (member.userId().equals(user._id())) {
                this.spectatorViewController = new SpectatorViewController(user);
                break;
            }
        }
        return spectatorViewController.render();
    }
}
