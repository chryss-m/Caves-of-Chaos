package Game.Player;

import Game.Enemy.Enemy;
import Game.Items.Inventory;
import Game.Items.Item;
import Game.Items.Weapon;
import Game.Map.GameMap;
import Game.Map.GameWorld;

import javax.swing.*;


public abstract class Player {


    protected static final int[] LEVEL_XP = {0, 300, 900, 2700, 6500, 14000};


    protected String name;
    protected int x, y; // Player position
    protected int hp;
    public int maxHp;
    protected int mp;
    public int maxMp;
    public int str;
    public int intelligence;
    protected int xp, level;


    protected Weapon weapon;
    protected Inventory inventory = new Inventory();

    //flags
    private boolean isResting = false;
    private GameWorld world;

    //constructor
    public Player(String name, int startX, int startY) {
        this.name = name;
        this.x = startX;
        this.y = startY;
        this.level = 1;
        this.xp = 0;
    }

    //movement logic
    public void move(int dx, int dy, GameWorld world) {
        GameMap map = world.getCurrentMap();
        int nx = x + dx, ny = y + dy;

        if (nx < 0 || ny < 0 || nx >= map.width || ny >= map.height) return;

        GameMap.TileType tile = map.getTileType(nx, ny);
        if (tile == GameMap.TileType.WALL) return;


        if (world.getEnemyAt(nx, ny) != null) return;

        //stairs
        if (tile == GameMap.TileType.STAIRS) {
            if (world.goDown()) {
                int[] p = world.getCurrentMap().getEntryPosition();
                x = p[0];
                y = p[1];
                unequipWeapon(); // Remove weapon on level transition
                System.out.println(name + " unequipped weapon due to going down.");
            }
            return;
        }

        //entry
        if (tile == GameMap.TileType.ENTRY) {
            if (world.goUp()) {
                int[] p = world.getCurrentMap().getExitPosition();
                x = p[0];
                y = p[1];
                unequipWeapon();
                System.out.println(name + " unequipped weapon due to going up.");
            }
            return;
        }

        x = nx;
        y = ny;
        world.spawnEnemiesIfNeeded(this);
    }

    //xp and levels
    public void gainXP(int amount) {
        xp += amount;
        int newLevel = calculate_Level(xp);
        if (newLevel > level) {
            levelUp(newLevel);
        }
    }

    protected int calculate_Level(int xp) {
        for (int i = LEVEL_XP.length - 1; i >= 0; i--) {
            if (xp >= LEVEL_XP[i]) return i + 1;
        }
        return 1;
    }

    public abstract void levelUp(int newLevel);

    //damage from enemies
    public void takeDamage(int damage, Enemy enemy) {
        hp -= damage;
        if (hp < 0) hp = 0;
        System.out.println(name + " takes " + damage + " damage! [" + hp + "/" + maxHp + "]");
        if (hp == 0) onDeath();
    }

    //damage from traps
    public void takeTrapDamage(int baseDamage) {
        double scale = 1.0 + (level - 1) * 0.1;
        int finalDamage = (int) Math.round(baseDamage * scale);
        hp -= finalDamage;
        if (hp < 0) hp = 0;
        if (hp == 0) onDeath();
    }

    //healing stats
    public void healHp(int amount) {
        if (hp == maxHp) {
            System.out.println(name + " is already at full HP! [" + hp + "/" + maxHp + "]");
            return;
        }

        int before = hp;
        hp = Math.min(hp + amount, maxHp);
        System.out.println(name + " heals " + (hp - before) + " HP! [" + hp + "/" + maxHp + "]");
    }

    public void healMana(int amount) {
        if (mp == maxMp) {
            System.out.println(name + " is already at full MP! [" + mp + "/" + maxMp + "]");
            return;
        }

        int before = mp;
        mp = Math.min(mp + amount, maxMp);
        System.out.println(name + " restores " + (mp - before) + " MP! [" + mp + "/" + maxMp + "]");
    }

    //resting methods
    public void startResting() { isResting = true; }
    public void stopResting() { isResting = false; }
    public boolean isResting() { return isResting; }

    //potion usage
    public boolean tryHealPotion() {
        return inventory.useHealthPotion(this);
    }

    public boolean tryManaPotion() {
        return inventory.useManaPotion(this);
    }

    public boolean canUseManaPotions() {
        return true;
    }

    //weapons
    public void equipWeapon(Weapon newWeapon) {
        if (this.weapon != null) this.weapon.unequip(this);
        this.weapon = newWeapon;
        newWeapon.equip(this);
        System.out.println(name + " equipped " + newWeapon.getName());
    }

    public void unequipWeapon() {
        if (this.weapon != null) {
            this.weapon.unequip(this);
            System.out.println(name + " unequipped " + weapon.getName());
            this.weapon = null;
        }
    }

    public Weapon getEquippedWeapon() {
        return weapon;
    }

    //win-lose logic
    public void winGame() {
        System.out.println("🎉 " + name + " has claimed the Crisis Gem and won the game!");
        JOptionPane.showMessageDialog(null,
                "🏆 " + name + " has claimed the Crisis Gem and won the game!",
                "Victory!", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    protected void onDeath() {
        System.out.println("💀 " + name + " has fallen in battle...");
        JOptionPane.showMessageDialog(null,
                "💀 " + name + " has fallen in battle...\nGame Over.",
                "Game Over", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }



    //abstract methods
    public abstract void rest();
    public abstract Enemy attackNearestWeakEnemy(GameWorld world);
    public abstract void attack(Enemy enemy);
    public abstract Enemy attackNearestVisibleEnemy(GameWorld world);
    public abstract void pickUp(Item item);
    public abstract void use(Item item);


    public boolean isWizard() {
        return this instanceof Wizard;
    }

    //getters-setters
    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMp() { return mp; }
    public int getMaxMp() { return maxMp; }
    public int getXp() { return xp; }
    public int getLevel() { return level; }
    public int getStr() { return str; }
    public int getIntelligence() { return intelligence; }
    public Inventory getInventory() { return inventory; }

    public int getXpForNextLevel() {
        return level >= LEVEL_XP.length ? LEVEL_XP[LEVEL_XP.length - 1] : LEVEL_XP[level];
    }

    public String getPlayerClass() {
        return getClass().getSimpleName();
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public GameWorld getWorld() {
        return this.world;
    }

    public void setWorld(GameWorld world) {
        this.world = world;
    }
}
