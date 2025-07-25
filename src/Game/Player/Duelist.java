package Game.Player;

import Game.Enemy.Enemy;
import Game.Items.Item;
import Game.Items.ItemEffectType;
import Game.Items.Trap;
import Game.Items.Weapon;
import Game.Map.GameWorld;

import java.util.Map;

/**
 * Duelist class — represents a melee fighter with high HP and Strength, but no Mana.
 * Designed for physical combat; cannot use mana-based abilities or potions.
 */
public class Duelist extends Player {


    private static final int[] LEVEL_HP  = {30, 60, 80, 90, 100, 140};
    private static final int[] LEVEL_STR = {10, 20, 25, 30, 35, 45};

    //constructor for level 1 duelist
    public Duelist(String name, int startX, int startY) {
        super(name, startX, startY);
        this.level = 1;
        this.maxHp = LEVEL_HP[0];
        this.hp = maxHp;
        this.str = LEVEL_STR[0];
        this.intelligence = 0;
        this.maxMp = 0;
        this.mp = 0;
    }

    //levels up the duelist and updates all the stats
    @Override
    public void levelUp(int newLevel) {
        this.level = newLevel;

        if (newLevel <= LEVEL_HP.length) {
            this.maxHp = LEVEL_HP[newLevel - 1];
            this.str = LEVEL_STR[newLevel - 1];
        } else {
            this.maxHp = LEVEL_HP[5];
            this.str = LEVEL_STR[5];
        }

        this.hp = maxHp;
        System.out.println(name + " LEVEL UP: " + newLevel + "!!");
    }

   //resting logic->+5% hp
    @Override
    public void rest() {
        int hpRestore = (int) Math.ceil(this.maxHp * 0.05);
        this.hp = Math.min(this.hp + hpRestore, this.maxHp);
    }

    //attacks an enemy
    @Override
    public void attack(Enemy enemy) {
        int totalDamage = str;

        if (weapon != null) {
            for (Map.Entry<ItemEffectType, Integer> entry : weapon.getEffects().entrySet()) {
                if (entry.getKey() == ItemEffectType.BONUS_STR) {
                    totalDamage += entry.getValue();
                }
            }
        }

        System.out.println(name + " strikes " + enemy.getName() +
                " (STR: " + totalDamage + ") → Total: " + totalDamage + " dmg!");

        enemy.takeDamage(totalDamage, this);

        if (!enemy.isAlive()) {
            System.out.println(enemy.getName() + " is defeated!");
            gainXP(enemy.getXpReward());
        }
    }


    @Override
    public Enemy attackNearestVisibleEnemy(GameWorld world) {
        System.out.println("Duelist cannot attack at range.");
        return null;
    }


    public Enemy attackNearestWeakEnemy(GameWorld world) {
        int px = getX(), py = getY();
        Enemy weakest = null;

        for (Enemy e : world.getEnemies()) {
            int ex = e.getX(), ey = e.getY();
            if (Math.abs(ex - px) + Math.abs(ey - py) == 1 && e.isAlive()) {
                if (weakest == null || e.getHp() < weakest.getHp()) {
                    weakest = e;
                }
            }
        }

        if (weakest != null) {
            this.attack(weakest);
            return weakest;
        } else {
            System.out.println("❌ No adjacent enemy to attack.");
            return null;
        }
    }



    //item pickup logic (+traps)
    @Override
    public void pickUp(Item item) {
        System.out.println(name + " picks up: " + item.getName());

        if (item instanceof Trap) {
            item.use(this);
        } else if (item instanceof Weapon weapon) {
            if (getEquippedWeapon() == null) {
                equipWeapon(weapon);
                System.out.println(name + " auto-equipped: " + weapon.getName());
            } else {
                System.out.println(name + " already has a weapon. Ignored pickup.");
            }
        } else {
            inventory.pickUp(item);
        }
    }


    @Override
    public void use(Item item) {
        System.out.println(name + " uses: " + item.getName());
        item.use(this);
    }

    //duelist cannot use mana potions
    @Override
    public boolean canUseManaPotions() {
        return false;
    }
}
