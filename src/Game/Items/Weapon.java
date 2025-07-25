package Game.Items;

import Game.Player.Player;

import java.util.Map;


 //Represents a weapon that can be equipped by the player.

public class Weapon implements Equippable {

    private final String name;
    private final WeaponType type;
    private final Map<ItemEffectType, Integer> effects;


    public Weapon(String name, WeaponType type, Map<ItemEffectType, Integer> effects) {
        this.name = name;
        this.type = type;
        this.effects = effects;
    }

    public String getName() {
        return name;
    }

    public WeaponType getType() {
        return type;
    }

    public Map<ItemEffectType, Integer> getEffects() {
        return effects;
    }

    //applies the  bonuses to the player when equipped.
    @Override
    public void onEquip(Player player) {
        for (Map.Entry<ItemEffectType, Integer> entry : effects.entrySet()) {
            applyEffect(player, entry.getKey(), entry.getValue());
        }
    }


     //Removes the weapon's bonuses when unequipped
    @Override
    public void onUnequip(Player player) {
        for (Map.Entry<ItemEffectType, Integer> entry : effects.entrySet()) {
            applyEffect(player, entry.getKey(), -entry.getValue());
        }
    }


    private void applyEffect(Player player, ItemEffectType type, int amount) {
        switch (type) {
            case BONUS_STR -> player.str += amount;
            case BONUS_INT -> player.intelligence += amount;

            default -> {
            }
        }
    }

    @Override
    public String getDescription() {
        return name + " [" + type + "]";
    }


    @Override
    public void use(Player player) {

    }


    @Override
    public void equip(Player player) {
        onEquip(player);
    }


    @Override
    public void unequip(Player player) {
        onUnequip(player);
    }
}
