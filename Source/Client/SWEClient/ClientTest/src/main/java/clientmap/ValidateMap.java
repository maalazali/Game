package clientmap;

import data.EnumField;
import data.EnumFortState;
import data.EnumPlayerPosition;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ValidateMap {


    private static final int WIDTH = 10;
    private static final int HEIGHT = 5;
    private static final int MIN_GRAS = 24;
    private static final int MIN_MOUNTAIN = 5;
    private static final int MIN_WATER = 7;

    static class pair
    {
        int first, second;

        public pair(int first, int second)
        {
            this.first = first;
            this.second = second;
        }
    }

    boolean [][]vis = new boolean[WIDTH][HEIGHT];

    // Direction vectors
    static int dRow[] = { -1, 0, 1, 0 };
    static int dCol[] = { 0, 1, 0, -1 };


    static boolean isValid(boolean vis[][], int row, int col)
    {
        // If cell lies out of bounds
        if (row < 0 || col < 0 ||
                row >= WIDTH || col >= HEIGHT)
            return false;

        // If cell is already visited
        if (vis[row][col]) {
            return false;
        }
        // Otherwise
        return true;
    }


    public boolean validateHalfMap(ClientMap clientHalfMap){
        boolean correctDimension = checkDimension(clientHalfMap);
        boolean hasNoIsland = checkIsland(clientHalfMap);
        boolean hasCorrectFieldsCount = checkFieldsCount(clientHalfMap);
        boolean borderIsOk = checkBorder(clientHalfMap);
        boolean hasFortOnGRas = isFortOnGras(clientHalfMap);
        boolean hasFortOnGrass = isFortOnGras(clientHalfMap);
        return correctDimension && hasNoIsland && hasCorrectFieldsCount && borderIsOk && hasFortOnGRas;
    }

    private boolean checkDimension(ClientMap clientHalfMap) {

        int maxX = 0;
        int maxY = 0;

        for(MapNode mapNode: clientHalfMap.getClientMap().values()){
            Coordinate coordinate = mapNode.getCoordinate();
            if(coordinate.getX() > maxX){
                maxX = coordinate.getX();
            }
            if(coordinate.getY() > maxY){
                maxY = coordinate.getY();
            }
        }

        if(maxX +1  == WIDTH && maxY+1 == HEIGHT){
            System.out.println(" maxX:"+maxX+"maxY:"+maxY );
            return true;
        }
        return false;
    }

    private boolean checkFieldsCount(ClientMap clientHalfMap) { //validate Methode machen und Interface benutzen mit Methode validate: Abstraktion

        if(clientHalfMap.getGrassCount() < MIN_GRAS){
            return false;
        }
        if(clientHalfMap.getMountainCount() < MIN_MOUNTAIN){
            return false;
        }
        return clientHalfMap.getWaterCount() >= MIN_WATER;
    }

    private boolean isFortOnGras(ClientMap clientHalfMap){
        for(Map.Entry<String, MapNode> entry: clientHalfMap.getClientMap().entrySet()){
            MapNode mapNode = entry.getValue();
            if(mapNode.getEnumField() == EnumField.GRAS && mapNode.getEnumFortState() == EnumFortState.MYFORTPRESENT){
                System.out.println("Fortress is on grass");
                return true;
            }
        }
        System.out.println("Fortress not on Grass");
        return false;
    }

    public boolean checkEdgeField(int x, int y, ClientMap clientHalfMap) { // ein Randstück
        String key = String.valueOf(x) + y;
        MapNode node = clientHalfMap.getClientMap().get(key);
        return node != null && node.getEnumField() != EnumField.WATER;
    }

    private boolean checkBorder(ClientMap clientHalfMap) {
        int notWaterCount = 0;
        int totalCount = 2 * HEIGHT + 2 * (WIDTH - 2); // Anzahl der Felder entlang aller Ränder

        // Obere und untere Ränder überprüfen
        for (int i = 0; i < HEIGHT; i++) {
            if (checkEdgeField(0, i, clientHalfMap)) {
                notWaterCount++;
            }
            if (checkEdgeField(WIDTH - 1, i, clientHalfMap)) {
                notWaterCount++;
            }
        }

        // Linke und rechte Ränder überprüfen (ohne doppelte Zählung der Ecken)
        for (int j = 1; j < WIDTH - 1; j++) {
            if (checkEdgeField(j, 0, clientHalfMap)) {
                notWaterCount++;
            }
            if (checkEdgeField(j, HEIGHT - 1, clientHalfMap)) {
                notWaterCount++;
            }
        }

        System.out.println("Border not WaterCount: " + notWaterCount); // Debugging
        return notWaterCount >= Math.ceil(totalCount * 0.51);
    }


    private boolean checkIsland(ClientMap clientHalfMap){
        Coordinate start = null;
        Map<String, MapNode> map = clientHalfMap.getClientMap();

        for (int i = 0; i < WIDTH; i++) {                  //NEW
            Arrays.fill(vis[i], false);
        }

        String startSearch = "99";
        for (int i = 0; i < WIDTH; i++){
            for (int j = 0; j < HEIGHT; j++) {
                String key = String.valueOf(i) + j;
                MapNode currentNode = map.get(key);
                if (currentNode != null && currentNode.getEnumField() != EnumField.WATER){              //IF NOT NULL: NEW
                    startSearch = key;
                    break;
                }
            }
            if (!startSearch.equals("99")) {
                System.out.println("Start Coordinates: " + startSearch);
                break;
            }
        }

        // BSF
        int nodeCount = 0;
        // Stores indices of the matrix cells
        Queue<pair> q = new LinkedList<>();

        // Mark the starting cell as visited
        // and push it into the queue
        int row = Character.getNumericValue(startSearch.charAt(0));
        int col = Character.getNumericValue(startSearch.charAt(1));
        q.add(new pair(row, col));
        vis[row][col] = true;
        nodeCount++;

        while (!q.isEmpty())
        {
            pair cell = q.peek();
            int x = cell.first;
            int y = cell.second;
            q.remove();
            // Go to the adjacent cells
            for(int i = 0; i < 4; i++)
            {
                int adjx = x + dRow[i];
                int adjy = y + dCol[i];
                String newKey = String.valueOf(adjx) + adjy;
                MapNode node = map.get(newKey);

                if (isValid(vis, adjx, adjy) && node != null && node.getEnumField() != EnumField.WATER)         //NODE != NOT NULL: NEW
                {
                    q.add(new pair(adjx, adjy));
                    vis[adjx][adjy] = true;
                    nodeCount++;
                    //System.out.println("Visiting: (" + adjx + "," + adjy + "), Non-Water NodeCount: " + nodeCount);
                }
            }
        }
        //return !(nodeCount < (clientHalfMap.getGrassCount() + clientHalfMap.getMountainCount()));
        if (nodeCount < (clientHalfMap.getGrassCount() + clientHalfMap.getMountainCount())){
            System.out.println("MAP INVALID");
            return false;
        }
        System.out.println("MAP VALID");
        return true;

    }

    private boolean isPlayerOnFort(ClientMap clientHalfMap) {
        for (Map.Entry<String, MapNode> entry : clientHalfMap.getClientMap().entrySet()) {
            MapNode mapNode = entry.getValue();
            if (mapNode.getEnumFortState() == EnumFortState.MYFORTPRESENT
                    && mapNode.getEnumPlayerPosition() == EnumPlayerPosition.MYPLAYERPOSITION) {
                System.out.println("Player is correctly positioned on their fort.");
                return true;
            }
        }
        System.out.println("Player is NOT correctly positioned on their fort.");
        return false;
    }


}
