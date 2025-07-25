package Game;

import java.util.Random;

//dice rolling logic
public class Dice {

    private static final Random rand = new Random();

    public static int roll(int numberOfDice, int sides, int bonus) {
        int total = bonus;

        for (int i = 0; i < numberOfDice; i++) {
            total += rand.nextInt(sides) + 1;
        }

        return total;
    }
}
