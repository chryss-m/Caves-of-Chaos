package Game.Enemy;

import Game.Map.GameWorld;
import Game.Player.Player;

public class ChaosKnight extends Enemy {
    public ChaosKnight(int x, int y) {
        super("Chaos Knight", 60, 20, 150, x, y, 9);
    }

    @Override
    public void onDeath(GameWorld world, Player killer) {
        System.out.println("Chaos Knight falls in silence.");
        killer.gainXP(xpReward);
    }

}
