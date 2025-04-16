package view;

import clientmap.ClientMap;
import clientmap.Coordinate;
import clientmap.MapNode;
import data.EnumTreasureState;

import java.util.Map;

public class CLIView {

    private Coordinate treasurePosition = null;

    public void displayMap(ClientMap map, boolean treasureCollected) {
        // Ermitteln der maximalen Dimensionen der Karte
        int width = 0;
        int height = 0;
        for (MapNode node : map.getClientMap().values()) {
            width = Math.max(width, node.getCoordinate().getX());
            height = Math.max(height, node.getCoordinate().getY());
        }


        String[][] visualMap = new String[width + 1][height + 1];
        for (int x = 0; x <= width; x++) {
            for (int y = 0; y <= height; y++) {
                visualMap[x][y] = "⬜";
            }
        }

        // Füllen der Karte mit den Elementen basierend auf den MapNode-Daten
        for (Map.Entry<String, MapNode> entry : map.getClientMap().entrySet()) {
            MapNode node = entry.getValue();
            int x = node.getCoordinate().getX();
            int y = node.getCoordinate().getY();

            switch (node.getEnumField()) {
                case GRAS -> visualMap[x][y] = "🌱"; // Gras
                case WATER -> visualMap[x][y] = "🌊"; // Wasser
                case MOUNTAIN -> visualMap[x][y] = "⛰️"; // Berg
            }
            if (node.getEnumTreasureState() != null) {
                if (node.getEnumTreasureState() == EnumTreasureState.MYTREASUREPRESENT && !treasureCollected) {
                    visualMap[x][y] = "💎";
                    treasurePosition = new Coordinate(x, y);
                }else if(node.getEnumTreasureState() == EnumTreasureState.MYTREASUREPRESENT && treasureCollected){
                    visualMap[x][y] = "✔️";
                    treasurePosition = new Coordinate(x, y);
                }else if (treasurePosition != null && treasurePosition.equals(new Coordinate(x, y))) {
                    visualMap[x][y] = "✔️";
                }
            }

            // Darstellung der Burgen
            if (node.getEnumFortState() != null) {
                switch (node.getEnumFortState()) {
                    case MYFORTPRESENT -> visualMap[x][y] = "🏰"; // Eigene Burg
                    case ENEMYFORTPRESENT -> visualMap[x][y] = "🎯"; // Gegnerische Burg
                }
            }

            // Spielerpositionen hinzufügen
            if (node.getEnumPlayerPosition() != null) {
                switch (node.getEnumPlayerPosition()) {
                    case MYPLAYERPOSITION -> visualMap[x][y] = "🤖"; // Spieler
                    case ENEMYPLAYERPOSITION -> visualMap[x][y] = "👾"; // Gegner
                    case BOTHPLAYERPOSITION -> visualMap[x][y] = "⚔️"; // Beide Spieler
                }
            }
        }

        // Ausgabe der Karte in der Konsole
        for (String[] row : visualMap) {
            for (String cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public void displayGameStatus(String status) {
        System.out.println("Spielstatus: " + status);
    }

    public void displayError(String error) {
        System.err.println("Fehler: " + error);
    }
}

