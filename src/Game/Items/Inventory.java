package Game.Items;

import Game.Player.Player;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;


//inventory-> manages potions
public class Inventory {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private int healthPotions;
    private int manaPotions;
    private Weapon equippedWeapon;
    private final List<Item> miscItems = new ArrayList<>();

   //listener to observe changes
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }


    public void pickUp(Item item) {
        switch (item) {
            case HealthPotion hp -> {
                int old = healthPotions;
                healthPotions++;
                pcs.firePropertyChange("healthPotions", old, healthPotions);
            }
            case ManaPotion mp -> {
                int old = manaPotions;
                manaPotions++;
                pcs.firePropertyChange("manaPotions", old, manaPotions);
            }
            case null, default -> miscItems.add(item);
        }
    }


    public boolean useHealthPotion(Player player) {
        if (player.getHp() == player.getMaxHp()) {
            return false;
        }

        if (healthPotions > 0) {
            healthPotions--;
            player.healHp(30);
            pcs.firePropertyChange("healthPotions", null, healthPotions);
            return true;
        }

        return false;
    }



    public boolean useManaPotion(Player player) {
        if (player.getMp() == player.getMaxMp()) {
            return false;
        }

        if (manaPotions > 0) {
            manaPotions--;
            player.healMana(10);
            pcs.firePropertyChange("manaPotions", null, manaPotions);
            return true;
        }

        return false;
    }



    //getters
    public int getHealthPotions() {
        return healthPotions;
    }

    public int getManaPotions() {
        return manaPotions;
    }

}
