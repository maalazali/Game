package movment;

import clientmap.ClientMap;
import clientmap.Coordinate;
import clientmap.MapNode;
import data.EnumField;
import data.EnumFortState;
import data.EnumTreasureState;
import network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ExploreSystem {

    private int enemyMaxXBoundary;
    private int enemyMinXBoundary;
    private int enemyMaxYBoundary;
    private int enemyMinYBoundary;
    private int ownMaxXBoundary;
    private int ownMinXBoundary;
    private int ownMaxYBoundary;
    private int ownMinYBoundary;
    public ClientMap ownTerritoryMap;
    public ClientMap enemyTerritoryMap;

    private static final Logger logger = LoggerFactory.getLogger(ExploreSystem.class);


    public Set<Coordinate> unexploredGrass = new HashSet<>();
    private Map<Coordinate, Coordinate> nodeTrace = new HashMap<>();

    private Coordinate specialTarget;

    public void defineTerritories(ClientMap map, Coordinate playerPosition) {
        defineMapDivision(map, playerPosition);
    }
    private static final int[][] DIRECTIONS = {
            {0, 1},  {0, -1},
            {1, 0},  {-1, 0},
            {1, 1},  {-1, -1},
            {1, -1}, {-1, 1}
    };

    private void defineMapDivision(ClientMap map, Coordinate playerPosition) {
        boolean isVerticalMap = map.getMaxHeight() >= map.getMaxWidth();
        if (isVerticalMap) {
            if (playerPosition.getY() <= map.midYDivider) {
                ownMinYBoundary = map.mapMinY;
                ownMaxYBoundary = map.midYDivider;
                enemyMinYBoundary = map.midYDivider + 1;
                enemyMaxYBoundary = map.mapMaxY;
            } else {
                enemyMinYBoundary = map.mapMinY;
                enemyMaxYBoundary = map.midYDivider;
                ownMinYBoundary = map.midYDivider + 1;
                ownMaxYBoundary = map.mapMaxY;
            }
            ownMinXBoundary = map.mapMinX;
            ownMaxXBoundary = map.midXDivider;
            enemyMinXBoundary = map.mapMinX;
            enemyMaxXBoundary = map.midXDivider;
        } else {
            if (playerPosition.getX() <= map.midXDivider) {
                ownMinXBoundary = map.mapMinX;
                ownMaxXBoundary = map.midXDivider;
                enemyMinXBoundary = map.midXDivider + 1;
                enemyMaxXBoundary = map.mapMaxX;
            } else {
                enemyMinXBoundary = map.mapMinX;
                enemyMaxXBoundary = map.midXDivider;
                ownMinXBoundary = map.midXDivider + 1;
                ownMaxXBoundary = map.mapMaxX;
            }
            ownMinYBoundary = map.mapMinY;
            ownMaxYBoundary = map.midYDivider;
            enemyMinYBoundary = map.mapMinY;
            enemyMaxYBoundary = map.midYDivider;
        }
    }

    public boolean isInOwnTerritory(Coordinate position) {
        return position.getX() >= ownMinXBoundary && position.getX() <= ownMaxXBoundary &&
                position.getY() >= ownMinYBoundary && position.getY() <= ownMaxYBoundary;
    }

    public boolean isInEnemyTerritory(Coordinate position) {
        return position.getX() >= enemyMinXBoundary && position.getX() <= enemyMaxXBoundary &&
                position.getY() >= enemyMinYBoundary && position.getY() <= enemyMaxYBoundary;
    }
    private void createOwnTerritoryMap(ClientMap map) {
        Map<String, MapNode> ownTerritoryNodes = new HashMap<>();

        for (Map.Entry<String, MapNode> entry : map.getClientMap().entrySet()) {
            MapNode node = entry.getValue();
            int x = node.getCoordinate().getX();
            int y = node.getCoordinate().getY();
            if (x >= ownMinXBoundary && x <= ownMaxXBoundary &&
                    y >= ownMinYBoundary && y <= ownMaxYBoundary) {
                ownTerritoryNodes.put(entry.getKey(), node);
            }
        }
        ownTerritoryMap = new ClientMap(ownTerritoryNodes);
    }

    private void createEnemyTerritoryMap(ClientMap map) {
        Map<String, MapNode> enemyTerritoryNodes = new HashMap<>();

        for (Map.Entry<String, MapNode> entry : map.getClientMap().entrySet()) {
            MapNode node = entry.getValue();
            int x = node.getCoordinate().getX();
            int y = node.getCoordinate().getY();
            if (x >= enemyMinXBoundary && x <= enemyMaxXBoundary &&
                    y >= enemyMinYBoundary && y <= enemyMaxYBoundary) {
                enemyTerritoryNodes.put(entry.getKey(), node);
            }
        }
        enemyTerritoryMap = new ClientMap(enemyTerritoryNodes);
    }

    public Coordinate determineNextGoal(ClientMap map, Coordinate currentPosition, Boolean treasureCollected) {
        unexploredGrass.remove(currentPosition);
        System.out.println("Current Position:");
        System.out.println(currentPosition);

        // Prüfen, ob der Spieler sich auf einem Bergfeld befindet
        MapNode currentNode = map.getClientMap().get(currentPosition.getX() + "" + currentPosition.getY());
        if (currentNode != null && currentNode.getEnumField() == EnumField.MOUNTAIN) {
            if (treasureCollected) {
                specialTarget = findFort(map, currentPosition);
            }
            else {
                if(specialTarget == null){
                    specialTarget = findTreasure(map, currentPosition);
                    System.out.println("Special Target 1:");
                    System.out.println(specialTarget);
                }
            }

            if (specialTarget != null) {
                // Falls das Ziel gefunden wurde, berechne den kürzesten Weg dorthin
                List<Coordinate> path = findShortestPath(map, currentPosition, specialTarget);

                // Bewege dich auf das nächste Feld in Richtung des Ziels
                logger.info("Moving towards special target: {}", path.get(1));
                System.out.println("Special Target Path:");
                System.out.println(path.get(1));
                return path.get(1);
            }
        }

        if(unexploredGrass.isEmpty()){
            //unexploredGrass =  getNodesOfType(halfMap, EnumField.GRAS).stream().map(MapNode::getCoordinate).collect(Collectors.toSet());
            int minXBoundary = treasureCollected ? enemyMinXBoundary : ownMinXBoundary;
            int maxXBoundary = treasureCollected ? enemyMaxXBoundary : ownMaxXBoundary;
            int minYBoundary = treasureCollected ? enemyMinYBoundary : ownMinYBoundary;
            int maxYBoundary = treasureCollected ? enemyMaxYBoundary : ownMaxYBoundary;

            for (MapNode node : map.getClientMap().values()) {
                Coordinate coord = node.getCoordinate();
                if (node.getEnumField() == EnumField.GRAS &&
                        coord.getX() >= minXBoundary && coord.getX() <= maxXBoundary &&
                        coord.getY() >= minYBoundary && coord.getY() <= maxYBoundary) {
                    unexploredGrass.add(coord);
                }
            }
        }

        if (currentNode != null && currentNode.getEnumField() == EnumField.MOUNTAIN) {
            removeSurroundingGrass(map, currentPosition);
        }

        // Nachbarn durchlaufen, um nahegelegene Grasfelder zu finden
        List<Coordinate> neighbors = getNeighbors(currentPosition, map);
        for (Coordinate neighbor : neighbors) {
            if (unexploredGrass.contains(neighbor)) {
                logger.info("Selected next goal (direct neighbor): {}", neighbor);

                System.out.println("Best Neighbor:");
                System.out.println(neighbor);
                return neighbor;
            }
        }

        // Alle Distanzen zu den Zielen (Gras- und Bergfelder) berechnen
        Map<Coordinate, Integer> distances = calculateDistancesFromStart(map, currentPosition);

        // 1. Bergfelder mit signifikanter Anzahl unerforschter Nachbarn priorisieren
        Set<Coordinate> mountainTargets = findMountainsWithSignificantUnexploredNeighbors(map);
        if (!mountainTargets.isEmpty()) {
            Coordinate target = findClosestTargetByDistance(mountainTargets, distances);
            if (target != null) {
                List<Coordinate> path = findShortestPath(map, currentPosition, target);
                if (!path.isEmpty()) {
                    logger.info("Selected next goal based on mountain targets: {}", target);
                    return path.get(1);
                }
            }
        }

        // 2. Nächste unerforschte Grasfelder wählen
        if (!unexploredGrass.isEmpty()) {
            Coordinate target = findClosestTargetByDistance(unexploredGrass, distances);
            if (target != null) {
                List<Coordinate> path = findShortestPath(map, currentPosition, target);
                if (!path.isEmpty()) {
                    logger.info("Selected next goal based on closest unexplored grass fields: {}", target);
                    return path.get(1);
                }
            }
        }



        // Falls kein valider Zug möglich ist, werfe eine Ausnahme
        throw new IllegalStateException("Kein valider Zug gefunden! Spieler ist blockiert.");
    }

    private void removeSurroundingGrass(ClientMap map, Coordinate playerPos) {
        // Überprüfe alle Nachbarfelder des Spielers
        for (int[] dir : DIRECTIONS) {
            int newX = playerPos.getX() + dir[0];
            int newY = playerPos.getY() + dir[1];
            Coordinate neighborCoord = new Coordinate(newX, newY);

            // Überprüfen, ob das Nachbarfeld in unexploredGrass ist
            if (unexploredGrass.contains(neighborCoord)) {
                unexploredGrass.remove(neighborCoord);
                logger.info("Removed grass field from unexploredGrass at {}", neighborCoord);
            }
        }
    }

    private List<Coordinate> getNeighbors(Coordinate currentPosition, ClientMap map) {
        List<Coordinate> neighbors = new ArrayList<>();
        int[][] directions = {
                {0, 1},  // oben
                {1, 0},  // rechts
                {0, -1}, // unten
                {-1, 0}  // links
        };

        for (int[] dir : directions) {
            int newX = currentPosition.getX() + dir[0];
            int newY = currentPosition.getY() + dir[1];

            // Überprüfung, ob sich der Nachbar innerhalb der Kartenbegrenzungen befindet
            if (newX >= map.mapMinX && newX <= map.mapMaxX &&
                    newY >= map.mapMinY && newY <= map.mapMaxY) {

                String key = newX + "" + newY;
                Coordinate neighbor = new Coordinate(newX, newY);
                MapNode node = map.getClientMap().get(key);

                if (node != null && node.getEnumField() != EnumField.WATER) {
                    neighbors.add(neighbor);
                }
            }
        }

        return neighbors;
    }
    private double movementCost(Coordinate from, Coordinate to, ClientMap map) {
        MapNode toNode = map.getClientMap().get(to.getX() + "" + to.getY());

        if (toNode == null || toNode.getEnumField() == data.EnumField.WATER) {
            return Double.MAX_VALUE; // Wasser ist unpassierbar
        }

        return switch (toNode.getEnumField()) {
            case GRAS -> 1;
            case MOUNTAIN -> 2; // Höhere Kosten für Berge
            default -> Double.MAX_VALUE;
        };
    }

    public List<Coordinate> findShortestPath(ClientMap map, Coordinate start, Coordinate target) {
        Map<Coordinate, Integer> distances = calculateDistancesFromStart(map, start);

        if (!distances.containsKey(target) || distances.get(target) == Integer.MAX_VALUE) {
            return Collections.emptyList();  // Kein Pfad vorhanden
        }

        List<Coordinate> path = new ArrayList<>();
        Coordinate current = target;

        while (current != null) {
            path.add(current);
            current = nodeTrace.get(current);
        }

        Collections.reverse(path);
        return path;
    }

    private Coordinate findClosestTargetByDistance(Set<Coordinate> targets, Map<Coordinate, Integer> distances) {
        return targets.stream()
                .filter(distances::containsKey) // Nur erreichbare Ziele berücksichtigen
                .min(Comparator.comparingInt(distances::get)) // Ziel mit minimaler Distanz finden
                .orElse(null);
    }

    private Set<Coordinate> findMountainsWithSignificantUnexploredNeighbors(ClientMap map) {
        return map.getClientMap().values().stream()
                .filter(node -> node.getEnumField() == EnumField.MOUNTAIN &&
                        getNeighbors(node.getCoordinate(), map).stream()
                                .filter(unexploredGrass::contains)
                                .count() >= 3) // Mindestanzahl unerforschter Nachbarn
                .map(MapNode::getCoordinate)
                .collect(Collectors.toSet());
    }

    private Map<Coordinate, Integer> calculateDistancesFromStart(ClientMap map, Coordinate startNode) {
        Set<Coordinate> openSet = new HashSet<>(getWalkableNodes(map));
        Map<Coordinate, Integer> distances = openSet.stream()
                .collect(Collectors.toMap(node -> node, node -> Integer.MAX_VALUE));
        distances.put(startNode, 0);
        nodeTrace.clear();

        while (!openSet.isEmpty()) {
            Coordinate current = openSet.stream()
                    .min(Comparator.comparingInt(distances::get))
                    .orElseThrow();

            openSet.remove(current);
            List<Coordinate> neighbors = getNeighbors(current, map);

            for (Coordinate neighbor : neighbors) {
                int tentativeDistance = distances.get(current) + 1;
                if (tentativeDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, tentativeDistance);
                    nodeTrace.put(neighbor, current);
                }
            }
        }

        return distances;
    }


    private Set<Coordinate> getWalkableNodes(ClientMap map) {
        return map.getClientMap().values().stream()
                .filter(node -> node.getEnumField() == EnumField.GRAS || node.getEnumField() == EnumField.MOUNTAIN )
                .map(MapNode::getCoordinate)
                .collect(Collectors.toSet());
    }

    private Set<MapNode> getNodesOfType(ClientMap map, EnumField fieldType) {
        return map.getClientMap().values().stream()
                .filter(node -> node.getEnumField() == fieldType)
                .collect(Collectors.toSet());
    }

    private Coordinate findTreasure(ClientMap map, Coordinate playerPos) {
        // Durchsuchen aller Felder der Karte nach dem Schatz
        Optional<Map.Entry<String, MapNode>> treasureNode = map.getClientMap().entrySet().stream()
                .filter(entry -> entry.getValue().getEnumTreasureState() == EnumTreasureState.MYTREASUREPRESENT)
                .findFirst();

        // Wenn ein Schatz gefunden wurde, die Koordinate zurückgeben
        return treasureNode.map(stringMapNodeEntry -> stringMapNodeEntry.getValue().getCoordinate()).orElse(null);

    }

   private Coordinate findFort(ClientMap map, Coordinate playerPos) {
       // Durchsuchen aller Felder der Karte nach einer feindlichen Burg
       Optional<Map.Entry<String, MapNode>> fortNode = map.getClientMap().entrySet().stream()
               .filter(entry -> entry.getValue().getEnumFortState() == EnumFortState.ENEMYFORTPRESENT)
               .findFirst();

       // Wenn eine feindliche Burg gefunden wurde, die Koordinate zurückgeben

       return fortNode.map(stringMapNodeEntry -> stringMapNodeEntry.getValue().getCoordinate()).orElse(null);
   }


}
