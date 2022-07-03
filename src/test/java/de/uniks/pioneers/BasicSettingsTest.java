package de.uniks.pioneers;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javax.imageio.ImageIO;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BasicSettingsTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        // start application
        this.stage = stage;
        App app = new App();
        app.start(stage);
    }

    @Test
    public void initialTest() {
        assertTrue(true);
    }

    @Test
    public void testWindowIcon() {

        Image image1 = new Image(Objects.requireNonNull(App.class.getResource("FATARI_logo.png")).toString());
        Image image2 = stage.getIcons().get(0);

        for (int i = 0; i < image1.getWidth(); i++) {
            for (int j = 0; j < image1.getHeight(); j++) {

                assertEquals(image1.getPixelReader().getColor(i, j), image2.getPixelReader().getColor(i, j));
            }
        }
    }

    @Test
    public void testTaskIcon() {
        try {
            java.awt.Image image = ImageIO.read(Objects.requireNonNull(Main.class.getResource("FATARI_logo.png")));
            assertEquals(java.awt.Taskbar.getTaskbar().getIconImage(), image);
        }
        catch (Exception ignored) {
        }
    }
}
