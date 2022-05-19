package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EditUserPictureTest extends ApplicationTest {

    @Mock
    UserService userService;

    @Spy
    IDStorage idStorage;


    @InjectMocks
    EditUserController editUserController;

    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {

        File file = new File(Main.class.getResource("defaultPicture.png").getFile());

        String avatar = editUserController.encodeFileToBase64Binary(file);
        when(userService.findOne(any())).thenReturn(Observable.just(new User("01", "Alice","offline",avatar)));
        when(idStorage.getID()).thenReturn(("01"));

        App app = new App(editUserController);
        app.start(stage);

    }

    @Test
    void changePicture() {
        clickOn("#userPicture");
        type(KeyCode.ESCAPE);
        rightClickOn("#userPicture");
        type(KeyCode.SPACE);
    }

}
