/*package pathFinder;

import clientMap.ClientHalfMap;
import clientMap.Coordinate;
import clientMap.MapNode;
import converter.EnumPlayerPositionConverter;
import data.EnumFortState;
import data.EnumMoves;
import data.EnumPlayerPosition;
import data.EnumTreasureState;
import game.ClientGameState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.GameState;
import java.util.List;
import java.util.Map;
import converter.EnumPlayerPositionConverter;

public class FindObject {

    private PathFinder pathFinder;
    private EnumPlayerPosition playerPosition;
    public FindObject(PathFinder pathFinder){
        this.pathFinder = pathFinder;
    }

    public static String findTreasure(ClientHalfMap clientHalfMap){
        for(Map.Entry<String, MapNode> entry: clientHalfMap.getClientHalfMap().entrySet()){                         //sowie eine for each schleife, alle nodes durchgehen
            MapNode mapNode = entry.getValue();
            if (mapNode == null || mapNode.getEnumTreasureState() == null) {
                continue;
            }else if(mapNode.getEnumTreasureState() == EnumTreasureState.MYTREASUREPRESENT){
                return entry.getKey();
            }
        }
        return null;
    }

    public static String findEnemyFort(ClientHalfMap clientHalfMap){
        for(Map.Entry<String, MapNode> entry: clientHalfMap.getClientHalfMap().entrySet()){
            MapNode mapNode = entry.getValue();
            if (mapNode == null || mapNode.getEnumFortState() == null) {
                continue;
            }else if(mapNode.getEnumFortState() == EnumFortState.ENEMYFORTPRESENT){
                return entry.getKey();
            }
        }
        return null;
    }

    public List<MapNode> findPathToObject(ClientHalfMap clientHalfMap, ClientGameState clientGameState){
        String startKey = clientHalfMap.getStartKey();  //jetzige Position
        if(isTreasureFound(clientGameState)){
            System.out.println("Schatz gefunden");
            String enemyKey = findEnemyFort(clientHalfMap);
            return pathFinder.findShortestPath(clientHalfMap, startKey, enemyKey);
        }else{
            System.out.println("Auf der Suche nach dem Schatz");
            String treasureKey = findTreasure(clientHalfMap);
            return pathFinder.findShortestPath(clientHalfMap, startKey, treasureKey);
        }
    }

    private boolean isTreasureFound(ClientGameState clientGameState){
        return clientGameState.getPlayerStates().stream().anyMatch(player-> player.hasCollectedTreasure());
    }

    public EnumMoves getNextMove(ClientGameState clientGameState){
        ClientHalfMap clientHalfMap = clientGameState.getMap();
        EnumPlayerPosition playerPosition = clientGameState.getPlayerPosition();

        MapNode currentNode = clientHalfMap.getClientHalfMap().values().stream()
                .filter(node -> node.getEnumPlayerPosition() == EnumPlayerPosition.MYPLAYERPOSITION)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Spielerposition nicht gefunden!"));

        Coordinate currentCoord = currentNode.getCoordinate();

        List<MapNode> pathToObject = findPathToObject(clientHalfMap, clientGameState);
        if (pathToObject == null || pathToObject.isEmpty()) {
            throw new IllegalStateException("Kein gültiger Pfad zum Ziel gefunden!");
        }
        MapNode nextNode = pathToObject.get(1); // [0] ist die aktuelle Position, [1] der nächste Schritt
        Coordinate nextCoord = nextNode.getCoordinate();

        int dx = nextCoord.getX() - currentCoord.getX();
        int dy = nextCoord.getY() - currentCoord.getY();

        if (dx == 1 && dy == 0) {
            return EnumMoves.RIGHT; // Bewegung nach rechts
        } else if (dx == -1 && dy == 0) {
            return EnumMoves.LEFT; // Bewegung nach links
        } else if (dx == 0 && dy == 1) {
            return EnumMoves.DOWN; // Bewegung nach unten
        } else if (dx == 0 && dy == -1) {
            return EnumMoves.UP; // Bewegung nach oben
        }


        throw new IllegalStateException("Ungültige Bewegung! Die nächste Position ist nicht erreichbar.");

    }




}
*/