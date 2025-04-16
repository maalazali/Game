package servermap;
import clientmap.ClientMap;
import clientmap.MapNode;
import data.ClientGameState;
import data.EnumTreasureState;
import network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ServerMap {
    private static final Logger logger = LoggerFactory.getLogger(ServerMap.class);

    public static void GetAndPrintServerMap(Network network) {
        try {
            // Abfrage des Spielstatus, um vollständige Karte zu erhalten
            ClientGameState gameState = network.getGameState();

            // Überprüfung, ob die Karte verfügbar ist
            if (gameState.getClientMap() != null) {
                ClientMap fullMap = gameState.getClientMap();

                // Ausgabe der Karte in der Konsole
                printMapToConsole(fullMap);
            } else {
                logger.info("Die Karte ist noch nicht vollständig verfügbar.");
                System.out.println("Die Karte ist noch nicht vollständig verfügbar.");
            }

        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der Karte: " + e.getMessage());
            System.err.println("Fehler beim Abrufen der Karte: " + e.getMessage());
        }
    }

    public static void printMapToConsole(ClientMap map) {
        // Ermitteln der maximalen Dimensionen der Karte
        int width = 0;
        int height = 0;
        for (MapNode node : map.getClientMap().values()) {
            width = Math.max(width, node.getCoordinate().getX());
            height = Math.max(height, node.getCoordinate().getY());
        }

        // Erstellen eines dynamischen Arrays basierend auf den Dimensionen
        char[][] visualMap = new char[width + 1][height + 1];
        // Initialisierung der Karte mit Leerzeichen
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                visualMap[x][y] = ' ';
            }
        }

        // Füllen der Karte basierend auf den MapNode-Daten
        for (Map.Entry<String, MapNode> entry : map.getClientMap().entrySet()) {
            MapNode node = entry.getValue();
            int x = node.getCoordinate().getX();
            int y = node.getCoordinate().getY();

            switch (node.getEnumField()) {
                case GRAS -> visualMap[x][y] = 'G';
                case WATER -> visualMap[x][y] = 'W';
                case MOUNTAIN -> visualMap[x][y] = 'M';
            }

            if (node.getEnumFortState() != null) {
                switch (node.getEnumFortState()) {
                    case MYFORTPRESENT -> visualMap[x][y] = 'F';
                    case ENEMYFORTPRESENT -> visualMap[x][y] = 'E';
                }
            }

            if (node.getEnumTreasureState() != null && node.getEnumTreasureState() == EnumTreasureState.MYTREASUREPRESENT) {
                visualMap[x][y] = 'T';
            }

            // Spielerpositionen hinzufügen
            if (node.getEnumPlayerPosition() != null) {
                switch (node.getEnumPlayerPosition()) {
                    case MYPLAYERPOSITION -> visualMap[x][y] = 'P';
                    case ENEMYPLAYERPOSITION -> visualMap[x][y] = 'E';
                    case BOTHPLAYERPOSITION -> visualMap[x][y] = 'B';
                }
            }
        }

        // Ausgabe der Karte in der Konsole
        for (char[] row : visualMap) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }
}
