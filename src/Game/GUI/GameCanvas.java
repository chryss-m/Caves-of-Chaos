package Game.GUI;

import Game.Enemy.Enemy;
import Game.Items.Item;
import Game.Items.Trap;
import Game.Items.Weapon;
import Game.Map.GameMap;
import Game.Map.GameWorld;
import Game.Player.Player;
import Game.Player.Wizard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public class GameCanvas extends JPanel {

    private final GameWorld world;
    private final Player player;
    private final SidePanel sidePanel;

    private static final int TILE_SIZE = 20;

    //icons for weapons and special items
    private final BufferedImage arcaneWandIcon;
    private final BufferedImage fireStaffIcon;
    private final BufferedImage ironSwordIcon;
    private final BufferedImage greatAxeIcon;
    private final BufferedImage crisisGemIcon;

    private Point playerAttackTarget = null;
    private Point enemyAttackTarget = null;
    private Point wizardSpellTarget = null;


    //constructor
    public GameCanvas(GameWorld world, Player player, SidePanel sidePanel) {
        this.world = world;
        this.player = player;
        this.sidePanel = sidePanel;

        setPreferredSize(new Dimension(
                world.getCurrentMap().getWidth() * TILE_SIZE,
                world.getCurrentMap().getHeight() * TILE_SIZE
        ));

        setFocusable(true);
        requestFocusInWindow();

        //handles keyboard input
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
                repaint();
            }
        });

        //load icons for weapons
        arcaneWandIcon = ImageLoader.load("/Game/GUI/arcanewand.png");
        fireStaffIcon  = ImageLoader.load("/Game/GUI/firestaff.png");
        ironSwordIcon  = ImageLoader.load("/Game/GUI/ironsword.png");
        greatAxeIcon   = ImageLoader.load("/Game/GUI/axe.png");
        crisisGemIcon  = ImageLoader.load("/Game/GUI/crisis_gem.png");
    }

    //keyboard
    private void handleKeyPress(KeyEvent e) {
        int key = e.getKeyCode();
        int dx = 0, dy = 0;


        switch (key) {
            //movement
            case KeyEvent.VK_UP, KeyEvent.VK_W    -> dy = -1;
            case KeyEvent.VK_DOWN, KeyEvent.VK_S  -> dy = 1;
            case KeyEvent.VK_LEFT, KeyEvent.VK_A  -> dx = -1;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> dx = 1;

            //resting
            case KeyEvent.VK_R -> {
                if (player.isResting()) {
                    player.stopResting();
                    sidePanel.log(" You stop resting and stand up.");
                } else {
                    player.startResting();
                    sidePanel.log(" You sit down to rest... (regenerate over time)");
                }
                sidePanel.updateStatus(player, world);
                return;
            }

            //use Health Potion
            case KeyEvent.VK_H -> {
                if (player.getInventory().getHealthPotions() == 0) {
                    sidePanel.log(" No Health Potions left!");
                } else {
                    boolean used = player.tryHealPotion();
                    if (used) {
                        sidePanel.log("Used a Health Potion.");
                    } else {
                        sidePanel.log(" You're already at full HP.");
                    }
                }
                sidePanel.updateStatus(player, world);
                return;
            }


            //use Mana Potion
            case KeyEvent.VK_M -> {
                if (player.getInventory().getManaPotions() == 0) {
                    sidePanel.log(" No Mana Potions left!");
                } else {
                    boolean used = player.tryManaPotion();
                    if (used) {
                        sidePanel.log(" Used a Mana Potion.");
                    } else {
                        sidePanel.log(" You're already at full MP.");
                    }
                }
                sidePanel.updateStatus(player, world);
                return;
            }
            case KeyEvent.VK_C -> {
                world.setCurrentLevel(9);
                GameMap map = world.getCurrentMap();

                int[] entry = map.getEntryPosition();
                player.setPosition(entry[0], entry[1]);

                player.levelUp(6);

                sidePanel.log("🌀 Cheat activated! You entered the final level!");
                sidePanel.updateStatus(player, world);
                repaint();
                return;
            }



            //pick-swap
            case KeyEvent.VK_P -> {
                List<Item> items = world.getCurrentMap().getItemsAt(player.getX(), player.getY());
                Weapon foundWeapon = items.stream()
                        .filter(item -> item instanceof Weapon)
                        .map(item -> (Weapon) item)
                        .findFirst()
                        .orElse(null);

                if (foundWeapon != null) {
                    if (player.getEquippedWeapon() != null) {
                        world.getCurrentMap().addItem(player.getX(), player.getY(), player.getEquippedWeapon());
                        sidePanel.log("🔁 Swapped " + player.getEquippedWeapon().getName() + " for " + foundWeapon.getName());
                    } else {
                        sidePanel.log("🗡️ Equipped: " + foundWeapon.getName());
                    }
                    player.equipWeapon(foundWeapon);
                    items.remove(foundWeapon);
                } else {
                    sidePanel.log(" No weapon here to equip.");
                }

                sidePanel.updateStatus(player, world);
                return;
            }

            case KeyEvent.VK_SPACE -> {
                if (player instanceof Wizard wizard) {

                    Enemy attacked = wizard.attackNearestVisibleEnemy(world);

                    Point target = wizard.getLastSpellTarget();
                    if (target != null) {

                        wizardSpellTarget = target;
                        Timer timer = new Timer(200, evt -> {
                            wizardSpellTarget = null;
                            repaint();
                        });
                        timer.setRepeats(false);
                        timer.start();

                        sidePanel.log(" Spell cast at " + target.x + "," + target.y);

                        if (attacked != null && !attacked.isAlive()) {
                            world.removeEnemy(attacked);
                            sidePanel.log("💀 Defeated: " + attacked.getName());
                        }

                    } else {
                        sidePanel.log("No visible enemy in range.");
                    }

                } else {

                    int px = player.getX();
                    int py = player.getY();
                    boolean attacked = false;

                    for (Enemy enemy : world.getEnemies()) {
                        int ex = enemy.getX(), ey = enemy.getY();
                        if (Math.abs(ex - px) + Math.abs(ey - py) == 1 && enemy.isAlive()) {
                            player.attack(enemy);


                            playerAttackTarget = new Point(ex, ey);
                            Timer timer = new Timer(100, evt -> {
                                playerAttackTarget = null;
                                repaint();
                            });
                            timer.setRepeats(false);
                            timer.start();

                            sidePanel.log("⚔You attacked " + enemy.getName() + "!");

                            if (!enemy.isAlive()) {
                                world.removeEnemy(enemy);
                                sidePanel.log("💀 Defeated: " + enemy.getName());
                            }

                            attacked = true;
                            break;
                        }
                    }

                    if (!attacked) {
                        sidePanel.log("No adjacent enemy to attack.");
                    }
                }

                sidePanel.updateStatus(player, world);
                repaint();
                return;
            }




        }

        //movement
        if (dx != 0 || dy != 0) {
            player.move(dx, dy, world);

            for (Enemy enemy : world.getEnemies()) {
                enemy.takeTurn(world, player);
            }

            Point enemyTarget = world.getLastEnemyAttackTarget();
            if (enemyTarget != null) {
                enemyAttackTarget = enemyTarget;
                world.clearLastEnemyAttackTarget();

                Timer timer = new Timer(100, evt -> {
                    enemyAttackTarget = null;
                    repaint();
                });
                timer.setRepeats(false);
                timer.start();
            }


            if (player.isResting()) {
                player.stopResting();
                sidePanel.log("Movement interrupted your rest!!");
            }


            List<Item> items = world.getCurrentMap().getItemsAt(player.getX(), player.getY());
            List<Item> toRemove = new ArrayList<>();

            for (Item item : items) {
                boolean pickedUp = false;

                if (item instanceof Trap) {
                    player.pickUp(item);
                    pickedUp = true;
                } else if (item instanceof Weapon weapon) {
                    if (player.getEquippedWeapon() == null) {
                        player.equipWeapon(weapon);
                        pickedUp = true;
                        toRemove.add(item);
                    }
                } else {
                    player.pickUp(item);
                    pickedUp = true;
                    toRemove.add(item);
                }

                if (pickedUp) {
                    sidePanel.log("🔸 Picked up: " + item.getName());
                } else if (item instanceof Weapon) {
                    sidePanel.log("Already carrying a weapon. Ignored: " + item.getName());
                }
            }

            world.getCurrentMap().getItemsAt(player.getX(), player.getY()).removeAll(toRemove);
            sidePanel.updateStatus(player, world);

        } else if (player.isResting()) {
            player.rest();
            sidePanel.log("Resting... Recovered some HP/MP.");
            sidePanel.updateStatus(player, world);
        }
    }

    //graphics for everything-> player,tiles,items, enemies...
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GameMap gameMap = world.getCurrentMap();
        gameMap.updateVisibility(player.getX(), player.getY());

        //draw tiles
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                GameMap.VisibilityState visibility = gameMap.getVisibility(x, y);
                if (visibility == GameMap.VisibilityState.UNKNOWN) continue;

                int drawX = x * TILE_SIZE;
                int drawY = y * TILE_SIZE;
                Color outerColor = Color.BLACK, innerColor = Color.DARK_GRAY;

                switch (gameMap.getTileType(x, y)) {
                    case WALL -> {
                        outerColor = new Color(0x3B2B20);
                        innerColor = new Color(0x604235);
                    }
                    case FLOOR -> {
                        outerColor = new Color(0x624337);
                        innerColor = new Color(0xECC3A6);
                    }
                    case ENTRY -> {
                        outerColor = new Color(0xEEFC31);
                        innerColor = new Color(0xF8E950);
                    }
                    case STAIRS -> {
                        outerColor = new Color(0xEF1E1E);
                        innerColor = new Color(0xFCA1A1);
                    }
                }

                if (visibility == GameMap.VisibilityState.FOGGED) {
                    outerColor = outerColor.darker();
                    innerColor = innerColor.darker().darker();
                }

                g.setColor(outerColor);
                g.fillRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                g.setColor(innerColor);
                g.fillRect(drawX + 1, drawY + 1, TILE_SIZE - 2, TILE_SIZE - 2);


                if (gameMap.getTileType(x, y) == GameMap.TileType.STAIRS) {
                    g.setColor(Color.BLACK);
                    Polygon triangle = new Polygon();
                    triangle.addPoint(drawX + TILE_SIZE / 2, drawY + 4);
                    triangle.addPoint(drawX + 4, drawY + TILE_SIZE - 4);
                    triangle.addPoint(drawX + TILE_SIZE - 4, drawY + TILE_SIZE - 4);
                    g.fillPolygon(triangle);
                }
            }
        }

        //items
        for (Map.Entry<Point, List<Item>> entry : gameMap.itemsPerTile.entrySet()) {
            Point p = entry.getKey();
            if (gameMap.getVisibility(p.x, p.y) == GameMap.VisibilityState.UNKNOWN) continue;

            int drawX = p.x * TILE_SIZE;
            int drawY = p.y * TILE_SIZE;

            for (Item item : entry.getValue()) {
                String name = item.getName().toLowerCase();

                switch (name) {
                    case "trap" -> {
                        g.setColor(Color.BLACK);
                        g.fillRect(drawX + TILE_SIZE / 2 - 1, drawY + TILE_SIZE / 2 - 1, 2, 2);
                    }
                    case "health potion" -> {
                        g.setColor(new Color(200, 0, 0));
                        g.fillOval(drawX + 6, drawY + 6, TILE_SIZE - 12, TILE_SIZE - 12);
                        g.setColor(new Color(252, 161, 161));
                        g.drawOval(drawX + 5, drawY + 5, TILE_SIZE - 10, TILE_SIZE - 10);
                    }
                    case "mana potion" -> {
                        g.setColor(new Color(50, 50, 255));
                        g.fillOval(drawX + 6, drawY + 6, TILE_SIZE - 12, TILE_SIZE - 12);
                        g.setColor(new Color(150, 150, 255, 128));
                        g.drawOval(drawX + 5, drawY + 5, TILE_SIZE - 10, TILE_SIZE - 10);
                    }
                    default -> {

                        if (item instanceof Weapon weapon) {
                            BufferedImage icon = switch (weapon.getName()) {
                                case "Arcane Wand" -> arcaneWandIcon;
                                case "Fire Staff" -> fireStaffIcon;
                                case "Iron Sword" -> ironSwordIcon;
                                case "Great Axe" -> greatAxeIcon;
                                default -> null;
                            };
                            if (icon != null) {
                                g.drawImage(icon, drawX + 2, drawY + 2, TILE_SIZE - 4, TILE_SIZE - 4, null);
                                continue;
                            }
                        }

                        if (item.getName().equalsIgnoreCase("Crisis Gem")) {
                            if (crisisGemIcon != null) {
                                g.drawImage(crisisGemIcon, drawX + 2, drawY + 2, TILE_SIZE - 4, TILE_SIZE - 4, null);
                            } else {
                                g.setColor(Color.MAGENTA);
                                g.fillOval(drawX + 6, drawY + 6, TILE_SIZE - 12, TILE_SIZE - 12);
                            }
                            continue;
                        }

                        //default: unknown item
                        g.setColor(Color.YELLOW);
                        g.fillOval(drawX + 6, drawY + 6, TILE_SIZE - 12, TILE_SIZE - 12);
                    }
                }
            }
        }

        //player
        int px = player.getX(), py = player.getY();
        if (player.getClass().getSimpleName().equals("Wizard")) {
            g.setColor(new Color(219, 238, 87));
            g.fillOval(px * TILE_SIZE + 2, py * TILE_SIZE + 2, TILE_SIZE - 4, TILE_SIZE - 4);
            g.setColor(new Color(60, 40, 0));
            g.drawString("W", px * TILE_SIZE + TILE_SIZE / 4, py * TILE_SIZE + TILE_SIZE * 3 / 4);
        } else {
            g.setColor(new Color(239, 12, 12));
            g.fillRect(px * TILE_SIZE + 2, py * TILE_SIZE + 2, TILE_SIZE - 4, TILE_SIZE - 4);
            g.setColor(Color.WHITE);
            g.drawString("D", px * TILE_SIZE + TILE_SIZE / 4, py * TILE_SIZE + TILE_SIZE * 3 / 4);
        }

        //enemies
        for (var enemy : world.getEnemies()) {
            int ex = enemy.getX(), ey = enemy.getY();
            if (gameMap.getVisibility(ex, ey) == GameMap.VisibilityState.VISIBLE) {
                int drawX = ex * TILE_SIZE;
                int drawY = ey * TILE_SIZE;

                g.setColor(Color.BLACK);
                Polygon triangle = new Polygon();
                triangle.addPoint(drawX + TILE_SIZE / 2, drawY + 4);
                triangle.addPoint(drawX + 4, drawY + TILE_SIZE - 4);
                triangle.addPoint(drawX + TILE_SIZE - 4, drawY + TILE_SIZE - 4);
                g.fillPolygon(triangle);
            }
        }

        //battle
        if (playerAttackTarget != null) {
            int ax = playerAttackTarget.x * TILE_SIZE;
            int ay = playerAttackTarget.y * TILE_SIZE;
            g.setColor(new Color(255, 0, 0, 128)); // κόκκινο ημιδιαφανές
            g.fillRect(ax, ay, TILE_SIZE, TILE_SIZE);
        }

        if (wizardSpellTarget != null) {
            int ax = wizardSpellTarget.x * TILE_SIZE;
            int ay = wizardSpellTarget.y * TILE_SIZE;
            g.setColor(new Color(0, 255, 255, 128)); // γαλάζιο ημιδιαφανές
            g.fillRect(ax, ay, TILE_SIZE, TILE_SIZE);
        }


        if (enemyAttackTarget != null) {
            int ax = enemyAttackTarget.x * TILE_SIZE;
            int ay = enemyAttackTarget.y * TILE_SIZE;
            g.setColor(new Color(255, 255, 0, 128)); // κίτρινο ημιδιαφανές
            g.fillRect(ax, ay, TILE_SIZE, TILE_SIZE);
        }

    }
}