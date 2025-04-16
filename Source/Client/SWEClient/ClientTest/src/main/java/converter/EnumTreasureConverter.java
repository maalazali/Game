package converter;
import data.EnumTreasureState;
import messagesbase.messagesfromserver.ETreasureState;

public class EnumTreasureConverter {
    public static ETreasureState eTreasureState (EnumTreasureState enumTreasureState){
        return switch (enumTreasureState) {
            case NOORUNKNOWNTREASURESTATE -> ETreasureState.NoOrUnknownTreasureState;
            case MYTREASUREPRESENT -> ETreasureState.MyTreasureIsPresent;
            default -> throw new IllegalArgumentException("Client treasure has an invalid value: " + enumTreasureState.toString());
        };
    }
    public static EnumTreasureState eClientTreasureState (ETreasureState eTreasureState){
        return switch(eTreasureState){
            case NoOrUnknownTreasureState -> EnumTreasureState.NOORUNKNOWNTREASURESTATE;
            case MyTreasureIsPresent -> EnumTreasureState.MYTREASUREPRESENT;
            default -> throw new IllegalArgumentException("Server treasure has an invalid value: " + eTreasureState.toString());

        };
    }
}
