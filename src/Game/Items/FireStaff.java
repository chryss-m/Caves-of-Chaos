package Game.Items;

import java.util.Map;

public class FireStaff extends Weapon {
    public FireStaff() {
        super("Fire Staff",
                WeaponType.STAFF,
                Map.of(
                        ItemEffectType.BONUS_INT, 4
                ));
    }
}
