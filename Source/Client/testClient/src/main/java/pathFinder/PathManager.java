package pathFinder;

import clientMap.ClientHalfMap;
import clientMap.Coordinate;
import data.EnumField;
import data.EnumFortState;
import data.EnumMoves;
import data.EnumTreasureState;
import game.ClientGameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

public class PathManager {

    private static Logger logger = LoggerFactory.getLogger(Path.class);

    private Coordinate goal;
    private FindPath findPath;
    private Deque<EnumMoves> movesToReachCurrentGoal;
    private List<Coordinate> visitedPoints;

    public PathManager(FindPath findPath) {
        this.findPath = findPath;
        this.movesToReachCurrentGoal = new LinkedList<EnumMoves>();
        this.visitedPoints = new ArrayList<Coordinate>();
    }

    public PathManager() {
        this.findPath = new FindPath();
        this.movesToReachCurrentGoal = new LinkedList<EnumMoves>();
        this.visitedPoints = new ArrayList<Coordinate>();
    }

    public List<Coordinate> getVisitedPoints() {
        return visitedPoints;
    }
    private void updateGoal(ClientGameState gameState) {
        logger.info("Calculating new goal, finding optimal path.");
        Map<Coordinate, Coordinate> pathFromCurrentPositionToNewGoal = findPath.findPathToTarget(gameState, visitedPoints);

        if (pathFromCurrentPositionToNewGoal.size() <= 1)
            throw new IllegalArgumentException("The goal finder could not find an optimal goal.");

        setGoal(pathFromCurrentPositionToNewGoal);
        setMovesToReachCurrentGoal(gameState, pathFromCurrentPositionToNewGoal);
        logger.info("New Goal: {}", goal);
    }

    private void setGoal(Map<Coordinate, Coordinate> pathFromCurrentPositionToNewGoal) {
        goal = pathFromCurrentPositionToNewGoal.entrySet().stream()
                .filter(p -> !pathFromCurrentPositionToNewGoal.containsValue(p.getKey())).findFirst().orElseThrow()
                .getKey();
    }

    private void setMovesToReachCurrentGoal(ClientGameState gameState, Map<Coordinate, Coordinate> pathFromCurrentPositionToNewGoal) {
        movesToReachCurrentGoal.clear();
        Coordinate currentPoint = goal;
        while (!pathFromCurrentPositionToNewGoal.get(currentPoint).equals(currentPoint)) {
            Coordinate nextPoint = pathFromCurrentPositionToNewGoal.get(currentPoint);
            EnumMoves nextMove = convertToNextMove(currentPoint, nextPoint);

            int currentCost = gameState.getMap().getGameMap().get(currentPoint).getEnumField().equals(EnumField.MOUNTAIN) ? 5 : 2;
            int nextCost = gameState.getMap().getGameMap().get(nextPoint).getEnumField().equals(EnumField.MOUNTAIN) ? 5 : 2;

            for (int i = 0; i < currentCost + nextCost; i++) {
                movesToReachCurrentGoal.addFirst(nextMove);
            }
            currentPoint = nextPoint;

        }
    }

    public Deque<EnumMoves> getPathToCurrentGoal() {
        return movesToReachCurrentGoal;
    }


    private EnumMoves convertToNextMove(Coordinate currentCoord, Coordinate nextCoord) {
        int dx = nextCoord.getX() - currentCoord.getX();
        int dy = nextCoord.getY() - currentCoord.getY();

        if (dx == -1 && dy == 0) {
            return EnumMoves.RIGHT; // Bewegung nach rechts
        } else if (dx == 1 && dy == 0) {
            return EnumMoves.LEFT; // Bewegung nach links
        } else if (dx == 0 && dy == -1) {
            return EnumMoves.UP; // Bewegung nach oben
        } else if (dx == 0 && dy == 1) {
            return EnumMoves.DOWN; // Bewegung nach unten
        }

        throw new IllegalArgumentException("The passed Coordinates " + currentCoord.toString() + " and "
                + nextCoord.toString() + " are not neighbors.");
    }

    public EnumMoves getNextMove(ClientGameState gameState) {
        ClientHalfMap map = gameState.getMap();
        gameState.setGameMap(map);
        Coordinate myPlayerPosition = map.getMyPlayerPosition();

        if (!visitedPoints.contains(myPlayerPosition)) {
            if (map.getNode(myPlayerPosition.getX(), myPlayerPosition.getY()).orElseThrow().getEnumField()
                    .equals(EnumField.MOUNTAIN)) {
                map.getSeeableNeighbors(myPlayerPosition.getX(), myPlayerPosition.getY()).forEach(p -> {
                    if (gameState.getMap().getNode(p.getX(), p.getY()).orElseThrow().getEnumTreasureState()
                            .equals(EnumTreasureState.NOORUNKNOWNTREASURESTATE)
                            && !gameState.getMap().getNode(p.getX(), p.getY()).orElseThrow().getEnumFortState()
                            .equals(EnumFortState.ENEMYFORTPRESENT))
                        visitedPoints.add(p);
                    logger.debug("New explored node: {}", p);
                });
            }
            visitedPoints.add(myPlayerPosition);
            logger.debug("New explored node: {}", myPlayerPosition);
        }

        while (movesToReachCurrentGoal.isEmpty())
            updateGoal(gameState);

        logger.info("Made move: {}", movesToReachCurrentGoal.peek());
        return movesToReachCurrentGoal.removeFirst();
    }


}
