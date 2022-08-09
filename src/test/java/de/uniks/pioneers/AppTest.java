package de.uniks.pioneers;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.assertions.api.Assertions.assertThat;


public class AppTest extends ApplicationTest {


    @Override
    public void start(Stage stage) {
        final App app = new App(null);
        MainComponent testComponent = DaggerTestComponent.builder().mainapp(app).build();

        app.start(stage);
        app.show(testComponent.loginController());
    }

    @Test
    public void criticalPath() {

        //test SignUp
        write("\t\t\t");
        type(KeyCode.SPACE);
        write("Alice\t");
        write("00000000\t");
        write("00000000\t");

        type(KeyCode.SPACE);
        type(KeyCode.SPACE);

        //test Login
        write("test\t");
        write("00000000\t\t");
        type(KeyCode.SPACE);

        //test RulesScreen
        type(KeyCode.SPACE);
        type(KeyCode.SPACE);

        type(KeyCode.DOWN);
        type(KeyCode.SPACE);
        //test EditUser
        write("\tAlice123\t");
        write("123456789\t");
        write("123456789\t\t\t");
        type(KeyCode.SPACE);

        write("\t\t\t\t\t\t\t");
        type(KeyCode.SPACE);

        //test CreateGameScreen
        write("testGame\t");
        write("12\t");
        type(KeyCode.SPACE);
        write("\t\t");
        for (int i = 0; i < 7; i++) {
            type(KeyCode.SPACE);
        }
        write("\t\t\t");

        type(KeyCode.SPACE);

        //test gameLobby
        WaitForAsyncUtils.waitForFxEvents();


        write("\t\t\t\t\t\t");
        type(KeyCode.SPACE);
        WaitForAsyncUtils.waitForFxEvents();
        sleep(5000);
        //test ingameScreen
        WaitForAsyncUtils.waitForFxEvents();
        // founding-settlement-1
        clickOn("#x0y0z0_6");
        //founding-road-1
        clickOn("#x0y0z0_7");
        //founding-settlement-2
        clickOn("#x0y0z0_0");
        //founding-road-2
        clickOn("#x1y0zM1_7");
        //roll dice
        clickOn("#roll");
        //build road
        clickOn("#road");
        clickOn("#x1yM1z0_11");
        //build settlement
        clickOn("#sett");
        clickOn("#x1yM1z0_0");
        WaitForAsyncUtils.waitForFxEvents();
        //back to lobbyScreen
        clickOn("#CloseGameButton");
        //back to loginScreen
        clickOn("#logoutButton");
        WaitForAsyncUtils.waitForFxEvents();
        //screenAssert login
        Button loginButton = lookup("#loginButton").query();
        assertThat(loginButton).isVisible();


    }
}
