package Game.Player;

import Game.Enemy.Enemy;
import Game.Items.Item;
import Game.Items.Trap;
import Game.Items.Weapon;
import Game.Map.GameMap;
import Game.Map.GameWorld;

import java.awt.*;

/**
 * Wizard class — represents a magic-user player with low HP but high MP and Intelligence.
 * Uses mana to cast spells and levels up primarily by improving intellect and MP pool.
 */
public class Wizard extends Player {


    private static final int[] LEVEL_HP  = {20, 40, 50, 55, 60, 80};
    private static final int[] LEVEL_MP  = {30, 50, 70, 90, 110, 140};
    private static final int[] LEVEL_INT = {10, 20, 30, 40, 50, 70};

    //constructor for level 1 wizard
    public Wizard(String name, int startX, int startY) {
        super(name, startX, startY);
        this.level = 1;
        this.maxHp = LEVEL_HP[0];
        this.hp = maxHp;
        this.maxMp = LEVEL_MP[0];
        this.mp = maxMp;
        this.str = 0;
        this.intelligence = LEVEL_INT[0];
    }

    //levels up the wizard and updates all the stats
    @Override
    public void levelUp(int newLevel) {
        this.level = newLevel;

        if (newLevel <= LEVEL_HP.length) {
            this.maxHp = LEVEL_HP[newLevel - 1];
            this.maxMp = LEVEL_MP[newLevel - 1];
            this.intelligence = LEVEL_INT[newLevel - 1];
        } else {
            this.maxHp = LEVEL_HP[5];
            this.maxMp = LEVEL_MP[5];
            this.intelligence = LEVEL_INT[5];
        }

        this.hp = maxHp;
        this.mp = maxMp;

        System.out.println(name + " LEVEL UP: " + newLevel + "!!");
    }

    //resting logic ->+5% hp, mp
    @Override
    public void rest() {
        int hpRestore = (int) Math.ceil(this.maxHp * 0.05);
        int mpRestore = (int) Math.ceil(this.maxMp * 0.05);

        this.hp = Math.min(this.hp + hpRestore, this.maxHp);
        this.mp = Math.min(this.mp + mpRestore, this.maxMp);
    }

    @Override
    public Enemy attackNearestWeakEnemy(GameWorld world) {
        System.out.println("Wizard does not use melee auto-attack.");
        return null;
    }

    //attacks an enemy using a spell
    @Override
    public void attack(Enemy enemy) {
        final int SPELL_COST = 5;      //spell cost in mp
        int totalDamage = intelligence;

        if (mp >= SPELL_COST) {
            mp -= SPELL_COST;

            System.out.println(name + " casts a spell on " + enemy.getName() +
                    " (INT: " + totalDamage + ") → Total: " + totalDamage + " dmg!");

            enemy.takeDamage(totalDamage, this);

            if (!enemy.isAlive()) {
                System.out.println(enemy.getName() + " is defeated!");
                gainXP(enemy.getXpReward());
            }

        } else {
            System.out.println(name + " tries to cast a spell, but not enough MP!");
        }
    }


    public Enemy attackNearestVisibleEnemy(GameWorld world) {
        int px = getX(), py = getY();
        Enemy closest = null;
        int minDist = Integer.MAX_VALUE;

        for (Enemy e : world.getEnemies()) {
            if (!e.isAlive()) continue;

            int dx = Math.abs(e.getX() - px);
            int dy = Math.abs(e.getY() - py);
            int dist = dx + dy;

            if (dist < minDist &&
                    world.getCurrentMap().getVisibility(e.getX(), e.getY()) == GameMap.VisibilityState.VISIBLE) {
                minDist = dist;
                closest = e;
            }
        }

        if (closest != null) {
            lastSpellTarget = new Point(closest.getX(), closest.getY());
            attack(closest);

            // Επέστρεψε τον εχθρό ώστε να τον χειριστούμε έξω
            return closest;

        } else {
            lastSpellTarget = null;
            return null;
        }
    }



    private Point lastSpellTarget = null;
    public Point getLastSpellTarget() {
        return lastSpellTarget;
    }




    //item pickup logic (+traps)
    @Override
    public void pickUp(Item item) {
        System.out.println(name + " picks up: " + item.getName());

        if (item instanceof Trap) {
            item.use(this);  // Auto-activate trap
        } else if (item instanceof Weapon weapon) {
            if (getEquippedWeapon() == null) {
                equipWeapon(weapon);
                System.out.println(name + " auto-equipped: " + weapon.getName());
            } else {
                System.out.println(name + " already has a weapon. Ignored pickup.");
            }
        } else {
            inventory.pickUp(item);  //potion
        }
    }

    //uses an item
    @Override
    public void use(Item item) {
        System.out.println(name + " uses: " + item.getName());
        item.use(this);
    }
}
