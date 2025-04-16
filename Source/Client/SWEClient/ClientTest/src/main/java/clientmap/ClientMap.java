package clientmap;

import data.EnumField;
import data.EnumFortState;
import data.EnumPlayerPosition;
import data.EnumTreasureState;
import network.Network;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class ClientMap {

    public final int mapMaxX = 19;
    public final int mapMinX = 0;
    public final int mapMaxY = 9;
    public final int mapMinY = 0;
    public final int midXDivider = 9;
    public final int midYDivider = 4;
    private Coordinate cachedPlayerPosition = null;
    private Map<String, MapNode> clientMap;
    private Collection<MapNode> mapNode;

    public ClientMap(Map<String, MapNode> clientMap){
        this.clientMap = clientMap;
    }
    public ClientMap(){

    }

    public Map<String, MapNode> getClientMap() {
        return clientMap;
    }

    public Collection<MapNode> getMapNode() {
        return mapNode;
    }

    public int getGrassCount(){
        return (int) clientMap.entrySet().stream()
                .filter(field  -> field.getValue().getEnumField().equals(EnumField.GRAS))
                .count();
    }
    public int getMountainCount(){
        return (int) clientMap.entrySet().stream()
                .filter(field  -> field.getValue().getEnumField().equals(EnumField.MOUNTAIN))
                .count();
    }
    public int getWaterCount(){
        return (int) clientMap.entrySet().stream()
                .filter(field  -> field.getValue().getEnumField().equals(EnumField.WATER))
                .count();
    }

    public int getMaxWidth() {
        int maxWidth = 0;
        for (MapNode node : clientMap.values()) {
            maxWidth = Math.max(maxWidth, node.getCoordinate().getX());
        }
        return maxWidth;
    }

    public int getMaxHeight() {
        int maxHeight = 0;
        for (MapNode node : clientMap.values()) {
            maxHeight = Math.max(maxHeight, node.getCoordinate().getY());
        }
        return maxHeight;
    }

    public void updatePlayerPosition(ClientMap map) {
        this.cachedPlayerPosition = map.getClientMap().values().stream()
                .filter(node -> node.getEnumPlayerPosition() == EnumPlayerPosition.MYPLAYERPOSITION || node.getEnumPlayerPosition() == EnumPlayerPosition.BOTHPLAYERPOSITION)
                .findFirst()
                .map(MapNode::getCoordinate)
                .orElse(null);
    }

    public Coordinate getPlayerPosition() {
        return cachedPlayerPosition;
    }

}
