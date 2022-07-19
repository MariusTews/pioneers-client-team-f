package de.uniks.pioneers;

import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.Message;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;


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

        write("\t\t\t\t\t\t");
        type(KeyCode.SPACE);

        //test CreateGameScreen
        write("testGame\t");
        write("12\t");
        type(KeyCode.SPACE);
        write("\t\t");
        System.out.println();
        for(int i =0;i<7;i++){
            type(KeyCode.SPACE);
        }
        System.out.println();
        write("\t\t");

        type(KeyCode.SPACE);

        //test gameLobby
        WaitForAsyncUtils.waitForFxEvents();
        System.out.println();


        write("\t\t\t\t\t\t");
        System.out.println();
        type(KeyCode.SPACE);
        TestModule.messageSubject.onNext(new Event<>("state.created", new Message("2022-11-30T18:35:24.00Z","1","89","01","state.created")));
        WaitForAsyncUtils.waitForFxEvents();
        sleep(7000);

    }
}
