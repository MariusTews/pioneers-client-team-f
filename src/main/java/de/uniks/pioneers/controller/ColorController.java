package de.uniks.pioneers.controller;


import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class ColorController {

    private List<Label> color = new ArrayList<>(26);


    public ColorController(){
        createColor();
    }

    //Sets labels withs its respective colors
    public  void createColor(){
        Label label1 = new Label();
        label1.setText("RED");
        label1.setTextFill(Color.RED);
        label1.setMinWidth(120);
        label1.setStyle("-fx-border-color: blue;");
        color.add(label1);

        Label label2 = new Label();
        label2.setText("BLUE");
        label2.setTextFill(Color.BLUE);
        label2.setMinWidth(120);
        label2.setStyle("-fx-border-color: blue;");
        color.add(label2);

        Label label3 = new Label();
        label3.setText("ORANGE");
        label3.setTextFill(Color.ORANGE);
        label3.setMinWidth(120);
        label3.setStyle("-fx-border-color: blue;");
        color.add(label3);

        Label label4 = new Label();
        label4.setText("VIOLET");
        label4.setTextFill(Color.VIOLET);
        label4.setMinWidth(120);
        label4.setStyle("-fx-border-color: blue;");
        color.add(label4);

        Label label5 = new Label();
        label5.setText("PINK");
        label5.setTextFill(Color.PINK);
        label5.setMinWidth(120);
        label5.setStyle("-fx-border-color: blue;");
        color.add(label5);

        Label label6 = new Label();
        label6.setText("CORAL");
        label6.setTextFill(Color.CORAL);
        label6.setMinWidth(120);
        label6.setStyle("-fx-border-color: blue;");
        color.add(label6);

        Label label7 = new Label();
        label7.setText("GOLD");
        label7.setTextFill(Color.GOLD);
        label7.setMinWidth(120);
        label7.setStyle("-fx-border-color: blue;");
        color.add(label7);

        Label label8 = new Label();
        label8.setText("DARKORANGE");
        label8.setTextFill(Color.DARKORANGE);
        label8.setMinWidth(120);
        label8.setStyle("-fx-border-color: blue;");
        color.add(label8);

        Label label9 = new Label();
        label9.setText("TOMATO");
        label9.setTextFill(Color.TOMATO);
        label9.setMinWidth(120);
        label9.setStyle("-fx-border-color: blue;");
        color.add(label9);

        Label label10 = new Label();
        label10.setText("DARKORCHID");
        label10.setTextFill(Color.DARKORCHID);
        label10.setMinWidth(120);
        label10.setStyle("-fx-border-color: blue;");
        color.add(label10);

        Label label11 = new Label();
        label11.setText("LIME");
        label11.setTextFill(Color.LIME);
        label11.setMinWidth(120);
        label11.setStyle("-fx-border-color: blue;");
        color.add(label11);

        Label label12 = new Label();
        label12.setText("GREEN");
        label12.setTextFill(Color.GREEN);
        label12.setMinWidth(120);
        label12.setStyle("-fx-border-color: blue;");
        color.add(label12);

        Label label13 = new Label();
        label13.setText("PALEGREEN");
        label13.setTextFill(Color.PALEGREEN);
        label13.setMinWidth(120);
        label13.setStyle("-fx-border-color: blue;");
        color.add(label13);

        Label label14 = new Label();
        label14.setText("PURPLE");
        label14.setTextFill(Color.PURPLE);
        label14.setMinWidth(120);
        label14.setStyle("-fx-border-color: blue;");
        color.add(label14);

        Label label15 = new Label();
        label15.setText("ROYALBLUE");
        label15.setTextFill(Color.ROYALBLUE);
        label15.setMinWidth(120);
        label15.setStyle("-fx-border-color: blue;");
        color.add(label15);

        Label label16 = new Label();
        label16.setText("CRIMSON");
        label16.setTextFill(Color.CRIMSON);
        label16.setMinWidth(120);
        label16.setStyle("-fx-border-color: blue;");
        color.add(label16);

        Label label17 = new Label();
        label17.setText("ROSYBROWN");
        label17.setTextFill(Color.ROSYBROWN);
        label17.setMinWidth(120);
        label17.setStyle("-fx-border-color: blue;");
        color.add(label17);

        Label label18 = new Label();
        label18.setText("BROWN");
        label18.setTextFill(Color.BROWN);
        label18.setMinWidth(120);
        label18.setStyle("-fx-border-color: blue;");
        color.add(label18);

        Label label19 = new Label();
        label19.setText("CHOCOLATE");
        label19.setTextFill(Color.CHOCOLATE);
        label19.setMinWidth(120);
        label19.setStyle("-fx-border-color: blue;");
        color.add(label19);

        Label label20 = new Label();
        label20.setText("SIENNA");
        label20.setTextFill(Color.SIENNA);
        label20.setMinWidth(120);
        label20.setStyle("-fx-border-color: blue;");
        color.add(label20);

        Label label21 = new Label();
        label21.setText("BURLYWOOD");
        label21.setTextFill(Color.BURLYWOOD);
        label21.setMinWidth(120);
        label21.setStyle("-fx-border-color: blue;");
        color.add(label21);

        Label label22 = new Label();
        label22.setText("LIMEGREEN");
        label22.setTextFill(Color.LIMEGREEN);
        label22.setMinWidth(120);
        label22.setStyle("-fx-border-color: blue;");
        color.add(label22);

        Label label23 = new Label();
        label23.setText("DARKCYAN");
        label23.setTextFill(Color.DARKCYAN);
        label23.setMinWidth(120);
        label23.setStyle("-fx-border-color: blue;");
        color.add(label23);

        Label label24 = new Label();
        label24.setText("DEEPSKYBLUE");
        label24.setTextFill(Color.DEEPSKYBLUE);
        label24.setMinWidth(120);
        label24.setStyle("-fx-border-color: blue;");
        color.add(label24);

        Label label25 = new Label();
        label25.setText("SEAGREEN");
        label25.setTextFill(Color.SEAGREEN);
        label25.setMinWidth(120);
        label25.setStyle("-fx-border-color: blue;");
        color.add(label25);

        Label label26 = new Label();
        label26.setText("POWDERBLUE");
        label26.setTextFill(Color.POWDERBLUE);
        label26.setMinWidth(120);
        label26.setStyle("-fx-border-color: blue;");
        color.add(label26);

    }

    public List<Label> getColor(){
        return color;
    }
}
