package de.uniks.pioneers.computation;

import de.uniks.pioneers.Main;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class DiceRoll {

	public List<Image> getDiceImages(int diceSum) {
		System.out.println(diceSum);
		List<Image> dices = new ArrayList<>();

		int firstDice;
		int secondDice;

		if (diceSum > 6) {
			int minFirstDice = diceSum % 6;
			firstDice = ThreadLocalRandom.current().nextInt(minFirstDice, 7);

		} else if (diceSum == 1) {
			firstDice = 1;
		} else {
			firstDice = ThreadLocalRandom.current().nextInt(1, diceSum);
		}
		secondDice = diceSum - firstDice;
		Image diceOne = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border" + firstDice + ".png")).toString());

		Image diceTwo = null;
		if (secondDice != 0) {
			diceTwo = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border" + secondDice + ".png")).toString());
		}
		dices.add(diceOne);
		dices.add(diceTwo);
		return dices;
	}
}
