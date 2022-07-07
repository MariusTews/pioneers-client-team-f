package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;


@ExtendWith(MockitoExtension.class)
class MemberListSubcontrollerTest extends ApplicationTest {
    @InjectMocks
    MemberListSubcontroller memberListSubcontroller;

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
        Assertions.assertEquals("", name.getText());
        Assertions.assertEquals("", ready.getText());
    }
}
