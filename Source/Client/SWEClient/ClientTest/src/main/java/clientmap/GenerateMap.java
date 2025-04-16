package clientmap;
import data.EnumField;
import data.EnumFortState;
import data.EnumTreasureState;

import java.util.*;



public class GenerateMap {
    private ValidateMap mapValidator = new ValidateMap();
    /*
    public static ClientMap generateMap() {
        int WIDTH = 5;
        int HEIGHT = 10;
        int MIN_GRAS = 24;
        int MIN_MOUNTAIN = 5;
        int MIN_WATER = 7;
        Random rand = new Random();
        Map<String, MapNode> map = new HashMap<>();                 //Map<Stiring für die Coordinaten, MapNode>
        String startKey = null;
        Coordinate playerCoordinate = null;
        Player myPlayer = null;

        char[][] mapCoordinate = new char[HEIGHT][WIDTH];
        int AnzGras = 0, AnzMountain = 0, AnzWater = 0;
        boolean fortSet = false;
        // Zuerst zufällig initialisieren
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Coordinate coordinate = new Coordinate(i, j);
                String key = String.valueOf(i) + String.valueOf(j);
                int fieldProb = rand.nextInt(100);
                if (fieldProb > 52 && AnzGras < MIN_GRAS) { //magic number mit Variablen arbeiten
                    mapCoordinate[i][j] = 'G';
                    if(!fortSet){
                        mapCoordinate[i][j] = 'F';
                        MapNode myFortNode = new MapNode(EnumField.GRAS, EnumFortState.MYFORTPRESENT, EnumTreasureState.NOORUNKNOWNTREASURESTATE, coordinate);
                        myFortNode.setEnumPlayerPosition(EnumPlayerPosition.MYPLAYERPOSITION);
                        map.put(key, myFortNode);
                        fortSet = true;
                        startKey = key;
                    }else{
                        map.put(key, new MapNode(EnumField.GRAS, EnumFortState.NOORUNKNOWNFORTSTATE, EnumTreasureState.NOORUNKNOWNTREASURESTATE, coordinate));
                    }
                    AnzGras++;
                } else if (fieldProb > 38 && AnzWater < MIN_WATER) {
                    boolean isEdge = (i == 0 || i == HEIGHT - 1 || j == 0 || j == WIDTH - 1);
                    if (isEdge) {
                        int edgeWaterCount = 0;
                        for (int x = 0; x < HEIGHT; x++) {
                            if (mapCoordinate[x][0] == 'W') edgeWaterCount++;
                            if (mapCoordinate[x][WIDTH - 1] == 'W') edgeWaterCount++;
                        }
                        for (int y = 0; y < WIDTH; y++) {
                            if (mapCoordinate[0][y] == 'W') edgeWaterCount++;
                            if (mapCoordinate[HEIGHT - 1][y] == 'W') edgeWaterCount++;
                        }
                        if (edgeWaterCount >= 2) continue; // Keine weiteren Wasserfelder an den Kanten platzieren
                    }
                    mapCoordinate[i][j] = 'W';
                    map.put(key, new MapNode(EnumField.WATER, EnumFortState.NOORUNKNOWNFORTSTATE, EnumTreasureState.NOORUNKNOWNTREASURESTATE, coordinate));
                    AnzWater++;
                } else if (fieldProb > 28 && AnzMountain < MIN_MOUNTAIN) {
                    mapCoordinate[i][j] = 'M';
                    map.put(key, new MapNode(EnumField.MOUNTAIN, EnumFortState.NOORUNKNOWNFORTSTATE, EnumTreasureState.NOORUNKNOWNTREASURESTATE, coordinate));
                    AnzMountain++;
                }
                else {
                    int restProb = rand.nextInt(72);
                    if (fieldProb > 24){
                        AnzGras++;
                        mapCoordinate[i][j] = 'G';
                        map.put(key, new MapNode(EnumField.GRAS, EnumFortState.NOORUNKNOWNFORTSTATE, EnumTreasureState.NOORUNKNOWNTREASURESTATE, coordinate));
                    }
                    else if (fieldProb > 10){
                        AnzWater++;
                        mapCoordinate[i][j] = 'W';
                        map.put(key, new MapNode(EnumField.WATER, EnumFortState.NOORUNKNOWNFORTSTATE, EnumTreasureState.NOORUNKNOWNTREASURESTATE, coordinate));
                    }
                    else {
                        AnzMountain++;
                        mapCoordinate[i][j] = 'M';
                        map.put(key, new MapNode(EnumField.MOUNTAIN, EnumFortState.NOORUNKNOWNFORTSTATE, EnumTreasureState.NOORUNKNOWNTREASURESTATE, coordinate));
                    }
                }
            }


        }




        // Ausgabe der Karte und Anzahl der Felder, später Anzahl der Felder löschen
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                System.out.print(mapCoordinate[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("Gras: " + AnzGras);
        System.out.println("Berge: " + AnzMountain);
        System.out.println("Wasser: " + AnzWater);


        return new ClientMap(map);
    }

     */
    private static final int WIDTH = 10;
    private static final int HEIGHT = 5;
    private static final int MIN_GRASS = 24;
    private static final int MIN_MOUNTAIN = 5;
    private static final int MIN_WATER = 7;
    private static final int MAX_EDGE_WATER = 2;

