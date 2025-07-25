package Game.GUI;

import Game.Items.ItemEffectType;
import Game.Map.GameWorld;
import Game.Player.Player;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

//displays player status and game log
public class SidePanel extends JPanel {

    private final JTextArea statusArea;
    private final JTextArea logArea;

    private GameWorld world;
    private Player player;

    private boolean listenerRegistered = false;


    public SidePanel() {
        setPreferredSize(new Dimension(250, 600));
        setLayout(new BorderLayout());

       //status area
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        statusArea.setBackground(Color.BLACK);
        statusArea.setForeground(Color.WHITE);
        JScrollPane statusScroll = new JScrollPane(statusArea);

        //logs area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(Color.DARK_GRAY);
        logArea.setForeground(Color.LIGHT_GRAY);
        JScrollPane logScroll = new JScrollPane(logArea);


        JPanel main = new JPanel(new BorderLayout());
        main.add(statusScroll, BorderLayout.CENTER);
        main.add(new JLabel("Log:"), BorderLayout.SOUTH);


        add(main, BorderLayout.NORTH);
        add(logScroll, BorderLayout.CENTER);
    }


    public void updateStatus(Player player, GameWorld world) {
        this.player = player;
        this.world = world;

        StringBuilder sb = new StringBuilder();


        sb.append("Class: ").append(player.getPlayerClass());
        if (player.isResting()) sb.append(" (Resting)");
        sb.append("\n");


        sb.append("Map Level: ").append(world.getCurrentLevelNum()).append("\n");


        sb.append("HP: ").append(player.getHp()).append(" / ").append(player.getMaxHp()).append("\n");
        sb.append("MP: ").append(player.getMp()).append(" / ").append(player.getMaxMp()).append("\n");


        int xp = player.getXp();
        int xpNext = player.getXpForNextLevel();
        int level = player.getLevel();
        sb.append("XP: ").append(xp).append(" / ").append(xpNext)
                .append("   (Lv. ").append(level).append(")\n");


        sb.append("STR: ").append(player.getStr()).append("\n");
        sb.append("INT: ").append(player.getIntelligence()).append("\n");


        if (player.getEquippedWeapon() != null) {
            sb.append("Weapon: ").append(player.getEquippedWeapon().getName()).append("\n");


            Map<ItemEffectType, Integer> effects = player.getEquippedWeapon().getEffects();
            StringBuilder bonus = new StringBuilder();
            for (Map.Entry<ItemEffectType, Integer> entry : effects.entrySet()) {
                String label = entry.getKey().name().replace("BONUS_", "");
                bonus.append(label).append("+").append(entry.getValue()).append(" ");
            }
            sb.append("Bonus: ").append(bonus).append("\n");
        } else {
            sb.append("Weapon: None\n");
            sb.append("Bonus: -\n");
        }


        int hp = player.getInventory().getHealthPotions();
        int mp = player.getInventory().getManaPotions();
        sb.append("Potions: ❤️ ").append(hp).append("   🔵 ").append(mp).append("\n");

        statusArea.setText(sb.toString());


        if (!listenerRegistered) {
            player.getInventory().addPropertyChangeListener(evt -> {
                if ("healthPotions".equals(evt.getPropertyName()) ||
                        "manaPotions".equals(evt.getPropertyName())) {
                    updateStatus(this.player, this.world);
                }
            });
            listenerRegistered = true;
        }
    }

    //add message to log area
    public void log(String message) {
        logArea.append("📜 " + message + "\n");
    }


    public void setWorld(GameWorld world) {
        this.world = world;
        world.addPropertyChangeListener(evt -> {
            if ("mapLevel".equals(evt.getPropertyName())) {
                updateStatus(this.player, this.world);
            }
        });
    }
}
