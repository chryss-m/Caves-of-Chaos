package Game.Items;

import Game.Player.Player;

import java.awt.*;
import java.util.Objects;

//trap on the map floor
public class Trap implements Consumable {

    private final ItemEffect effect;


    public Trap(int damage) {
        this.effect = new ItemEffect(ItemEffectType.DAMAGE_HP, damage);
    }

    @Override
    public String getName() {
        return "Trap";
    }

    @Override
    public String getDescription() {
        return "You triggered a trap!";
    }


    //activates the trap and applies its effect to the player.
    @Override
    public void use(Player player) {
        System.out.println("☠️ You stepped on a trap! You take " + effect.getAmount() + " damage.");
        Toolkit.getDefaultToolkit().beep();

        if (Objects.requireNonNull(effect.getType()) == ItemEffectType.DAMAGE_HP) {
            player.takeTrapDamage(effect.getAmount());
        } else {
            System.err.println("Unsupported trap effect: " + effect.getType());
        }
    }


    @Override
    public boolean isConsumedOnUse() {
        return true;
    }
}
