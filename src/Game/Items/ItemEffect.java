package Game.Items;

public class ItemEffect {

    private final ItemEffectType type;
    private final int amount;

    public ItemEffect(ItemEffectType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public ItemEffectType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }
}
