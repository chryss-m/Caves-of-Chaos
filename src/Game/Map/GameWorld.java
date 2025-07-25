package Game.Map;

import Game.Enemy.*;
import Game.Items.ItemPlacer;
import Game.Player.Player;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Random;

//manages the entire game structure: levels, enemies, player progression, map transitions
public class GameWorld {

    public static final int LEVELS = 10;

    private final GameMap[] maps = new GameMap[LEVELS];
    private final Player player;
    private int currentLevel = 0;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private ShadowSerpent shadowSerpent;


    //initializes the world with 10 levels and distributes items
    public GameWorld(GameMap initialMap, Player player) {
        this.maps[0] = initialMap;
        this.player = player;
        this.player.setWorld(this);

        for (int depth = 2; depth <= LEVELS; depth++) {
            GameMap newMap = new GameMap(initialMap.getWidth(), initialMap.getHeight(), 0.45);
            maps[depth - 1] = newMap;
            ItemPlacer.placePotions(newMap, player);
            newMap.placeStarterWeapons(player);
        }

        ItemPlacer.placePotions(initialMap, player);
        maps[0].placeStarterWeapons(player);
    }


    //spawns enemies depending on player's level
    public void spawnEnemiesIfNeeded(Player player) {
        GameMap map = getCurrentMap();
        Random rand = new Random();
        int playerLevel = player.getLevel();

        boolean isFinalLevel = getCurrentLevelNum() == 10;


        if (isFinalLevel) {
            boolean serpentAlreadyOnMap = map.getEnemies().stream()
                    .anyMatch(e -> e instanceof ShadowSerpent);

            if (!serpentAlreadyOnMap) {
                int x, y;
                do {
                    x = rand.nextInt(map.width);
                    y = rand.nextInt(map.height);
                } while (map.getTileType(x, y) != GameMap.TileType.FLOOR ||
                        map.getEnemyAt(x, y) != null);

                if (shadowSerpent == null || !shadowSerpent.isAlive()) {
                    shadowSerpent = new ShadowSerpent(x, y);
                } else {
                    shadowSerpent.setX(x);
                    shadowSerpent.setY(y);
                }

                map.addEnemy(shadowSerpent);
                System.out.println("Shadow Serpent placed on level 10");
            }

            return;
        }


        int currentEnemyCount = map.getEnemies().size();
        int maxEnemies = 3 + playerLevel;

        if (currentEnemyCount >= maxEnemies) return;

        int toSpawn = maxEnemies - currentEnemyCount;

        for (int i = 0; i < toSpawn; i++) {
            int x, y;
            do {
                x = rand.nextInt(map.width);
                y = rand.nextInt(map.height);
            } while (map.getTileType(x, y) != GameMap.TileType.FLOOR ||
                    map.getEnemyAt(x, y) != null);

            Enemy e = switch (playerLevel) {
                case 1 -> new ShadowSoldier(x, y);
                case 2 -> new AncientGuard(x, y);
                case 3 -> rand.nextBoolean() ? new GreaterShade(x, y) : new ChaosKnight(x, y);
                case 4 -> new ChaosKnight(x, y);
                case 5, 6 -> rand.nextBoolean() ? new ChaosBeast(x, y) : new PatternShade(x, y);
                default -> new ShadowSoldier(x, y); // fallback
            };

            map.addEnemy(e);
        }

    }

    //enemies
    public List<Enemy> getEnemies() {
        return getCurrentMap().getEnemies();
    }

    public Enemy getEnemyAt(int x, int y) {
        return getCurrentMap().getEnemyAt(x, y);
    }

    public void removeEnemy(Enemy enemy) {
        getCurrentMap().getEnemies().remove(enemy);
    }

    //removes all enemies from map except the final boss
    private void clearEnemiesExceptShadowSerpent(GameMap map) {
        map.getEnemies().removeIf(enemy -> !(enemy instanceof ShadowSerpent));
    }



    //move player one map down
    public boolean goDown() {
        if (currentLevel >= LEVELS - 1) return false;

        clearEnemiesExceptShadowSerpent(maps[currentLevel]);
        currentLevel++;
        pcs.firePropertyChange("mapLevel", null, getCurrentLevelNum());
        spawnEnemiesIfNeeded(player);
        return true;
    }

    //move player one map up
    public boolean goUp() {
        if (currentLevel <= 0) return false;

        clearEnemiesExceptShadowSerpent(maps[currentLevel]);
        currentLevel--;
        pcs.firePropertyChange("mapLevel", null, getCurrentLevelNum());
        spawnEnemiesIfNeeded(player);
        return true;
    }

    //listener
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }


    public GameMap getCurrentMap() {
        return maps[currentLevel];
    }

    public int getCurrentLevelNum() {
        return currentLevel + 1;
    }

    //enemy attack
    private Point lastEnemyAttackTarget = null;

    public Point getLastEnemyAttackTarget() {
        return lastEnemyAttackTarget;
    }

    public void setLastEnemyAttackTarget(Point point) {
        this.lastEnemyAttackTarget = point;
    }

    public void clearLastEnemyAttackTarget() {
        this.lastEnemyAttackTarget = null;
    }


    public void setCurrentLevel(int index) {
        if (index >= 0 && index < maps.length) {
            clearEnemiesExceptShadowSerpent(maps[currentLevel]);
            currentLevel = index;
            pcs.firePropertyChange("mapLevel", null, getCurrentLevelNum());
            spawnEnemiesIfNeeded(player);
        } else {
            System.out.println("Invalid level index: " + index);
        }
    }
}
