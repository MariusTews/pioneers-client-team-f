package de.uniks.pioneers.controller;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ColorControllerTest extends ApplicationTest {


    ColorController colorcontroller;

    @BeforeEach
    public void setup(){
        colorcontroller = mock(ColorController.class);
    }

    @Test
    public void coloTest(){
        //Todo: test makes no sense and needs to be adjusted
        Label a1 = new Label();
        a1.setText("GREEN");
        a1.setTextFill(Color.GREEN);
        a1.setMinWidth(120);
        a1.setStyle("-fx-border-color: blue");
        List<Label> listLabel = new ArrayList<>();
        listLabel.add(a1);
        when(colorcontroller.getColor()).thenReturn((listLabel));
        List<Label> color = colorcontroller.getColor();
        Assertions.assertEquals(color.size(),1);

        verify(colorcontroller).getColor();

        Assertions.assertEquals(listLabel,colorcontroller.getColor());


    }

    @Override
    public void stop() throws Exception {
        super.stop();
        colorcontroller = null;
    }


}
