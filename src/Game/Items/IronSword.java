package Game.Items;

import java.util.Map;

public class IronSword extends Weapon {
    public IronSword() {
        super("Iron Sword",
                WeaponType.SWORD,
                Map.of(ItemEffectType.BONUS_STR, 2));
    }
}
