package Game.GUI;

import Game.Map.GameMap;
import Game.Map.GameWorld;
import Game.Player.Duelist;
import Game.Player.Player;
import Game.Player.Wizard;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

//first game window->select a character
public class CharacterSelectionPanel extends JPanel {

    private final JFrame parentFrame;

    //constructor
    public CharacterSelectionPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        setBackground(new Color(0x3E2C23)); // Dark brown background

        //title label
        JLabel title = new JLabel("Character Selection", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 32));
        title.setForeground(new Color(0xE9DCC9));
        add(title, BorderLayout.NORTH);

        //options (Wizard, Duelist)
        JPanel charactersPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        charactersPanel.setOpaque(false);

        charactersPanel.add(createCharacterBox("Wizard", "/Game/GUI/wizard.png", () -> startGame("Wizard")));
        charactersPanel.add(createCharacterBox("Duelist", "/Game/GUI/duelist.png", () -> startGame("Duelist")));

        add(charactersPanel, BorderLayout.CENTER);
    }

    //method to create the character box inside the window
    private JPanel createCharacterBox(String name, String imagePath, Runnable onSelect) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0x8B5E3C), 4));
        panel.setBackground(new Color(0x5C4033));

        //character image
        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            iconLabel.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(imagePath))));
        } catch (Exception e) {
            iconLabel.setText("Image not found");
            iconLabel.setForeground(Color.RED);
        }

        //name label
        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Serif", Font.BOLD, 22));
        nameLabel.setForeground(new Color(0xE9DCC9));

        //button to select the character
        JButton selectButton = new JButton("SELECT");
        selectButton.setFont(new Font("Serif", Font.BOLD, 16));
        selectButton.setBackground(new Color(0xA9744F));
        selectButton.setForeground(Color.WHITE);
        selectButton.setFocusPainted(false);
        selectButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        selectButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        selectButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                selectButton.setBackground(new Color(0xC69C6D));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                selectButton.setBackground(new Color(0xA9744F));
            }
        });

        selectButton.addActionListener(e -> onSelect.run());

        panel.add(nameLabel, BorderLayout.NORTH);
        panel.add(iconLabel, BorderLayout.CENTER);
        panel.add(selectButton, BorderLayout.SOUTH);

        return panel;
    }

    //start the game according to the character choice
    private void startGame(String type) {
        int width = 40;
        int height = 30;
        double fillPercent = 0.45;

        //generate initial game map
        GameMap firstMap = new GameMap(width, height, fillPercent);
        int[] entry = firstMap.getEntryPosition();

        //create player at map entry position
        Player player = type.equals("Wizard")
                ? new Wizard("Wizard", entry[0], entry[1])
                : new Duelist("Duelist", entry[0], entry[1]);

        //create game world with initial map and player
        GameWorld world = new GameWorld(firstMap, player);

        //setup GUI components
        SidePanel sidePanel = new SidePanel();
        sidePanel.setWorld(world);

        GameCanvas canvas = new GameCanvas(world, player, sidePanel);
        sidePanel.updateStatus(player, world);

        parentFrame.getContentPane().removeAll();
        parentFrame.setLayout(new BorderLayout());
        parentFrame.add(canvas, BorderLayout.CENTER);
        parentFrame.add(sidePanel, BorderLayout.EAST);
        parentFrame.pack();
        parentFrame.revalidate();
        canvas.requestFocusInWindow();
    }
}
