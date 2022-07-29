package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.PioneersService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.HashMap;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OpponentSubControllerTest extends ApplicationTest {
    // Create resources hash
    @Mock
    PioneersService pioneersService ;
    final  HashMap<String, Integer> resources = new HashMap<>() {{
        put("unknown", 3);
    }};
    // Get the path of an image
    final String avatar = Objects.requireNonNull(App.class.getResource("defaultPicture.png")).toString();


    @InjectMocks
    OpponentSubController opponentSubController = new OpponentSubController(new Player("01", "00",
            Color.DARKORCHID.toString(), true,6, resources, null,2,2,null, null),
            new User("1234","12345","00", "bob", "online", avatar, null), 4, pioneersService);

    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {
        // start application with the created opponent sub controller
        when(pioneersService.findAllPlayers(any())).thenReturn(Observable.empty());
        when(pioneersService.findOnePlayer(any(),any())).thenReturn(Observable.empty());
        opponentSubController = new OpponentSubController(new Player("01", "00",
                Color.DARKORCHID.toString(), true,6, resources, null,2,2,null, null),
                new User("1234","12345","00", "bob", "online", avatar, null), 4, pioneersService);
        final App app = new App(opponentSubController);
        app.start(stage);
    }

    @Test
    public void opponentViewTest() {
        // Test if all labels and the avatar are set
        Label username = lookup("#usernameLabel").query();
        Label totalResources = lookup("#totalResourcesLabel").query();
        Label victoryPointsLabel = lookup("#victoryPointsLabel").query();
        ImageView avatarImageView = lookup("#avatarImageView").query();
        Image avatarImage = avatarImageView.getImage();
        VBox vBox = (VBox) avatarImageView.getParent().getParent();

        Assertions.assertThat(username.getText()).isEqualTo("bob");
        Assertions.assertThat(username.getTextFill()).isEqualTo(Color.DARKORCHID);
        Assertions.assertThat(totalResources.getText()).isEqualTo("Total resources: 3");
        Assertions.assertThat(victoryPointsLabel.getText()).isEqualTo("VP: 2/4");
        Assertions.assertThat(opponentSubController.getId()).isEqualTo("00");
        Assertions.assertThat(avatarImage.getUrl()).isEqualTo(avatar);
        Assertions.assertThat(vBox).isEqualTo(opponentSubController.getParent());
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        opponentSubController = null;
        resources.clear();

    }
}
