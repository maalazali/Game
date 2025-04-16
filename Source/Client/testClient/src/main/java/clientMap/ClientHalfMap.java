package clientMap;

import data.EnumField;
import data.EnumFortState;
import data.EnumPlayerPosition;
import data.EnumTreasureState;

import java.util.*;

public class ClientHalfMap extends GameMap{
    private Map<String, MapNode> clientHalfMap;
    private String startKey;
    private Collection<MapNode> mapNode;


    public ClientHalfMap(Map<String, MapNode> clientHalfMap){
        super(clientHalfMap);
        this.clientHalfMap = clientHalfMap;
    }
    public ClientHalfMap() {
        this.clientHalfMap = new HashMap<>();
    }
    public Map<String, MapNode> getClientHalfMap() {
        return clientHalfMap;
    }

    public int getGrassCount(){
        return (int) clientHalfMap.entrySet().stream()
                .filter(field  -> field.getValue().getEnumField().equals(EnumField.GRAS))
                .count();
    }
    public int getMountainCount(){
        return (int) clientHalfMap.entrySet().stream()
                .filter(field  -> field.getValue().getEnumField().equals(EnumField.MOUNTAIN))
                .count();
    }
    public int getWaterCount(){
        return (int) clientHalfMap.entrySet().stream()
                .filter(field  -> field.getValue().getEnumField().equals(EnumField.WATER))
                .count();
    }
    public String getStartKey() {
        return startKey;
    }
    public Collection<MapNode> getMapNode() {
        return mapNode;
    }

    public Optional <Coordinate> getMyTreasurePlace(){
        return getClientHalfMap().entrySet().stream()
                .filter(entry -> entry.getValue().getEnumTreasureState().equals(EnumTreasureState.MYTREASUREPRESENT))
                .map(entry -> entry.getValue().getCoordinate())
                .findFirst();
    }

    public Optional <Coordinate> getEnemyFortPlace(){
        return getClientHalfMap().entrySet().stream()
                .filter(entry -> entry.getValue().getEnumFortState().equals(EnumFortState.ENEMYFORTPRESENT))
                .map(entry -> entry.getValue().getCoordinate())
                .findFirst();
    }

    /*public Coordinate getMyPlayerPosition(){
        return getClientHalfMap().entrySet().stream()
                .filter(entry -> entry.getValue().getEnumPlayerPosition().equals(EnumPlayerPosition.MYPLAYERPOSITION)
                || entry.getValue().getEnumPlayerPosition().equals(EnumPlayerPosition.BOTHPLAYERPOSITION) )
                .map(entry -> entry.getValue().getCoordinate())
                .findFirst().orElseThrow(()-> new IllegalStateException("No Coordinates found for player"));
    }*/

    public Coordinate getMyPlayerPosition() {
        return getGameMap().entrySet().stream()
                .filter(entry -> entry.getValue().getEnumPlayerPosition().equals(EnumPlayerPosition.MYPLAYERPOSITION)
                        || entry.getValue().getEnumPlayerPosition().equals(EnumPlayerPosition.BOTHPLAYERPOSITION))
                .map(entry -> entry.getValue().getCoordinate())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Coordinates found for player. Current map state: "
                        + getGameMap().toString()));
    }
    public List<Coordinate> getVisitableNeighbors(int x, int y) {
        List<Coordinate> ret = getDirectNeighbors(x, y);
        ret.removeIf(p -> getNode(p.getX(), p.getY()).orElseThrow().getEnumField().equals(EnumField.WATER));
        return ret;
    }
    public List<Coordinate> getSeeableNeighbors(int x, int y) {
        List<Coordinate> ret = getAllNeighbors(x, y);
        ret.removeIf(p -> getNode(p.getX(), p.getY()).orElseThrow().getEnumField().equals(EnumField.WATER)
                || getNode(p.getX(), p.getY()).orElseThrow().getEnumField().equals(EnumField.MOUNTAIN)
                || getNode(p.getX(), p.getY()).orElseThrow().getEnumFortState()
                .equals(EnumFortState.MYFORTPRESENT));
        return ret;
    }
    //!!!!!!!!!!
    private List<Coordinate> getAllNeighbors(int x, int y) {
        List<Coordinate> ret = getDirectNeighbors(x, y);

        Optional<MapNode> temp;
        temp = getNode(x + 1, y + 1);
        if (temp.isPresent())
            ret.add(Coordinate.of(x + 1, y + 1));

        temp = getNode(x + 1, y - 1);
        if (temp.isPresent())
            ret.add(Coordinate.of(x + 1, y - 1));

        temp = getNode(x - 1, y - 1);
        if (temp.isPresent())
            ret.add(Coordinate.of(x - 1, y - 1));

        temp = getNode(x - 1, y + 1);
        if (temp.isPresent())
            ret.add(Coordinate.of(x - 1, y + 1));

        return ret;
    }
//!!!!!!!!!!
    private List<Coordinate> getDirectNeighbors(int x, int y) {
        List<Coordinate> ret = new ArrayList<Coordinate>();
        Optional<MapNode> temp = getNode(x, y + 1);
        if (temp.isPresent())
            ret.add(Coordinate.of(x, y + 1));

        temp = getNode(x - 1, y);
        if (temp.isPresent())
            ret.add(Coordinate.of(x - 1, y));

        temp = getNode(x, y - 1);
        if (temp.isPresent())
            ret.add(Coordinate.of(x, y - 1));

        temp = getNode(x + 1, y);
        if (temp.isPresent())
            ret.add(Coordinate.of(x + 1, y));
        return ret;
    }


}
