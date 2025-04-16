package converter;

import data.EnumPlayerPosition;
import messagesbase.messagesfromserver.EPlayerPositionState;

public class EnumPlayerPositionConverter {

    public static EnumPlayerPosition convertClientPostion (EPlayerPositionState ePlayerPositionState){
        return switch(ePlayerPositionState){
            case MyPlayerPosition -> EnumPlayerPosition.MYPLAYERPOSITION;
            case NoPlayerPresent -> EnumPlayerPosition.NOPLAYERPRESENT;
            case EnemyPlayerPosition -> EnumPlayerPosition.ENEMYPLAYERPOSITION;
            case BothPlayerPosition -> EnumPlayerPosition.BOTHPLAYERPOSITION;
            default ->  throw new IllegalArgumentException("Server postion has an invalid value: " + ePlayerPositionState.toString());
        };
    }

    public static EPlayerPositionState convertServerPosition (EnumPlayerPosition enumPlayerPosition){
        return switch(enumPlayerPosition){
            case BOTHPLAYERPOSITION -> EPlayerPositionState.BothPlayerPosition;
            case ENEMYPLAYERPOSITION -> EPlayerPositionState.EnemyPlayerPosition;
            case NOPLAYERPRESENT -> EPlayerPositionState.NoPlayerPresent;
            case MYPLAYERPOSITION -> EPlayerPositionState.MyPlayerPosition;
            default ->  throw new IllegalArgumentException("Client position has an invalid value: " + enumPlayerPosition.toString());
        };
    }
}
