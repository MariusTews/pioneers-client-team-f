package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;


@ExtendWith(MockitoExtension.class)
class MemberListSubControllerNoSpectatorTest extends ApplicationTest {

    Member member = new Member("", "", "", "", false, null, false);
    String avatar = "data:image/png;base64,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";
    User user = new User("", "", "", "Karl", "online", avatar, null);
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
        Assertions.assertEquals("Karl", name.getText());
        Assertions.assertEquals("", ready.getText());
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        memberListSubcontroller = null;
    }
}
