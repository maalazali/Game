package pathFinder;

import clientMap.ClientHalfMap;
import clientMap.MapNode;
import data.EnumField;
import data.EnumMoves;
import data.EnumPlayerPosition;
import game.ClientGameState;
import messagesbase.messagesfromserver.FullMapNode;
import network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import clientMap.Coordinate;

import java.util.*;
import java.util.function.Predicate;

public class FindPath {

    private static Logger logger = LoggerFactory.getLogger(FindPath.class);

    //Konstante Karten Größe für HalfMap und FullMap

    private final int MAX_HEIGHT = 19;
    private final int HALF_HEIGHT = 9;
    private final int MIN_X_VALUE = 0;

    private final int MAX_WIDTH = 9;
    private final int HALF_WIDTH = 4;
    private final int MIN_Y_VALUE = 0;
    private MapNode mapNode;

    //Borders/Grenzen der Karte
    private Network network;
    private EnumPlayerPosition playerPosition;
    private EnumMoves moves;
    private EnumField fields;
    private ClientHalfMap clientMap;
    private int xUpperBorder;
    private int xLowerBorder;
    private int yUpperBorder;
    private int yLowerBorder;


    public Map<Coordinate, Coordinate> findPathToTarget(ClientGameState gameState, List<Coordinate> visitedPoints) {

        clientMap = new ClientHalfMap();
        gameState.setGameMap(clientMap);
        if (clientMap == null) {
            throw new IllegalStateException("Map is not initialized in ClientGameState");
        }

        if(!gameState.getPlayer().orElseThrow().hasCollectedTreasure()){
            searchOnHalfMap(clientMap, visitedPoints);
        }else{
            searchOnFullMap(clientMap, visitedPoints);
        }
        logger.debug("Set Borders: "+xLowerBorder+", "+xUpperBorder+", "+yLowerBorder+", "+ yUpperBorder);

        if(!gameState.getPlayer().orElseThrow().hasCollectedTreasure()){
           Optional <Coordinate> treasure = clientMap.getMyTreasurePlace();
           if(treasure.isPresent()){
               Predicate<Coordinate> treasureChecker = p -> p.getX() == treasure.orElseThrow().getX()
                       && p.getY() == treasure.orElseThrow().getY();
               return getPathToUnvisitedNodes(clientMap, visitedPoints, treasureChecker);
           }
        }else{
            Optional<Coordinate> enemyFort = clientMap.getEnemyFortPlace();
            if (enemyFort.isPresent()) {
                Predicate<Coordinate> enemyFortChecker = p -> p.getX() == enemyFort.orElseThrow().getX()
                        && p.getY() == enemyFort.orElseThrow().getY();
                return getPathToUnvisitedNodes(clientMap, visitedPoints, enemyFortChecker);
            }
        }
        Predicate<Coordinate> mountainChecker = p -> clientMap.getNode(p.getX(), p.getY()).orElseThrow().getEnumField()
                .equals(EnumField.MOUNTAIN);
        Map<Coordinate, Coordinate> ret = getPathToUnvisitedNodes(clientMap, visitedPoints, mountainChecker);

        if (ret.size() > 1)
            return ret;

        Predicate<Coordinate> grassChecker = p -> clientMap.getNode(p.getX(), p.getY()).orElseThrow().getEnumField()
                .equals(EnumField.GRAS);
        return getPathToUnvisitedNodes(clientMap, visitedPoints, grassChecker);

    }

    //Teilt die Karte in zwei Hälften
    private void searchOnHalfMap(ClientHalfMap gameMap, List<Coordinate> visitedPoints, boolean isEnemyHalfMap) {
        //eine Kartenhälfte: 10X5; wenn wir Schatz suchen müssen wir auf unsere Kartenhälfte schauen otherwise auf die Kartenhälfte vom Gegner, je nach dem wie die Karte kombiniert ist, müssen wir die Karte richtig aufteile, um unsere Kartenhälfte zu finden
        //Karten-Kombination : 5X20 dann wird die Karte rechts und links aufgeteilt
        //Karten - Kombination : 10X10 dann wird die Karten oben und unten aufgeteilt
        if (gameMap.getMaxX() == MAX_HEIGHT) {
            //wenn getX größer gleich 10 ist, heisst es, dass die beiden Karten horizontal gelegt worden sind
            //verstehe aber nicht wieso man anhand get(0) fest macht...
            if (isEnemyHalfMap == (visitedPoints.get(0).getX() >= HALF_HEIGHT + 1)) {
                xUpperBorder = HALF_HEIGHT;
                xLowerBorder = MIN_X_VALUE;
            } else {
                xUpperBorder = MAX_HEIGHT;
                xLowerBorder = HALF_HEIGHT + 1;
            }
            yUpperBorder = HALF_WIDTH;
            yLowerBorder = MIN_Y_VALUE;
        } else {
            if (isEnemyHalfMap == visitedPoints.get(0).getY() >= HALF_WIDTH + 1) {
                yUpperBorder = MAX_WIDTH;
                yLowerBorder = HALF_WIDTH + 1;
            } else {
                yUpperBorder = MAX_HEIGHT;
                yLowerBorder = HALF_WIDTH;
            }

            xUpperBorder = HALF_HEIGHT;
            xLowerBorder = MIN_X_VALUE;
        }
    }

