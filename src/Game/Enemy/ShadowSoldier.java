package Game.Enemy;

import Game.Map.GameWorld;
import Game.Player.Player;

public class ShadowSoldier extends Enemy {


    public ShadowSoldier(int x, int y) {
        super("Shadow Soldier", 5, 2, 30, x, y,4);
    }

    @Override
    public void onDeath(GameWorld world, Player killer) {
        System.out.println(" Shadow Soldier is defeated!");
        killer.gainXP(xpReward);
    }


}