package converter;

import clientMap.ClientHalfMap;
import data.EnumPlayerPosition;
import data.EnumPlayerState;
import game.ClientGameState;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import player.Player;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static converter.EnumPlayerPositionConverter.convertClientPostion;
import static converter.EnumPlayerStateConverter.playerClientConverter;
import static converter.MapConverter.convertToGameMap;
import static converter.PlayerConverter.convertToPlayer;

public class ClientGameConverter {
    private String uniquePlayerIdentifier;

    private EPlayerPositionState ePlayerPositionState;
    private EPlayerGameState ePlayerGameState;


    // Setter for uniquePlayerIdentifier
    public void setUniquePlayerIdentifier(String uniquePlayerIdentifier) {
        this.uniquePlayerIdentifier = uniquePlayerIdentifier;
    }

    /*public static ClientGameState convertToClientState(GameState gameState, String playerId ){

        ClientGameState clientGameState;
        String gameStateId = gameState.getGameStateId();
        ClientHalfMap clientMap = convertToGameMap(gameState.getMap());
        Set<Player> player = gameState.getPlayers().stream().map(p -> convertToPlayer(p)).collect(Collectors.toSet());
        Player myPlayer = player.stream().filter(x -> x.getUniquePlayerIdentifier().equals(playerId)).findAny().orElseThrow();
        Optional<Player> enemyPlayer = player.stream().filter(x -> !x.getUniquePlayerIdentifier().equals(playerId)).findAny();

        if (enemyPlayer.isPresent())
            clientGameState = new ClientGameState(gameStateId, myPlayer, enemyPlayer.orElseThrow(), clientMap);
        else
            clientGameState = new ClientGameState(gameStateId, myPlayer, clientMap);

        return clientGameState;
    }*/

    public ClientGameState convertToClientState(GameState gameState) {
        if (gameState == null) {
            throw new IllegalArgumentException("GameState must not be null");
        }

        Player myPlayer = null;
        Player enemyPlayer = null;

        String gameID = gameState.getGameStateId();

        ClientHalfMap clientHalfMap = convertToGameMap(gameState.getMap());

        Set<PlayerState> playerStates = gameState.getPlayers();

        for (PlayerState playerState : playerStates) {
            myPlayer = convertToPlayer(playerState);
            enemyPlayer = convertToPlayer(playerState);
            break;
        }

        return new ClientGameState(gameID, myPlayer, enemyPlayer, clientHalfMap);
    }


   /*
    public static ClientGameState convertToClientState(GameState gameState){

        String gameIdentifier = gameState.getGameStateId();
        MapConverter mapConverter =  mapConverter(uniquePlayerIdentifier.getUniquePlayerID(), gameState.getMap());

            var converter = new PlayerStateConverter();
            Set<PlayerState> playerStates = gameState.getPlayers();

            Map<Player, EPlayerState> playersWithStates = playerStates.stream().collect(Collectors.toMap(
                    playerState -> new Player(playerState.getFirstName(), playerState.getLastName(),
                            playerState.getUAccount(), playerState.hasCollectedTreasure()),
                    player -> converter.convertFromServerEntity(player.getState()), (existing, replacement) -> existing));

            return new ClientGameState(gameIdentifier, clientMap, playersWithStates);

    }
    */

}


