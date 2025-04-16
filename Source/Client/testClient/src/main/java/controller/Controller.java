package controller;

import clientMap.ClientHalfMap;
import clientMap.GenerateHalfMap;
import clientMap.ValidateMap;
import data.EnumMoves;
import data.EnumPlayerState;
import game.ClientGameState;
import messagesbase.UniquePlayerIdentifier;
import network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pathFinder.PathManager;
import player.Player;


public class Controller {

    private static Logger logger = LoggerFactory.getLogger(Controller.class);

    private Network network;
    private UniquePlayerIdentifier uniquePlayerIdentifier;
    private ClientGameState gameState = new ClientGameState();
    private String serverBaseUrl;
    private ClientGameState clientGameState;
    private PathManager pathManager;

    private String gameIdentifier;
    public Controller(Network network, PathManager pathManager){
        this.network = network;
        this.pathManager = pathManager;
    }

    /*public boolean updateGameState(ClientGameState gameState) {

        if (gameState.equals(this.gameState))
            return false;
        // Validierung von Spieler und Gegner
        gameState.getPlayer().ifPresent(player -> {
            player.setPlayerState(gameState.getEnumPlayerState());
            this.gameState.setPlayer(player);
        });
        gameState.getPlayer().ifPresentOrElse(this.gameState::setPlayer,
                () -> logger.warn("Player is not set in the current game state."));
        gameState.getEnemyPlayer().ifPresentOrElse(this.gameState::setEnemyPlayer,
                () -> logger.warn("Enemy player is not set in the current game state."));

        // Validierung der Karte
        if (gameState.getMapOptional().isPresent()) {
            this.gameState.setGameMap(gameState.getMapOptional().get());
        } else {
            logger.warn("Map is not set in the current game state.");
        }

        this.gameState.setGameStateId(gameState.getGameStateId());

        return true;
    }*/

    public boolean updateGameState(ClientGameState clientGameState) {
        if (clientGameState.equals(this.clientGameState))
            return false;

        if (clientGameState.getPlayer().isPresent())
            this.clientGameState.setPlayer(clientGameState.getPlayer().orElseThrow());
        if (clientGameState.getEnemyPlayer().isPresent())
            this.clientGameState.setEnemyPlayer(clientGameState.getEnemyPlayer().orElseThrow());
        this.clientGameState.setGameMap(clientGameState.getMap());
        this.clientGameState.setGameStateId(clientGameState.getGameStateId());

        return true;
    }



    private void move(){
        EnumMoves moves = pathManager.getNextMove(gameState);
        //EnumMoves moves = EnumMoves.RIGHT;
        logger.info("Moving {}", moves);
        network.sendMove(moves);
        gameState.incrementRounds();
    }
    public void run(String firstName, String lastName, String uAccount) {

//    try{
//    ClientGameState currentGameState = network.getGameState();
//
//    if (currentGameState == null) {
//        logger.error("getGameState() returned null.");
//        throw new IllegalStateException("Game state is null.");
//    }
//
//// Logge die Inhalte des GameState-Objekts
//    logger.info("GameState received: {}", currentGameState);
//
//    currentGameState.getPlayer().ifPresentOrElse(
//            player -> logger.info("Player is present: {}", player.getUniquePlayerIdentifier()),
//            () -> logger.warn("Player is not present in the received game state.")
//    );
//
//    currentGameState.getEnemyPlayer().ifPresentOrElse(
//            enemy -> logger.info("Enemy player is present."),
//            () -> logger.warn("Enemy player is not present.")
//    );
//
//    if (currentGameState.getMapOptional().isPresent()) {
//        logger.info("Map is present in the game state.");
//    } else {
//        logger.warn("Map is not present in the game state.");
//    }
//} catch (InterruptedException e) {
//    throw new RuntimeException(e);
//}


        try {
            logger.info("Attempting player registration with credentials: " +firstName+ " " +lastName +" "+ uAccount);
            network.sendPlayerRegistration(firstName, lastName, uAccount);
            logger.info("Registration successful. Your Player ID is: {} " + network.getPlayerId());
        } catch (IllegalArgumentException e) {
            logger.error("Registration failed. " + e);
            System.exit(1);

        }
        if (gameState == null) {
            System.out.println("The gameState passed to updateGameState is null.");
        }
        try{
            while(true){
                ClientGameState currentGameState = network.getGameState();
                if(currentGameState.getEnumPlayerState() == EnumPlayerState.MUST_ACT){
                    logger.info("Spieler ist am Zug. Beende Warte-Schleife.");
                    break;
                }
                try {
                    Thread.sleep(400);
                }catch (InterruptedException e) {

                }
            }

        }catch (InterruptedException e){
            logger.error("Received Error message from server:" +e);
            System.exit(1);
        }

        try{
            logger.info("Generating map...");
            GenerateHalfMap generateHalfMap = new GenerateHalfMap();
            ValidateMap validateMap = new ValidateMap();


            ClientHalfMap map = generateHalfMap.generateClientHalfMap();
            while (!validateMap.validateHalfMap(map)) {
                System.out.println("Generated map");
                if (map == null || map.getGameMap() == null) {
                    logger.error("Generated map or its gameMap is null!");
                    throw new IllegalStateException("Invalid map generated.");
                }
                map = generateHalfMap.generateClientHalfMap();
            }
            network.sendMap(map);
            logger.info("Map successfully sent.");
        }catch (IllegalArgumentException e){//catching the wrong excpetion
            logger.error("Failed to send map.", e);
            System.exit(1);
        }
        try {
            while (true) {
                updateGameState(network.getGameState());
                if (gameState == null) {
                    System.out.println(" GET PLAYER: " + gameState.getPlayer());
                    System.out.println(" GET PLAYER STATES: " + gameState.getPlayerStates());
                    System.out.println(" GET ENEMY PLAYER: " + gameState.getEnemyPlayer());
                    System.out.println(" GET GAME STATE ID: " + gameState.getGameStateId());
                    System.out.println(" GET ENUM PLAYER STATE: " + gameState.getEnumPlayerState());
                }
                //if(currentGameState.getEnumPlayerState()== EnumPlayerState.WON)
                if (gameState.getEnumPlayerState()== EnumPlayerState.WON) {
                    System.out.println("Congratulations! You are the winner!");
                    break;
                } else if (gameState.getEnumPlayerState()== EnumPlayerState.LOST) {
                    System.out.println("You lost the game.");
                    break;
                }
                if (gameState.getEnumPlayerState() == EnumPlayerState.MUST_ACT){
                    move();
                    System.out.println("MY TURN TO ACT!! " + network.getPlayerId());
                }

                /*
                ClientGameState currentGameState = network.getGameState();
                logger.info("Aktueller Spielstatus: {}", currentGameState.getEnumPlayerState());
                if (currentGameState.getEnumPlayerState() == EnumPlayerState.WON) {
                    logger.info("Spieler hat gewonnen!");
                    break;
                }else if (currentGameState.getEnumPlayerState() == EnumPlayerState.LOST) {
                    System.out.println("Spieler hat verloren!");
                    break;
                }
                if (currentGameState.getEnumPlayerState() == EnumPlayerState.MUST_ACT){
                    move();
                }*/

                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {

                }

            }
        } catch (InterruptedException e) {
            logger.error("Received error message from server: {}", e);
            System.exit(1);
        }
    }
}
