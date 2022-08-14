package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
class EditUserControllerTest extends ApplicationTest {

    @Mock
    UserService userService;

    @Spy
    IDStorage idStorage;

    @InjectMocks
    EditUserController editUserController;

    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {

        File file = new File(Objects.requireNonNull(Main.class.getResource("defaultPicture.png")).getFile());

        String avatar = editUserController.encodeFileToBase64Binary(file);
        when(userService.findOne(any())).thenReturn(Observable.just(new User("1234", "12345", "01", "Alice", "offline",
                avatar, new ArrayList<>())));
        when(idStorage.getID()).thenReturn(("01"));

        App app = new App(editUserController);
        app.start(stage);

    }

    @Test
    void deletePicture() {
        write("\t\t\t\t\t");
        type(KeyCode.SPACE);
        type(KeyCode.RIGHT);
        type(KeyCode.SPACE);

        Image image = new Image(String.valueOf(Main.class.getResource("defaultPicture.png")));

        assertEquals(image.getUrl(), this.editUserController.userPicture.getImage().getUrl());
    }

    @Test
    void deleteUser() {
        when(userService.delete(any())).thenReturn(Observable.just(new User("1234", "12345", "01", "Alice", "offline", null, new ArrayList<>())));

        write("\t\t\t\t");
        type(KeyCode.SPACE);
        type(KeyCode.SPACE);
        verify(userService).delete("01");
    }

    @Test
    void updateUser() {
        File file = new File(Objects.requireNonNull(Main.class.getResource("defaultPicture.png")).getFile());

        String avatar = editUserController.encodeFileToBase64Binary(file);
        when(userService.userUpdate(any(), any(), any(), any(), any(), any())).thenReturn(Observable.just(new User("1234", "12345", "01", "Alice", "offline", avatar, new ArrayList<>())));
        write("\t\t\t\t\t\t");
        //check if all text-fields are empty
        type(KeyCode.SPACE);
        write("\t\t\t1234567\t\t\t\t");
        type(KeyCode.SPACE);

        //check if password do not match
        verifyThat("OK", NodeMatchers.isVisible());
        type(KeyCode.SPACE);
        write("\t\t\t1234567\t1234567\t\t\t");

        //check if password is not 8 characters long
        type(KeyCode.SPACE);
        verifyThat("OK", NodeMatchers.isVisible());
        type(KeyCode.SPACE);
        write("\t\tAlice\t12345678\t12345678\t\t\t");

        //check if username and password are changed
        type(KeyCode.SPACE);
        verify(userService).userUpdate("01", "Alice", avatar, new ArrayList<>(), "online", "12345678");
        write("\t\t");
        type(KeyCode.BACK_SPACE);
        write("\t\t\t\t\t");

        //check if only password changed
        type(KeyCode.SPACE);
        verify(userService).userUpdate("01", "Alice", avatar, new ArrayList<>(), "online", "12345678");
        write("\t\tAlice\t");
        type(KeyCode.BACK_SPACE);
        write("\t");
        type(KeyCode.BACK_SPACE);
        write("\t\t\t");

        //check if only username changed
        type(KeyCode.SPACE);
        verify(userService).userUpdate("01", "Alice", avatar, new ArrayList<>(), "online", "12345678");
        type(KeyCode.SPACE);
    }

    @Test
    void avatarTooBig() {
        File file = new File(Objects.requireNonNull(Main.class.getResource("FATARI_logo.png")).getFile());
        editUserController.setPictureFile(file);
        clickOn("Confirm");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("OK");
    }

    @Test
    void usernameAlreadyTaken() {
        when(userService.userUpdate(any(), any(), any(), any(), any(), any())).thenReturn(Observable.error(new Throwable("HTTP 409 ")));
        clickOn("Confirm");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("OK");
    }

    @Test
    void usernameTooLong() {
        when(userService.userUpdate(any(), any(), any(), any(), any(), any())).thenReturn(Observable.error(new Throwable("HTTP 400 ")));
        clickOn("Confirm");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("OK");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        editUserController = null;
        idStorage = null;
        userService = null;
    }
}
