package Game.Enemy;

import Game.Dice;
import Game.Items.CrisisGem;
import Game.Map.GameWorld;
import Game.Player.Player;

public class ShadowSerpent extends Enemy {
    public ShadowSerpent(int x, int y) {
        super("Shadow Serpent", 20, 0, 100, x, y, 4);
    }

    @Override
    public void attack(Player player) {
        int dmg = Dice.roll(4, 6, 10);
        System.out.println(name + " bites with venom for " + dmg + " damage!");
        player.takeDamage(dmg, this);
    }

    @Override
    public void takeDamage(int amount, Player attacker) {
        hp -= amount;
        if (hp < 0) hp = 0;

        System.out.println(name + " takes " + amount + " damage! [" + hp + "/" + maxHp + "]");

        if (!isAlive()) {
            GameWorld world = attacker.getWorld();
            if (world != null) {
                world.removeEnemy(this);
                onDeath(world, attacker);
            } else {
                System.out.println("ShadowSerpent death without valid GameWorld reference!");
            }
        }
    }

    @Override
    public void onDeath(GameWorld world, Player killer) {
        System.out.println("💀 The Chaos Serpent has been defeated!");

        world.getCurrentMap().placeItem(x, y, new CrisisGem());
        System.out.println("💎 Crisis Gem dropped at " + x + "," + y);

        killer.gainXP(xpReward);
    }
}
