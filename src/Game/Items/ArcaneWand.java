package Game.Items;

import java.util.Map;

public class ArcaneWand extends Weapon {
    public ArcaneWand() {
        super("Arcane Wand",
                WeaponType.WAND,
                Map.of(ItemEffectType.BONUS_INT, 2));
    }
}
