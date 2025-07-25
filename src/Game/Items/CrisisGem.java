package Game.Items;

import java.util.Map;

public class CrisisGem extends Weapon {
    public CrisisGem() {
        super("Crisis Gem",
                WeaponType.STAFF,
                Map.of());
    }

    @Override
    public void equip(Game.Player.Player player) {
        super.equip(player);
        System.out.println(player.getName() + " equipped the Crisis Gem...");


        player.winGame();
    }

    @Override
    public String getName() {
        return "Crisis Gem";
    }
}

