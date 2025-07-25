
package Game.Enemy;

import Game.Dice;
import Game.Map.GameWorld;
import Game.Player.Player;


public class ChaosBeast extends Enemy {
    public ChaosBeast(int x, int y) {
        super("Chaos Beast", 80, 0, 200, x, y, 5);
    }

    @Override
    public void attack(Player player) {
        int dmg = Dice.roll(2, 6, 10);
        System.out.println(name + " charges wildly for " + dmg + " damage!");
        player.takeDamage(dmg, this);
    }

    @Override
    public void onDeath(GameWorld world, Player killer) {
        System.out.println("Chaos Beast lets out a final roar!");
        killer.gainXP(xpReward);
    }



}