    public static ClientMap generateMap() {
        Random rand = new Random();
        Map<String, MapNode> map = new HashMap<>();
        char[][] mapVisual = new char[WIDTH][HEIGHT];
        int grassCount = 0, mountainCount = 0, waterCount = 0;

        int topEdgeWater = 0, bottomEdgeWater = 0, leftEdgeWater = 0, rightEdgeWater = 0;

        // 1. Felder zufällig erstellen und Bedingungen einhalten
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Coordinate coordinate = new Coordinate(x, y);
                String key = x + "" + y;
                int fieldType = rand.nextInt(100);
                if (fieldType < 48) {
                    if (grassCount < MIN_GRASS) {
                        map.put(key, new MapNode(EnumField.GRAS, EnumFortState.NOORUNKNOWNFORTSTATE, EnumTreasureState.NOORUNKNOWNTREASURESTATE, coordinate));
                        mapVisual[x][y] = 'G';
                        grassCount++;
                    } else {
                        // Fülle mit Bergen, wenn das Gras-Minimum erreicht ist
                        map.put(key, new MapNode(EnumField.MOUNTAIN, EnumFortState.NOORUNKNOWNFORTSTATE, EnumTreasureState.NOORUNKNOWNTREASURESTATE, coordinate));
                        mapVisual[x][y] = 'M';
                        mountainCount++;
                    }
                } else if (fieldType < 62 && mountainCount < MIN_MOUNTAIN) {
                    map.put(key, new MapNode(EnumField.MOUNTAIN, EnumFortState.NOORUNKNOWNFORTSTATE, EnumTreasureState.NOORUNKNOWNTREASURESTATE, coordinate));
                    mapVisual[x][y] = 'M';
                    mountainCount++;
                } else if (fieldType < 76 && waterCount < MIN_WATER && canPlaceWaterOnEdge(x, y, topEdgeWater, bottomEdgeWater, leftEdgeWater, rightEdgeWater)) {
                    map.put(key, new MapNode(EnumField.WATER, EnumFortState.NOORUNKNOWNFORTSTATE, EnumTreasureState.NOORUNKNOWNTREASURESTATE, coordinate));
                    mapVisual[x][y] = 'W';
                    waterCount++;

                    if (x == 0) topEdgeWater++;
                    if (x == WIDTH - 1) bottomEdgeWater++;
                    if (y == 0) leftEdgeWater++;
                    if (y == HEIGHT - 1) rightEdgeWater++;
                } else {
                    map.put(key, new MapNode(EnumField.MOUNTAIN, EnumFortState.NOORUNKNOWNFORTSTATE, EnumTreasureState.NOORUNKNOWNTREASURESTATE, coordinate));
                    mapVisual[x][y] = 'M';
                    mountainCount++;
                }
            }
        }

        // 2. Fort platzieren
        placeFortOnGrass(map, mapVisual);

        // 3. Schatz platzieren
        placeTreasureOnGrass(map, mapVisual);

        // 4. Karte ausgeben
        printMap(mapVisual);

        return new ClientMap(map);
    }

    private static boolean canPlaceWaterOnEdge(int x, int y, int top, int bottom, int left, int right) {
        if (x == 0 && top >= MAX_EDGE_WATER) return false;        // Obere Kante
        if (x == WIDTH - 1 && bottom >= MAX_EDGE_WATER) return false; // Untere Kante
        if (y == 0 && left >= MAX_EDGE_WATER) return false;       // Linke Kante
        if (y == HEIGHT - 1 && right >= MAX_EDGE_WATER) return false;  // Rechte Kante
        return true;
    }

    private static void placeFortOnGrass(Map<String, MapNode> map, char[][] mapVisual) {
        Random rand = new Random();
        while (true) {
            int x = rand.nextInt(WIDTH - 2) + 1;
            int y = rand.nextInt(HEIGHT - 2) + 1;
            String key = x + "" + y;
            MapNode node = map.get(key);
            if (node.getEnumField() == EnumField.GRAS && node.getEnumFortState() == EnumFortState.NOORUNKNOWNFORTSTATE) {
                if (hasAdjacentGrass(map, x, y)) {
                    node.setEnumFortState(EnumFortState.MYFORTPRESENT);
                    mapVisual[x][y] = 'F';
                    break;
                }
            }
        }
    }

    private static boolean hasAdjacentGrass(Map<String, MapNode> map, int x, int y) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            String key = (x + dir[0]) + "" + (y + dir[1]);
            MapNode adjacentNode = map.get(key);
            if (adjacentNode != null && adjacentNode.getEnumField() == EnumField.GRAS) {
                return true;
            }
        }
        return false;
    }

    private static void placeTreasureOnGrass(Map<String, MapNode> map, char[][] mapVisual) {
        Random rand = new Random();
        while (true) {
            int x = rand.nextInt(WIDTH - 2) + 1;
            int y = rand.nextInt(HEIGHT - 2) + 1;
            String key = x + "" + y;
            MapNode node = map.get(key);
            if (node.getEnumField() == EnumField.GRAS && node.getEnumFortState() != EnumFortState.MYFORTPRESENT && node.getEnumTreasureState() == EnumTreasureState.NOORUNKNOWNTREASURESTATE) {
                node.setEnumTreasureState(EnumTreasureState.MYTREASUREPRESENT);
                mapVisual[x][y] = 'T';
                break;
            }
        }
    }

    private static void printMap(char[][] mapVisual) {
        for (char[] row : mapVisual) {
            for (char field : row) {
                System.out.print(field + " ");
            }
            System.out.println();
        }
    }


    public ClientMap generateClientHalfMap(){
        ClientMap map = new ClientMap();
        map = generateMap();
        return map;
    }

}

