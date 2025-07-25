package Game.Enemy;

import Game.Map.GameWorld;
import Game.Player.Player;

public class GreaterShade extends Enemy {
    public GreaterShade(int x, int y) {
        super("Greater Shade", 50, 12, 120, x, y, 7);
    }


    @Override
    public void onDeath(GameWorld world, Player killer) {
        System.out.println("Greater Shade dissipates into shadows!");
        killer.gainXP(xpReward);
    }

}