    private void searchOnFullMap(ClientHalfMap map, List<Coordinate> visitedPoints) {
        if (map == null) {
            throw new IllegalArgumentException("Map cannot be null");
        }

        searchOnHalfMap(map, visitedPoints, true);
    }

    private void searchOnHalfMap(ClientHalfMap map, List<Coordinate> visitedPoints) {
        if (map == null) {
            throw new IllegalArgumentException("Map cannot be null");
        }

        searchOnHalfMap(map, visitedPoints, false);
    }

    private Map<Coordinate, Coordinate> getPathToUnvisitedNodes(ClientHalfMap clientHalfMap, List<Coordinate> visitedPoints, Predicate<Coordinate> needMapNodes) {
        Coordinate myPlayerPosition = clientHalfMap.getMyPlayerPosition(); // Startpunkt
        Coordinate goal = myPlayerPosition;

        Map<Coordinate, Coordinate> previousNodes = new HashMap<>(); // Verfolgt, von welchem Punkt aus der aktuelle Punkt erreicht wurde
        Map<Coordinate, Integer> currentCosts = new HashMap<>(); // Speichert die minimalen Kosten, um einen Punkt zu erreichen
        PriorityQueue<Coordinate> unsettledNodes = new PriorityQueue<>(Comparator.comparingInt(k -> currentCosts.getOrDefault(k, Integer.MAX_VALUE))); // Priorisiert Punkte mit den geringsten Kosten

        currentCosts.put(myPlayerPosition, 0);
        unsettledNodes.add(myPlayerPosition);

        while (!unsettledNodes.isEmpty()) {
            Coordinate current = unsettledNodes.poll(); // Entfernt den Punkt mit den niedrigsten Kosten

            // Prüfen, ob der aktuelle Punkt unser Ziel ist
            if (needMapNodes.test(current) && !visitedPoints.contains(current)
                    && !outsideOfBorder(current.getX(), current.getY())
                    && checkIfOptimal(current, clientHalfMap, visitedPoints)) {
                goal = current;
                break;
            }

            // Nachbarn des aktuellen Punkts abrufen
            List<Coordinate> neighbors = clientHalfMap.getVisitableNeighbors(current.getX(), current.getY());
            for (Coordinate next : neighbors) {
                MapNode currentNode = clientHalfMap.getNode(current.getX(), current.getY()).orElseThrow();
                MapNode nextNode = clientHalfMap.getNode(next.getX(), next.getY()).orElseThrow();

                // Bewegungskosten zwischen aktuellem und nächstem Feld berechnen
                int moveCost = 0;
                if (currentNode.getEnumField().equals(EnumField.GRAS) && nextNode.getEnumField().equals(EnumField.GRAS)) {
                    moveCost = 2; // Gras zu Gras
                } else if ((currentNode.getEnumField().equals(EnumField.GRAS) && nextNode.getEnumField().equals(EnumField.MOUNTAIN)) ||
                        (currentNode.getEnumField().equals(EnumField.MOUNTAIN) && nextNode.getEnumField().equals(EnumField.GRAS))) {
                    moveCost = 3; // Gras zu Berg oder Berg zu Gras
                } else if (currentNode.getEnumField().equals(EnumField.MOUNTAIN) && nextNode.getEnumField().equals(EnumField.MOUNTAIN)) {
                    moveCost = 4; // Berg zu Berg
                } else {
                    continue; // Wasser ignorieren
                }

                // Neue Kosten berechnen
                int newCost = currentCosts.get(current) + moveCost;

                // Falls die neuen Kosten geringer sind, aktualisiere die Kosten und setze den Vorgänger
                if (!currentCosts.containsKey(next) || newCost < currentCosts.get(next)) {
                    currentCosts.put(next, newCost);
                    unsettledNodes.add(next);
                    previousNodes.put(next, current);
                }
            }
        }

        // Rückwärtspfad von Zielpunkt zu Startpunkt erstellen
        Map<Coordinate, Coordinate> ret = new HashMap<>();
        Coordinate currentPoint = goal;
        while (!previousNodes.get(currentPoint).equals(currentPoint)) {
            Coordinate nextPoint = previousNodes.get(currentPoint);
            ret.put(currentPoint, nextPoint);
            currentPoint = nextPoint;
        }
        ret.put(currentPoint, currentPoint);
        return ret;
    }

