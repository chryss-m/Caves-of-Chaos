package Game.Items;

import Game.Player.Player;

//it's the base for all the game's weapons
public interface Item {

    String getName();               //weapon name
    String getDescription();        //description-what it does
    void use(Player player);        //what happens when used by player
}
