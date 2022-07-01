package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.User;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class SpectatorViewControllerTest extends ApplicationTest {


    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage){
        User user = new User(null,null,"2","Tia","offline",null,new ArrayList<>());
        SpectatorViewController spectatorViewController = new SpectatorViewController(user);
        final App app = new App(spectatorViewController);
        app.start(stage);
    }

    @Test
    public void testParameters(){
        Label userNameLabel = lookup("#spectatorNameId").query();
        Assertions.assertThat(userNameLabel.getText().equals("Tia"));
    }
}
