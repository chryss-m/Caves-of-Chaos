package Game.Enemy;

import Game.Dice;
import Game.Map.GameWorld;
import Game.Player.Player;

public class PatternShade extends Enemy {
    public PatternShade(int x, int y) {
        super("Pattern Shade", 50, 0, 400, x, y, 10);
    }

    @Override
    public void attack(Player player) {
        int dmg = Dice.roll(3, 6, 10);
        System.out.println(name + " warps reality for " + dmg + " damage!");
        player.takeDamage(dmg, this);
    }
    @Override
    public void onDeath(GameWorld world, Player killer) {
        System.out.println("Pattern Shade unravels its form...");
        killer.gainXP(xpReward);
    }



}
