package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSubViewTest extends ApplicationTest {

    @Spy
    IDStorage idStorage;

    @Mock
    GameFieldSubController gameFieldSubController;
    @Mock
    UserService userService;


    HashMap<String, Integer> hm = new HashMap<>(){{
      put("wool", 2);
      put("grain", 3);
      put("ore", 2);
      put("lumber", 2);
      put("brick", 6);
    }
    };



    @InjectMocks
    UserSubView userSubView = new UserSubView(idStorage,userService,new Player("id","2",
            "#000000", true,2, hm,new HashMap<>(),2,2,null),2,gameFieldSubController);
    ;


    public void start(Stage stage){
        when(idStorage.getID()).thenReturn("2");
        when(userService.findAllUsers()).thenReturn(Observable.just(List.of(
                new User("1234","12345","2","testus","online",null, new ArrayList<>()))));

        userSubView = new UserSubView(idStorage,userService,new Player("id","2",
                "#000000", true,2, hm,new HashMap<>(),2,10,null),2,gameFieldSubController);

        final App app = new App(userSubView);
        app.start(stage);
        testParameters();
    }


    @Test
    public void testParameters(){

        Label userNameLabel = lookup("#name").query();
        Assertions.assertThat(userNameLabel.getText().equals("Name"));

        Label victoryPoints = lookup("#victoryPoints").query();
        Assertions.assertThat(victoryPoints.getText().equals(""));

        Label item1 = lookup("#item1").query();
        Assertions.assertThat(item1.getText().equals(""));

        Label item2 = lookup("#item2").query();
        Assertions.assertThat(item2.getText().equals(""));

        Label item3 = lookup("#item3").query();
        Assertions.assertThat(item3.getText().equals(""));

        Label item4 = lookup("#item4").query();
        Assertions.assertThat(item4.getText().equals(""));

        Label item5 = lookup("#item5").query();
        Assertions.assertThat(item5.getText().equals(""));

    }

}
