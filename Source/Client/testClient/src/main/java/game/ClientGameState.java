package game;

import clientMap.ClientHalfMap;
import clientMap.GameMap;
import clientMap.MapNode;
import data.EnumFortState;
import data.EnumPlayerPosition;
import data.EnumPlayerState;
import messagesbase.UniqueGameIdentifier;
import messagesbase.messagesfromserver.PlayerState;
import player.Player;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class ClientGameState {

    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    private String gameIdentifier;
    private int roundNumber;
    private Player myPlayer;
    private Player enemyPlayer;
    private ClientHalfMap map;


    private EnumPlayerState enumPlayerState;
    private Set<PlayerState> playerStates;
    private String gameStateId;
    private EnumPlayerPosition playerPosition;


    public Set<PlayerState> getPlayerStates() {
        return playerStates;
    }

    public ClientGameState(String gameIdentifier, ClientHalfMap map, Player player, EnumPlayerState enumPlayerState) {
        this.gameIdentifier = gameIdentifier;
        this.map = map;
        this.myPlayer = player;
        this.enumPlayerState = enumPlayerState;
    }
    public ClientGameState(String gameStateId, Player myPlayer, Player enemyPlayer, ClientHalfMap map) {
        this.gameStateId = gameStateId;
        this.myPlayer = myPlayer;
        this.enemyPlayer = enemyPlayer;
        this.map = map;
    }
    public ClientGameState(String gameStateId, Player myPlayer, ClientHalfMap map) {
        this.gameStateId = gameStateId;
        this.myPlayer = myPlayer;
        this.map = map;
    }
    public ClientGameState(){
        this.map = new ClientHalfMap(); // Leere Karte
        this.myPlayer = null; // Spieler muss gesetzt werden
        this.enemyPlayer = null; // Gegner muss gesetzt werden
    }

    public String getGameIdentifier() {
        return gameIdentifier;
    }

    public void setGameMap(ClientHalfMap map) {
        ClientHalfMap beforeChange = this.map;
        this.map = map;
        changes.firePropertyChange("gameMap", beforeChange, map);
    }

    public ClientHalfMap getMap() {
        if (map == null) {
            throw new IllegalStateException("Map is not initialized in ClientGameState");
        }
        return map;
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(myPlayer);
    }

    public void setPlayer(Player player) {
        this.myPlayer = player;
    }

    public Optional<Player> getEnemyPlayer() {
        return Optional.ofNullable(enemyPlayer);
    }

    public void setEnemyPlayer(Player enemyPlayer) {
        this.enemyPlayer = enemyPlayer;
    }

    public void setGameStateId(String gameStateId) {
        this.gameStateId = gameStateId;
    }

    public String getGameStateId() {
        return gameStateId;
    }

    public EnumPlayerState getEnumPlayerState() {
        return enumPlayerState;
    }

    public EnumPlayerPosition getPlayerPosition() {
        return playerPosition;
    }

    public Optional<GameMap> getMapOptional() {
        return Optional.ofNullable(map);
    }

    // Hilfsmethode zum Finden der Schlüssel für MYFORTPRESENT und ENEMYFORTPRESENT
    private String findFortKey(EnumFortState fortState, Map<String, MapNode> map) {
        return map.entrySet().stream()
                .filter(entry -> entry.getValue().getEnumFortState().equals(fortState))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }


    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClientGameState other = (ClientGameState) obj;
        return Objects.equals(gameStateId, other.gameStateId);
    }


    public void incrementRounds() {
        roundNumber++;
    }
}
