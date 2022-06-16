package de.uniks.pioneers.controller;


import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.Constants.*;

public class ColorController {

    private final List<Label> color = new ArrayList<>();

    public ColorController(){
        createColor();
    }

    //Sets labels withs its respective colors
    public  void createColor() {
        for(int i = 0; i<8;i++) {
            Label label = new Label();
            label.setText(COLORSTRINGARRAY[i]);
            label.setTextFill(COLORARRAY[i]);
            label.setMinWidth(150);
            color.add(label);
        }
    }

    public List<Label> getColor(){
        return color;
    }
}
