package controller;

import clientmap.*;
import data.ClientGameState;
import data.EnumPlayerState;
import messagesbase.UniquePlayerIdentifier;
import movment.MovementManager;
import network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.CLIView;
import view.GameGUI;

public class Controller {

    private static Logger logger = LoggerFactory.getLogger(Controller.class);

    private Network network;
    private CLIView cliView;
    private GameGUI gameGUI;
    private MovementManager movementManager;
    private UniquePlayerIdentifier uniquePlayerIdentifier;
    private ClientGameState clientGameState = new ClientGameState();

    private static final int MAX_ROUNDS = 159;
    private int roundCounter = 0;

    private boolean startWithGUI = true;

    public Controller(Network network){
        this.network = network;
        this.cliView = new CLIView();
        //this.gameGUI = new GameGUI();
        this.movementManager = new MovementManager();
    }
/*

    public boolean updateGameState(ClientGameState clientGameState) {
        if (clientGameState.equals(this.clientGameState))
            return false;

        if (clientGameState.getMyPlayer().isPresent())
            this.clientGameState.setMyPlayer(clientGameState.getMyPlayer().orElseThrow());
        if (clientGameState.getEnemyPlayer().isPresent())
            this.clientGameState.setEnemyPlayer(clientGameState.getEnemyPlayer().orElseThrow());
        this.clientGameState.setGameMap(clientGameState.getClientMap());
        this.clientGameState.setGameStateId(clientGameState.getGameID());

        return true;
    }
*/

    public void run(String firstname, String lastname, String uAccount) {
        try {
            // Spieler registrieren
            logger.info("Attempting player registration with credentials: " + firstname + " " + lastname + " " + uAccount);
            network.sendPlayerRegistration(firstname, lastname, uAccount);
            logger.info("Registration successful. Your Player ID is: {} ", network.getPlayerId());

            // Spielstatus abfragen, bevor die Karte gesendet wird
            ClientGameState gameState = network.getGameState();



            while (gameState.getEnumPlayerState() != EnumPlayerState.MUST_ACT) {
                logger.info("Wait for your turn...");
                cliView.displayGameStatus("Wait for your turn...");
                Thread.sleep(400); // Wartezeit zwischen Statusabfragen
                gameState = network.getGameState();
            }


            // Generiere und sende die Karte, wenn der Client am Zug ist
            ClientMap map = generateAndValidateMap();
            network.sendMap(map);
            logger.info("Map successfully sent.");
            if(startWithGUI){
                gameGUI = new GameGUI();
                gameGUI.setVisible(true);
            }
            // Nächste Spielaktionen in einer Schleife
            while (true) {

                gameState = network.getGameState();

                // Bewegung ausführen
                if (gameState.getEnumPlayerState() == EnumPlayerState.MUST_ACT) {
                    ClientMap mapServer = gameState.getClientMap();
                    boolean treasureCollected = gameState.getMyPlayer().orElseThrow().hasCollectedTreasure();
                    roundCounter++; // Rundenzähler erhöhen
                    System.out.println("Round:");
                    System.out.println(roundCounter);
                    System.out.println("Map:");
                    cliView.displayMap(mapServer, treasureCollected);
                    //ServerMap.printMapToConsole(gameState.getClientMap());
                    if(startWithGUI){
                        gameGUI.updateMap(mapServer,treasureCollected);
                        gameGUI.updateStatus("Round: "+roundCounter);
                    }

                    movementManager.executeNextMove(network, gameState.getClientMap(),gameState);
                }
                if (roundCounter >= MAX_ROUNDS || gameState.getEnumPlayerState() == EnumPlayerState.WON || gameState.getEnumPlayerState() == EnumPlayerState.LOST) {
                    if(startWithGUI){
                        gameState = network.getGameState();
                        gameGUI.updateMap(gameState.getClientMap(),gameState.getMyPlayer().orElseThrow().hasCollectedTreasure());
                        gameGUI.updateStatus("Spiel beendet. Status: " + gameState.getEnumPlayerState());
                    }
                    cliView.displayGameStatus("Spiel beendet. Status: " + gameState.getEnumPlayerState());
                    break;
                }
                Thread.sleep(400); // Vermeidung von ständiger Abfrage ohne Pause
            }
        } catch (Exception e) {
            logger.error("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
            cliView.displayError("Error: " + e.getMessage());
        }

    }
    public ClientMap generateAndValidateMap() {
        GenerateMap generateMap = new GenerateMap();
        ValidateMap validateMap = new ValidateMap();

        ClientMap map = generateMap.generateClientHalfMap();
        while (!validateMap.validateHalfMap(map)) {
            map = generateMap.generateClientHalfMap();
        }
        return map;
    }
}


