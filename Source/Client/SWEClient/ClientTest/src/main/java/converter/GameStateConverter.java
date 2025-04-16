package converter;

import clientmap.ClientMap;
import data.ClientGameState;
import data.EnumPlayerState;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromserver.GameState;
import player.Player;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static converter.MapConverter.convertMap;
import static converter.MapConverter.convertToGameMap;
import static converter.PlayerConverter.convertToPlayer;

public class GameStateConverter {

    public ClientGameState convertGameState(GameState gameState,String playerId ){

        ClientGameState clientGameState;
        String gameStateId = gameState.getGameStateId();
        ClientMap clientMap = convertToGameMap(gameState.getMap());
        Set<Player> player = gameState.getPlayers().stream().map(p -> convertToPlayer(p)).collect(Collectors.toSet());
        Player myPlayer = player.stream().filter(x -> x.getPlayerID().equals(playerId)).findAny().orElseThrow();
        Optional<Player> enemyPlayer = player.stream().filter(x -> !x.getPlayerID().equals(playerId)).findAny();

        if (enemyPlayer.isPresent())
            clientGameState = new ClientGameState(gameStateId, myPlayer, enemyPlayer.orElseThrow(), clientMap);
        else
            clientGameState = new ClientGameState(gameStateId, myPlayer, clientMap);

        // Spielerstatus bestimmen
        EnumPlayerState playerState = determinePlayerState(gameState, playerId);

        // ClientGameState erstellen
        clientGameState.setEnumPlayerState(playerState);

        return clientGameState;
    }


    /*
    public ClientGameState convertGameState(GameState gameState, String playerId) {

        ClientGameState clientGameState;
        String gameStateId = gameState.getGameStateId();
        ClientMap clientMap = convertToGameMap(gameState.getMap());
        Set<Player> players = gameState.getPlayers().stream()
                .map(p -> convertToPlayer(p))
                .collect(Collectors.toSet());

        Player myPlayer = players.stream()
                .filter(x -> x.getPlayerID().equals(playerId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Player not found!"));

        Optional<Player> enemyPlayer = players.stream()
                .filter(x -> !x.getPlayerID().equals(playerId))
                .findAny();

        // Bestimme den Spielstatus (EnumPlayerState)
        EnumPlayerState playerState = determinePlayerState(gameState, playerId);

        // Erstelle ClientGameState
        if (enemyPlayer.isPresent()) {
            clientGameState = new ClientGameState(gameStateId, myPlayer, enemyPlayer.orElseThrow(), clientMap);
        } else {
            clientGameState = new ClientGameState(gameStateId, myPlayer, clientMap);
        }

        // Setze EnumPlayerState
        clientGameState.setEnumPlayerState(playerState);

        return clientGameState;
    }

     */

    private EnumPlayerState determinePlayerState(GameState gameState, String playerId) {
        // Suche den Spieler im GameState
        var playerState = gameState.getPlayers().stream()
                .filter(p -> p.getUniquePlayerID().equals(playerId))
                .map(p -> p.getState())
                .findFirst()
                .orElseThrow();

        // Mapping von String zu EnumPlayerState
        switch (playerState) {
            case playerState.MustAct:
                return EnumPlayerState.MUST_ACT;
            case playerState.MustWait:
                return EnumPlayerState.MUST_WAIT;
            case playerState.Won:
                return EnumPlayerState.WON;
            case playerState.Lost:
                return EnumPlayerState.LOST;
            default:
                throw new IllegalStateException("Unbekannter Spielerzustand: " + playerState);
        }
    }

}
