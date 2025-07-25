package Game.Items;

import Game.Player.Player;

public class ManaPotion implements Consumable {
    private final ItemEffect effect;

    public ManaPotion() {
        this.effect = new ItemEffect(ItemEffectType.HEAL_MP, 25);
    }

    @Override
    public void use(Player player) {
        System.out.println("You drink the Mana Potion.");
        player.healMana(effect.getAmount());
    }

    @Override
    public boolean isConsumedOnUse() {
        return true;
    }

    @Override
    public String getName() {
        return "Mana Potion";
    }

    @Override
    public String getDescription() {
        return "Restores 25 MP.";
    }
}