    /*private Map<Coordinate, Coordinate> getPathToUnvisitedNodes(ClientHalfMap clientHalfMap, List<Coordinate> visitedPoints, Predicate<Coordinate> needMapNodes){
        Coordinate myPlayerPosition = clientHalfMap.getMyPlayerPosition();                                                                                          //startpunkt
        Coordinate goal = myPlayerPosition;
        Map<Coordinate, Coordinate> previousNodes = new HashMap<>();                                                                                                //Verfolgt, von welchem Punkt aus der aktuelle Punkt erreicht wurde
        Map<Coordinate, Integer> currentCosts = new HashMap<>();                                                                                                    //Speichert die minimalen Kosten, um einen Punkt zu erreichen.
        PriorityQueue<Coordinate> unsettledNodes = new PriorityQueue<>(Comparator.comparingInt(k -> currentCosts.getOrDefault(k, Integer.MAX_VALUE)));              //basierend auf die Kosten, werden die nodes gespeichert, nodes mit den niedrigsten Kosten haben vorrang
        unsettledNodes.add(myPlayerPosition);

        while(!unsettledNodes.isEmpty()){
            Coordinate current = unsettledNodes.poll();                                    //entfernt Node mit den niedrigsten Kosten & bereits besucht

            if(needMapNodes.test(current)&&!visitedPoints.contains(current)
                    &&!outsideOfBorder(current.getX(), current.getY())
                    && checkIfOptimal(current, clientHalfMap, visitedPoints )){
                goal = current;
                break;
            }
            List<Coordinate> neighbor = clientHalfMap.getVisitableNeighbors(current.getX(), current.getY());
            for(Coordinate next: neighbor){
                MapNode currentNode = clientMap.getNode(current.getX(), current.getY()).orElseThrow();
                MapNode nextNode = clientMap.getNode(next.getX(), next.getY()).orElseThrow();

                int currentCost = currentNode.getEnumField().equals(EnumField.MOUNTAIN) ? 5 : 2;
                int nextCost = nextNode.getEnumField().equals(EnumField.MOUNTAIN) ? 5 : 2;

                Integer new_cost = currentCosts.get(current) + currentCost + nextCost;

                if (!currentCosts.containsKey(next) || new_cost < currentCosts.get(next)) {
                    currentCosts.put(next, new_cost);
                    unsettledNodes.add(next);
                    previousNodes.put(next, current);
                }
            }
        }


        Map<Coordinate, Coordinate> ret = new HashMap<>();
        Coordinate currentPoint = goal;
        while (!previousNodes.get(currentPoint).equals(currentPoint)) {
            Coordinate nextPoint = previousNodes.get(currentPoint);
            ret.put(currentPoint, nextPoint);
            currentPoint = nextPoint;
        }
        ret.put(currentPoint, currentPoint);
        return ret;

    }*/

    private boolean checkIfOptimal(Coordinate goal, ClientHalfMap map, List<Coordinate> visitedPoints) {
        if (map == null) {
            throw new IllegalStateException("Client map is not initialized");
        }
        MapNode goalNode = map.getNode(goal.getX(), goal.getY()).orElseThrow();                   //Holt den MapNode, der dem Punkt goal auf der Karte entspricht.
        //unbesuchte Nachbarn zählen
        long temp = map.getSeeableNeighbors(goal.getX(), goal.getY()).stream()
                .filter(p -> !visitedPoints.contains(p)).count();
        if (goalNode.getEnumField().equals(EnumField.MOUNTAIN))
            if (temp <= 1)
                return false;

        return true;
    }



    private boolean outsideOfBorder(int x, int y) {
        return (x < xLowerBorder || x > xUpperBorder || y < yLowerBorder || y > yUpperBorder);
    }

}