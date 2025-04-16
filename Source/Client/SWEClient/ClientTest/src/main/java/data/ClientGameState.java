package data;

import clientmap.ClientMap;
import player.Player;
import java.util.Objects;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Optional;

public class ClientGameState {

    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }


    private EnumPlayerState enumPlayerState;
    private Player myPlayer;
    private Player enemyPlayer;

    private String gameID;
    private ClientMap clientMap;

    public ClientGameState(){
        gameID = "empty game state";
    }

    public ClientGameState(String gameID, Player myPlayer, Player enemyPlayer, ClientMap clientMap){
        this.gameID = gameID;
        this.myPlayer = myPlayer;
        this.enemyPlayer = enemyPlayer;
        this.clientMap = clientMap;

    }
    public ClientGameState(String gameID, Player myPlayer, ClientMap clientMap){
        this.gameID = gameID;
        this.myPlayer = myPlayer;
        this.clientMap = clientMap;

    }


    public EnumPlayerState getEnumPlayerState() {
        return enumPlayerState;
    }

    public void setEnumPlayerState(EnumPlayerState playerState){
        this.enumPlayerState = playerState;
    }

    public String getGameID() {
        return gameID;
    }

    public ClientMap getClientMap() {
        return clientMap;
    }

    public void setGameStateId(String gameStateId) {
        this.gameID = gameStateId;
    }

    public void setGameMap(ClientMap clientMap) {
        ClientMap beforeChange = this.clientMap;
        this.clientMap = clientMap;
        changes.firePropertyChange("gameMap", beforeChange, clientMap);
    }

    public Optional<Player> getEnemyPlayer() {
        return Optional.ofNullable(enemyPlayer);
    }

    public void setEnemyPlayer(Player enemyPlayer) {
        this.enemyPlayer = enemyPlayer;
    }

    public Optional<Player> getMyPlayer() {
        return Optional.ofNullable(myPlayer);
    }

    public void setMyPlayer(Player myPlayer) {
        this.myPlayer = myPlayer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClientGameState other = (ClientGameState) obj;
        return Objects.equals(gameID, other.gameID);
    }

}
