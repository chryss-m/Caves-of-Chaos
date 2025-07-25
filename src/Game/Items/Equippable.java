package Game.Items;

import Game.Player.Player;

public interface Equippable extends Item {
    void onEquip(Player player);

    void onUnequip(Player player);

    void equip(Player player);
    void unequip(Player player);
}
