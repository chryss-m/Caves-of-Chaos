package Game;

import Game.GUI.CharacterSelectionPanel;

import javax.swing.*;


//Constructs the main game window and initializes the character selection screen.
public class Game extends JFrame {

    public Game() {
        super("Τα Σπήλαια του Χάους");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CharacterSelectionPanel panel = new CharacterSelectionPanel(this);
        panel.setPreferredSize(new java.awt.Dimension(800, 600));

        add(panel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Game();
            }
        });
    }
}

