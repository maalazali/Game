package converter;

import data.EnumPlayerState;
import data.ClientGameState;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.PlayerState;

import java.util.Set;


public class EnumPlayerStateConverter {
    public static EPlayerGameState playerConverter(EnumPlayerState enumPlayerState){

        return switch (enumPlayerState) {
            case LOST -> EPlayerGameState.Lost;
            case MUST_WAIT -> EPlayerGameState.MustWait;
            case MUST_ACT -> EPlayerGameState.MustAct;
            case WON -> EPlayerGameState.Won;
            default ->
                    throw new IllegalArgumentException("Client player state has an invalid value: " + enumPlayerState.toString());
        };
    }

    public static EnumPlayerState playerClientConverter(EPlayerGameState ePlayerGameState){
        return switch(ePlayerGameState){
            case Lost -> EnumPlayerState.LOST;
            case Won -> EnumPlayerState.WON;
            case MustAct -> EnumPlayerState.MUST_ACT;
            case MustWait -> EnumPlayerState.MUST_WAIT;
            default -> throw new IllegalArgumentException("Server player state has an invalid value: " + ePlayerGameState.toString());
        };
    }


}
