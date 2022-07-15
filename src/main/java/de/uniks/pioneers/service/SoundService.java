package de.uniks.pioneers.service;

import de.uniks.pioneers.Main;
import javafx.scene.media.AudioClip;

import java.util.Objects;

import static de.uniks.pioneers.Constants.ROB_ACTION;

public class SoundService {
    public void playSound(String action) {
        AudioClip audioClip;
        switch (action) {
            case ROB_ACTION -> {
                audioClip = new AudioClip(Objects.requireNonNull(Main.class.getResource("sound/rob.mp3")).toExternalForm());
                audioClip.play();
            }
            case "building" -> {
                audioClip = new AudioClip(Objects.requireNonNull(Main.class.getResource("sound/building.mp3")).toExternalForm());
                audioClip.play();
            }
            case "receive" -> {
                audioClip = new AudioClip(Objects.requireNonNull(Main.class.getResource("sound/receive.mp3")).toExternalForm());
                audioClip.play();
            }
            case "drop" -> {
                audioClip = new AudioClip(Objects.requireNonNull(Main.class.getResource("sound/drop.mp3")).toExternalForm());
                audioClip.play();
            }
        }
    }
}
