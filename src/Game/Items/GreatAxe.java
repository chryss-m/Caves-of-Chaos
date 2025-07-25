package Game.Items;

import java.util.Map;

public class GreatAxe extends Weapon {
    public GreatAxe() {
        super("Great Axe",
                WeaponType.AXE,
                Map.of(ItemEffectType.BONUS_STR, 4));
    }
}
