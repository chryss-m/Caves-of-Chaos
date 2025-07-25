package Game.Enemy;

import Game.Map.GameMap;
import Game.Map.GameWorld;
import Game.Player.Player;

import java.awt.*;

//abstract base class for all enemy types
public abstract class Enemy {

    protected String name;
    protected int hp, maxHp;
    protected int damage;
    protected int xpReward;
    protected int x, y;
    protected int visibilityRadius;


    public Enemy(String name, int maxHp, int damage, int xpReward, int x, int y, int visibilityRadius) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.damage = damage;
        this.xpReward = xpReward;
        this.x = x;
        this.y = y;
        this.visibilityRadius = visibilityRadius;
    }


    public void takeDamage(int amount, Player attacker) {
        hp -= amount;
        if (hp < 0) hp = 0;

        System.out.println(name + " takes " + amount + " damage! [" + hp + "/" + maxHp + "]");

        // If enemy is dead, remove from map and trigger death logic
        if (!isAlive()) {
            GameWorld world = attacker.getWorld();

                world.removeEnemy(this);
                onDeath(world, attacker);

        }
    }

    //attack player
    public void attack(Player player) {
        System.out.println(name + " hits " + player.getName() + " for " + damage + " dmg!");
        player.takeDamage(damage, this);
    }

    //enemy ai
    public void takeTurn(GameWorld world, Player player) {
        if (!isAlive()) return;

        if (canSeePlayer(player)) {
            int dx = Math.abs(player.getX() - this.x);
            int dy = Math.abs(player.getY() - this.y);


            if (dx + dy == 1) {
                attack(player);
                world.setLastEnemyAttackTarget(new Point(player.getX(), player.getY()));
            } else {

                int moveX = Integer.compare(player.getX(), this.x);
                int moveY = Integer.compare(player.getY(), this.y);

                int newX = this.x + moveX;
                int newY = this.y + moveY;

                boolean blockedByWall = world.getCurrentMap().getTileType(newX, newY) == GameMap.TileType.WALL;
                boolean blockedByEnemy = world.getEnemyAt(newX, newY) != null;
                boolean blockedByPlayer = player.getX() == newX && player.getY() == newY;

                if (!blockedByWall && !blockedByEnemy && !blockedByPlayer) {
                    this.x = newX;
                    this.y = newY;
                }
            }
        }
    }

    //visibility check
    protected boolean canSeePlayer(Player player) {
        int dx = Math.abs(player.getX() - this.x);
        int dy = Math.abs(player.getY() - this.y);
        return dx + dy <= visibilityRadius;
    }

    //getters-setters
    public boolean isAlive() { return hp > 0; }
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getDamage() { return damage; }
    public int getXpReward() { return xpReward; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }


    //death abstract method
    public abstract void onDeath(GameWorld world, Player killer);
}
