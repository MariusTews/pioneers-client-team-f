package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.io.IOException;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class MemberListSubcontroller implements Controller {
    @FXML
    public ImageView idAvatar;
    @FXML
    public Label idUsername;
    @FXML
    public Label idReady;
    private App app;
    private Member member;

    private final UserService userService;

    @Inject
    public MemberListSubcontroller(App app, Member member, UserService userService) {
        this.app = app;
        this.member = member;
        this.userService = userService;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/MemberListSubView.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (this.member != null) {
            // set username and avatar
            this.userService
                    .findOne(this.member.userId())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(result -> {
                        this.idUsername.setText(result.name());

                        if (result.avatar() != null) {
                            this.idAvatar.setImage(new Image(result.avatar()));
                        }
                    });

            // set ready
            if (this.member.ready()) {
                this.idReady.setText("- ready -");
                this.idReady.setTextFill(Color.GREEN);
            } else {
                this.idReady.setText("- not ready -");
                this.idReady.setTextFill(Color.RED);
            }
        }

        return parent;
    }
}
