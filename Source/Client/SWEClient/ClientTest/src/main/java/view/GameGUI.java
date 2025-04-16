package view;

import javax.swing.*;
import java.awt.*;
import clientmap.ClientMap;
import clientmap.Coordinate;
import clientmap.MapNode;
import data.EnumTreasureState;

public class GameGUI extends JFrame {

    private JPanel mapPanel;
    private JLabel statusLabel;
    private JLabel treasureLabel;
    private Coordinate treasurePosition = null;
    private boolean treasureLabelUpdate = false;

    public GameGUI() {
        setTitle("Game GUI");
        setSize(1200, 1200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Statusanzeige
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statusLabel = new JLabel("Game Status: Waiting...");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        //add(statusLabel, BorderLayout.SOUTH);

        treasureLabel =new JLabel("Treasure: X");
        treasureLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        treasureLabel.setFont(new Font("Arial", Font.BOLD, 16));
        //add(treasureLabel, BorderLayout.SOUTH);
        JPanel statusLabels = new JPanel();
        statusLabels.setLayout(new GridLayout(1, 2)); // Zwei Spalten für "Round" und "Treasure"
        statusLabels.add(statusLabel);
        statusLabels.add(treasureLabel);
        statusPanel.add(statusLabels, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.SOUTH); // Unterhalb der Karte platzieren
    }

    public void updateMap(ClientMap map, boolean treasureCollected) {
        // Entfernen des vorherigen Inhalts
        if (mapPanel != null) {
            remove(mapPanel);
        }

        int width = 0;
        int height = 0;
        for (MapNode node : map.getClientMap().values()) {
            width = Math.max(width, node.getCoordinate().getX());
            height = Math.max(height, node.getCoordinate().getY());
        }
        mapPanel = new JPanel(new GridLayout(width +1, height +1));
        add(mapPanel, BorderLayout.CENTER);

        // Befüllen des Rasters mit Karteninhalten
        for (int x = 0; x <= width; x++) {
            for (int y = 0; y <= height; y++) {
                JPanel cell = new JPanel();
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                cell.setBackground(Color.WHITE); // Standardfarbe

                MapNode node = map.getClientMap().get(x + "" + y);
                if (node != null) {
                    if (node.getEnumField() != null) {
                        switch (node.getEnumField()) {
                            case GRAS -> cell.setBackground(Color.GREEN);
                            case WATER -> cell.setBackground(Color.BLUE);
                            case MOUNTAIN -> cell.setBackground(Color.GRAY);
                        }
                    }
                    JLabel content = new JLabel();
                    content.setHorizontalAlignment(SwingConstants.CENTER);
                    content.setVerticalAlignment(SwingConstants.CENTER);


                    if (node.getEnumTreasureState() == EnumTreasureState.MYTREASUREPRESENT && !treasureCollected) {
                        content.setIcon(scaleIcon("src/main/resources/treasure.png",50, 50)); // Schatzbild
                        treasurePosition = new Coordinate(x, y);
                    }else if(node.getEnumTreasureState() == EnumTreasureState.MYTREASUREPRESENT && treasureCollected){
                        treasurePosition = new Coordinate(x, y);
                    }else if (treasurePosition != null && treasurePosition.equals(new Coordinate(x, y))) {
                        content.setIcon(scaleIcon("src/main/resources/treasure_collected.png",75, 75)); // Gesammelter Schatz
                    }

                    if (node.getEnumPlayerPosition() != null) {
                        switch (node.getEnumPlayerPosition()) {
                            case MYPLAYERPOSITION -> content.setIcon(scaleIcon("src/main/resources/player.png",50, 50)); // Spielerbild
                            case ENEMYPLAYERPOSITION -> content.setIcon(scaleIcon("src/main/resources/enemy.png",50, 50)); // Gegnerbild
                            case BOTHPLAYERPOSITION -> content.setIcon(scaleIcon("src/main/resources/both_players.png",50, 50)); // Beide Spieler
                        }
                    }


                    // Darstellung der Burgen
                    if (node.getEnumFortState() != null) {
                        switch (node.getEnumFortState()) {
                            case MYFORTPRESENT -> content.setIcon(scaleIcon("src/main/resources/player_fort.png",50, 50)); // Eigene Burg
                            case ENEMYFORTPRESENT -> content.setIcon(scaleIcon("src/main/resources/enemy_fort.png",75, 75)); // Gegnerische Burg
                        }
                    }
                    // Update the status label
                    updateStatusLabel(treasureCollected);
                    cell.add(content);
                }
                mapPanel.add(cell);
            }
        }


        // Aktualisierung der Anzeige
        mapPanel.revalidate();
        mapPanel.repaint();
    }


    public void updateStatus(String status) {
        statusLabel.setText(status);
    }

    public void updateStatusLabel(boolean treasureCollected) {
        if (treasureCollected && !treasureLabelUpdate) {
            treasureLabel.setText("Treasure:");
            treasureLabel.setIcon(scaleIcon("src/main/resources/treasure.png", 20, 20)); // Icon hinzufügen
            treasureLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            treasureLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            treasureLabelUpdate = true;
        }
    }

    private ImageIcon scaleIcon(String path, int width, int height) {
        try {
            // Lade das Bild aus dem Ressourcenpfad
            ImageIcon icon = new ImageIcon(path);
            // Skalieren des Bildes
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            // Zurückgeben eines neuen Icons
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading or scaling icon: " + path);
            return null;
        }
    }

}

