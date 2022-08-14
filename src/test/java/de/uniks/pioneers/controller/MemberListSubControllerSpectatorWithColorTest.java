package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;


@ExtendWith(MockitoExtension.class)
class MemberListSubControllerSpectatorWithColorTest extends ApplicationTest {

    Member member = new Member("", "", "", "", false, Color.BROWN.toString(), true);
    User user = new User("", "", "", "Karl", "online", null, null);
    MemberListSubController memberListSubcontroller = new MemberListSubController(member, user);

    public void start(Stage stage) {
        App app = new App(memberListSubcontroller);
        app.start(stage);
    }

    @Test
    public void testViewParameters() {
        ImageView avatar = lookup("#idAvatar").query();
        Label name = lookup("#idUsername").query();
        Label ready = lookup("#idReady").query();

        Assertions.assertEquals("ImageView[id=idAvatar, styleClass=image-view]", avatar.toString());
        Assertions.assertEquals("(Spectator)", name.getText());
        Assertions.assertEquals("Karl", ready.getText());
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        memberListSubcontroller = null;
    }
}
