package Game.Items;

import Game.Map.GameMap;
import Game.Map.GameMap.TileType;
import Game.Player.Player;

import java.util.Random;


//class for placing potions on the game map during map generation.

public class ItemPlacer {


    public static void placePotions(GameMap map, Player player) {
        Random rand = new Random();

        int floorTiles = countFloorTiles(map);
        int numHealth = (int) (floorTiles * 0.03);
        int numMana = player.canUseManaPotions() ? (int) (floorTiles * 0.01) : 0;

        placeRandomItems(map, rand, numHealth, new HealthPotion());

        if (numMana > 0) {
            placeRandomItems(map, rand, numMana, new ManaPotion());
        }
    }

    //how many are the floor type tiles?
    private static int countFloorTiles(GameMap map) {
        int count = 0;
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getTileType(x, y) == TileType.FLOOR) {
                    count++;
                }
            }
        }
        return count;
    }


    private static void placeRandomItems(GameMap map, Random rand, int count, Item itemTemplate) {
        int width = map.getWidth();
        int height = map.getHeight();

        for (int i = 0; i < count; i++) {
            int x, y;

            //retry until a valid, empty floor tile is found
            do {
                x = rand.nextInt(width);
                y = rand.nextInt(height);
            } while (map.getTileType(x, y) != TileType.FLOOR || !map.getItemsAt(x, y).isEmpty());

            map.addItem(x, y, cloneItem(itemTemplate));
        }
    }

    private static Item cloneItem(Item item) {
        if (item instanceof HealthPotion) return new HealthPotion();
        if (item instanceof ManaPotion) return new ManaPotion();

        throw new IllegalArgumentException("Unsupported item type");
    }
}
