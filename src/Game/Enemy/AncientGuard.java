package Game.Enemy;

import Game.Map.GameWorld;
import Game.Player.Player;

public class AncientGuard extends Enemy {
    public AncientGuard(int x, int y) {
        super("Ancient Guard", 15, 5, 50, x, y, 7);
    }

    @Override
    public void onDeath(GameWorld world, Player killer) {
        System.out.println("Ancient Guard crumbles into dust!");
        killer.gainXP(xpReward);
    }


}