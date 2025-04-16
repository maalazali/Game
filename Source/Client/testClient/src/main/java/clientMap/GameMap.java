package clientMap;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public abstract class GameMap {
    private Map<String, MapNode> gameMap;
    private int maxX;
    private int maxY;

    public GameMap (Map<String, MapNode> gameMap) {
        this.gameMap = gameMap;
        this.maxX = gameMap.values().stream()
                .map(mapNode -> mapNode.getCoordinate().getX())
                .max(Comparator.naturalOrder())
                .orElse(0);

        this.maxY = gameMap.values().stream()
                .map(mapNode -> mapNode.getCoordinate().getY())
                .max(Comparator.naturalOrder())
                .orElse(0);
    }

    public GameMap(){

    }

    public Map<String, MapNode> getGameMap() {
        return gameMap;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public Optional<MapNode> getNode(int x, int y) {
        return gameMap.values().stream()
                .filter(a-> a.getX() == x && a.getY() == y)
                .findFirst();
    }

}
