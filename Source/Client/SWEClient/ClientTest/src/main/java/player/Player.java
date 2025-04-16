package player;

import data.EnumPlayerPosition;
import data.EnumPlayerState;
import messagesbase.UniquePlayerIdentifier;

public class Player {

    String firstName;
    String lastName;
    String uAccount;
    String playerID;
    private boolean collectedTreasure = false;
    private EnumPlayerState playerState;
    private boolean hasTreasure;


    public Player(String firstName,String lastName,String uAccount){
        this.uAccount = uAccount;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public Player(String uniquePlayerID, String firstName, String lastName, String uAccount, boolean b, EnumPlayerState playerState) {
        this.playerID = uniquePlayerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.uAccount = uAccount;
        this.collectedTreasure = b;
        this.playerState = playerState;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUAccount() {
        return uAccount;
    }

    public boolean hasCollectedTreasure() {
        return collectedTreasure;
    }

    public String getPlayerID() {
        return playerID;
    }
}
