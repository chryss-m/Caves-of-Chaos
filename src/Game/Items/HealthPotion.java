package Game.Items;

import Game.Player.Player;

public class HealthPotion implements Consumable {
    private final ItemEffect effect;

    public HealthPotion() {
        this.effect = new ItemEffect(ItemEffectType.HEAL_HP, 10);
    }

    @Override
    public void use(Player player) {
        System.out.println("You drink the Health Potion.");
        player.healHp(effect.getAmount());
    }

    @Override
    public boolean isConsumedOnUse() {
        return true;
    }

    @Override
    public String getName() {
        return "Health Potion";
    }

    @Override
    public String getDescription() {
        return "Restores 30 HP.";
    }
}
