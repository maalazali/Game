package player;

import clientMap.Coordinate;
import clientMap.MapNode;
import data.EnumPlayerState;
import messagesbase.UniquePlayerIdentifier;

public class Player {

    private String firstName;
    private String lastName;
    private String uAccount;
    private boolean collectedTreasure = false;
    private String uniquePlayerIdentifier;
    private EnumPlayerState playerState;
    private Coordinate position;
    private String playerID;
    private EnumPlayerState enumPlayerState;

    public Player(String uniquePlayerIdentifier, String firstName, String lastName, String uAccount, boolean collectedTreasure, EnumPlayerState playerState) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.uAccount = uAccount;
        this.collectedTreasure = collectedTreasure;
        this.uniquePlayerIdentifier = uniquePlayerIdentifier;
        this.playerState = playerState;
    }


    public Player(String uniquePlayerID, String firstName, String lastName, String uAccount, boolean b) {
        this.playerID = uniquePlayerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.uAccount = uAccount;
        this.collectedTreasure = b;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getuAccount() {
        return uAccount;
    }

    public boolean hasCollectedTreasure() {
        return this.collectedTreasure;
    }
    public void setCollectedTreasure(boolean hasCollected){
        collectedTreasure = hasCollected;
    }

    public String getUniquePlayerIdentifier() {
        return playerID;
    }

    public boolean hasWon(){
        return playerState.equals(EnumPlayerState.WON);
    }

    public boolean hasLost(){
        return playerState.equals(EnumPlayerState.LOST);
    }

    public boolean mustAct(){
        return playerState.equals(EnumPlayerState.MUST_ACT);
    }

    public void setPlayerState(EnumPlayerState playerState) {
        this.playerState = playerState;
    }

    public Coordinate getPosition() {
        return position;
    }

    // Setter für die Position
    public void setPosition(MapNode mapNode) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        this.position = position;
    }



}
