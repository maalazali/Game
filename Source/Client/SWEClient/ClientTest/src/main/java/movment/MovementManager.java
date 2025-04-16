package movment;

import clientmap.ClientMap;
import clientmap.Coordinate;
import clientmap.MapNode;
import data.*;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromserver.GameState;
import network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servermap.ServerMap;

import java.util.*;

public class MovementManager {

    private static final Logger logger = LoggerFactory.getLogger(MovementManager.class);

    private  Coordinate currentPlayerPosition;
    private boolean treasureFound = false;
    private boolean mapInitialized = false;
    private boolean enemyMapInitialized = false;
    private final ExploreSystem explore = new ExploreSystem();

    public void executeNextMove(Network network, ClientMap map, ClientGameState gameState) {
        if(!mapInitialized){
            MapNode playerNode = null;
            playerNode = map.getClientMap()
                    .values()
                    .stream()
                    .filter(node -> node.getEnumPlayerPosition() == EnumPlayerPosition.MYPLAYERPOSITION)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Spielerposition nicht auf der Karte gefunden!"));

            currentPlayerPosition = playerNode.getCoordinate();
            explore.defineTerritories(map,currentPlayerPosition);
            mapInitialized = true;
        }
        Optional<MapNode> playerNode = map.getClientMap().values().stream()
                .filter(node -> node.getEnumPlayerPosition() == EnumPlayerPosition.MYPLAYERPOSITION || node.getEnumPlayerPosition() == EnumPlayerPosition.BOTHPLAYERPOSITION)
                .findFirst();

        currentPlayerPosition = playerNode
                .map(MapNode::getCoordinate)
                .orElseThrow(() -> new IllegalStateException("Spielerposition nicht auf der Karte gefunden!"));

        if(!treasureFound){
            treasureFound = gameState.getMyPlayer().orElseThrow().hasCollectedTreasure();
            System.out.println("Treasure found:");
            System.out.println(treasureFound);
        }

        Coordinate nextGoal = null;
        if (!treasureFound) {
            nextGoal = explore.determineNextGoal(map, currentPlayerPosition, treasureFound);
        }
        else{
            if(!enemyMapInitialized){
                explore.unexploredGrass.clear();
                enemyMapInitialized = true;
            }

            System.out.println("Search Enemy Fort");
            nextGoal = explore.determineNextGoal(map, currentPlayerPosition, treasureFound);
        }
        if(nextGoal != null){
            EnumMoves move = determineMoveDirection(currentPlayerPosition, nextGoal, map);

            EMove moveConverted = convertEnumMovesToEMoves(move);

            System.out.println("Move:");
            System.out.println(moveConverted);
            logger.info("Moving to: " + nextGoal + " in direction: " + move);
            network.sendMove(network.getPlayerId(), moveConverted);

        }
    }
    private EnumMoves determineMoveDirection(Coordinate current, Coordinate to, ClientMap map) {
        if (to.getY() > current.getY()) return EnumMoves.Down;
        if (to.getY() < current.getY()) return EnumMoves.Up;
        if (to.getX() > current.getX()) return EnumMoves.Right;
        if (to.getX() < current.getX()) return EnumMoves.Left;
        return null;
    }

    private static EMove convertEnumMovesToEMoves(EnumMoves move) {
        return switch (move) {
            case Up -> EMove.Up;
            case Down -> EMove.Down;
            case Left -> EMove.Left;
            case Right -> EMove.Right;
            default -> throw new IllegalArgumentException("Invalid move direction");
        };
    }
}
