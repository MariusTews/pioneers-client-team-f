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
import org.testfx.matcher.base.NodeMatchers;

import java.io.File;
import java.util.ArrayList;


import static org.mockito.ArgumentMatchers.any;
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

        File file = new File(Main.class.getResource("defaultPicture.png").getFile());

        String avatar = editUserController.encodeFileToBase64Binary(file);
        when(userService.findOne(any())).thenReturn(Observable.just(new User("01", "Alice","offline",
                                                                                            avatar, new ArrayList<>())));
        when(idStorage.getID()).thenReturn(("01"));

        App app = new App(editUserController);
        app.start(stage);

    }


    @Test
    void updateUser() {
        File file = new File(Main.class.getResource("defaultPicture.png").getFile());

        String avatar = editUserController.encodeFileToBase64Binary(file);

        when(userService.userUpdate(any(),any(),any(),any(),any(),any())).thenReturn(Observable.just(new User("01",
                                                                    "Alice","offline",avatar,new ArrayList<>())));
        write("\t\t\t\t\t");
        when(userService.userUpdate(any(),any(),any(),any(),any(),any())).thenReturn(Observable.just(new User("01","Alice","offline",avatar,new ArrayList<>())));
        write("\t\t\t\t\t\t");
        //check if all textfields are empty
        type(KeyCode.SPACE);
        write("\t\t1234567\t\t\t\t\t");
        type(KeyCode.SPACE);

        //check if password do not match
        verifyThat("OK", NodeMatchers.isVisible());
        type(KeyCode.SPACE);
        write("\t\t1234567\t1234567\t\t\t\t");

        //check if password is not 8 characters long
        type(KeyCode.SPACE);
        verifyThat("OK", NodeMatchers.isVisible());
        type(KeyCode.SPACE);
        write("\tAlice\t12345678\t12345678\t\t\t\t");

        //check if username and password are changed
        type(KeyCode.SPACE);
        verify(userService).userUpdate("01","Alice",avatar,new ArrayList<>(),"online","12345678");
        write("\t");
        type(KeyCode.BACK_SPACE);
        write("\t\t\t\t\t\t");

        //check if only password changed
        type(KeyCode.SPACE);
        verify(userService).userUpdate("01","Alice",avatar,new ArrayList<>(),"online","12345678");
        write("\tAlice\t");
        type(KeyCode.BACK_SPACE);
        write("\t");
        type(KeyCode.BACK_SPACE);
        write("\t\t\t\t");

        //check if only username changed
        type(KeyCode.SPACE);
        verify(userService).userUpdate("01","Alice",avatar,new ArrayList<>(),"online","12345678");
        type(KeyCode.SPACE);
    }
}
