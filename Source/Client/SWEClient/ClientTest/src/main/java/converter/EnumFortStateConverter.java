package converter;
import data.EnumFortState;
import messagesbase.messagesfromserver.EFortState;

public class EnumFortStateConverter {
    public static EFortState eFortState (EnumFortState enumFortState){
        return switch (enumFortState) {
            case MYFORTPRESENT -> EFortState.MyFortPresent;
            case ENEMYFORTPRESENT -> EFortState.EnemyFortPresent;
            case NOORUNKNOWNFORTSTATE -> EFortState.NoOrUnknownFortState;
            default -> throw new IllegalArgumentException("Client fort has an invalid value: " + enumFortState.toString());
        };
    }
    public static EnumFortState eClientFortState (EFortState eFortState){
        return switch(eFortState){
            case MyFortPresent -> EnumFortState.MYFORTPRESENT;
            case EnemyFortPresent -> EnumFortState.ENEMYFORTPRESENT;
            case NoOrUnknownFortState -> EnumFortState.NOORUNKNOWNFORTSTATE;
            default -> throw new IllegalArgumentException("Server fort has an invalid value: " + eFortState.toString());
        };
    }
}
