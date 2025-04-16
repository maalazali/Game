package converter;
import data.EnumField;
import messagesbase.messagesfromclient.ETerrain;


public class EnumFieldConverter {
    public static ETerrain fieldConverter(EnumField enumField){
        return switch (enumField) {
            case MOUNTAIN -> ETerrain.Mountain;
            case GRAS -> ETerrain.Grass;
            case WATER -> ETerrain.Water;
            default ->
                    throw new IllegalArgumentException("Client terrain has an invalid value: " + enumField.toString());
        };
    }

    public static EnumField fieldClientConverter(ETerrain eTerrain){
        return switch(eTerrain){
            case Mountain -> EnumField.MOUNTAIN;
            case Grass -> EnumField.GRAS;
            case Water -> EnumField.WATER;
            default ->
                    throw new IllegalArgumentException("Server terrain has an invalid value: " + eTerrain.toString());
        };
    }
}
