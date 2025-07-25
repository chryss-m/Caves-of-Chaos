package Game.Map;

import Game.Enemy.Enemy;
import Game.Items.*;
import Game.Player.Player;
import java.awt.*;
import java.util.*;
import java.util.List;

//this class represents the map-> players,tiles,enemies, items
public class GameMap {

    public enum TileType { WALL, FLOOR, ENTRY, STAIRS }
    public enum VisibilityState { UNKNOWN, FOGGED, VISIBLE }

    //tiles
    public TileType[][] tiles;
    public VisibilityState[][] visibility;
    public int width, height;
    public int entryX, entryY;
    public int exitX, exitY;

    //enemies and items per tiles
    private final List<Enemy> enemies = new ArrayList<>();
    public final Map<Point, List<Item>> itemsPerTile = new HashMap<>();

    //directions for path generation
    public enum Direction {
        LEFT(-1, 0), RIGHT(1, 0), UP(0, -1), DOWN(0, 1);
        public final int dx, dy;
        Direction(int dx, int dy) { this.dx = dx; this.dy = dy; }

        public static Direction random(Random rand) {
            return values()[rand.nextInt(values().length)];
        }
    }

    //constructor for a random map
    public GameMap(int width, int height, double fillPercent) {
        this.width = width;
        this.height = height;
        tiles = new TileType[width][height];
        visibility = new VisibilityState[width][height];

        generateMap(fillPercent);
        initializeVisibility();
        populateTraps(8); //generate traps at the same time
    }


    //visibility
    private void initializeVisibility() {
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                visibility[x][y] = VisibilityState.UNKNOWN;
    }

    //player's visibility
    public void updateVisibility(int playerX, int playerY) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int dx = playerX - x;
                int dy = playerY - y;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < 6.0) {
                    visibility[x][y] = VisibilityState.VISIBLE;
                } else if (visibility[x][y] == VisibilityState.VISIBLE) {
                    visibility[x][y] = VisibilityState.FOGGED;
                }
            }
        }
    }

    //visibility getter
    public VisibilityState getVisibility(int x, int y) {
        return visibility[x][y];
    }

    //map generator
    private void generateMap(double fillPercent) {
        Random rand = new Random();

        //al the tiles are walls
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                tiles[x][y] = TileType.WALL;

        //generate entry point
        entryX = rand.nextInt(width);
        entryY = rand.nextInt(height);
        tiles[entryX][entryY] = TileType.ENTRY;

        //random walk
        int curX = entryX, curY = entryY;
        int filled = 1;
        int total = width * height;
        int maxAttempts = total * 10;
        double target = fillPercent * total;
        int attempts = 0;

        while (filled < target && attempts < maxAttempts) {
            Direction dir = Direction.random(rand);
            int nx = curX + dir.dx;
            int ny = curY + dir.dy;

            if (isInBounds(nx, ny)) {
                curX = nx;
                curY = ny;
                if (tiles[curX][curY] == TileType.WALL) {
                    tiles[curX][curY] = TileType.FLOOR;
                    filled++;
                }
            }
            attempts++;
        }

        //exit generation
        int maxDist = -1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] == TileType.FLOOR || tiles[x][y] == TileType.ENTRY) {
                    int dist = Math.abs(x - entryX) + Math.abs(y - entryY);
                    if (dist > maxDist) {
                        maxDist = dist;
                        exitX = x;
                        exitY = y;
                    }
                }
            }
        }
        tiles[exitX][exitY] = TileType.STAIRS;

        //check if entry is isolated (surrounded by walls)
        if (!hasAdjacentFloor(entryX, entryY)) {
            generateMap(fillPercent);
        }
    }


    private boolean hasAdjacentFloor(int x, int y) {
        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        for (int[] d : dirs) {
            int nx = x + d[0], ny = y + d[1];
            if (isInBounds(nx, ny) && tiles[nx][ny] == TileType.FLOOR)
                return true;
        }
        return false;
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }



    //enemy-methods
    public void addEnemy(Enemy e) { enemies.add(e); }
    public void removeEnemy(Enemy e) { enemies.remove(e); }

    public Enemy getEnemyAt(int x, int y) {
        for (Enemy e : enemies) {
            if (e.getX() == x && e.getY() == y && e.isAlive())
                return e;
        }
        return null;
    }


    public List<Enemy> getEnemies() {
        return enemies;
    }




    //item-methods
    public void addItem(int x, int y, Item item) {
        Point p = new Point(x, y);
        itemsPerTile.computeIfAbsent(p, k -> new ArrayList<>()).add(item);
    }

    public List<Item> getItemsAt(int x, int y) {
        return itemsPerTile.getOrDefault(new Point(x, y), new ArrayList<>());
    }



    private void populateTraps(int numberOfTraps) {
        Random rand = new Random();
        int placed = 0;

        while (placed < numberOfTraps) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);

            if (tiles[x][y] == TileType.FLOOR &&
                    getItemsAt(x, y).isEmpty() &&
                    getEnemyAt(x, y) == null) {

                addItem(x, y, new Trap(10 + rand.nextInt(6))); // Trap does 10–15 damage
                placed++;
            }
        }
    }


    public void placeStarterWeapons(Player player) {
        Random rand = new Random();
        List<Weapon> weapons = player.isWizard()
                ? List.of(new ArcaneWand(), new FireStaff())
                : List.of(new IronSword(), new GreatAxe());

        for (Weapon weapon : weapons) {
            int x, y;
            do {
                x = rand.nextInt(width);
                y = rand.nextInt(height);
            } while (
                    getTileType(x, y) != TileType.FLOOR ||
                            !getItemsAt(x, y).isEmpty() ||
                            getEnemyAt(x, y) != null
            );

            addItem(x, y, weapon);
        }
    }


    public void placeItem(int x, int y, Item item) {
        addItem(x, y, item);
    }



    //general getters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public TileType getTileType(int x, int y) { return tiles[x][y]; }

    public int[] getEntryPosition() { return new int[] { entryX, entryY }; }
    public int[] getExitPosition()  { return new int[] { exitX, exitY }; }
}
