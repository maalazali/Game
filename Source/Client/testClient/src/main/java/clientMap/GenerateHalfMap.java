package clientMap;
import data.EnumField;
import data.EnumFortState;
import data.EnumPlayerPosition;
import data.EnumTreasureState;
import player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class GenerateHalfMap {
    private ValidateMap mapValidator = new ValidateMap();
    public static ClientHalfMap generateMap() {
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


        return new ClientHalfMap(map);
    }


    public ClientHalfMap generateClientHalfMap(){
//        boolean validated = false;
        ClientHalfMap map = new ClientHalfMap();
        map = generateMap();
//        while(!validated) {
//            map = generateMap();
//            validated = mapValidator.validateHalfMap(map);
//        }


        return map;
    }


//max 2 WasserFelder in der langen Sete und max 1 in der krizen Seite



}

