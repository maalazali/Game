package converter;

import clientMap.MapNode;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMapNode;

public class MapNodeConverter {


    public static FullMapNode convertServerNode(MapNode mapNode){

        ETerrain eTerrain = EnumFieldConverter.fieldConverter(mapNode.getEnumField());
        ETreasureState eTreasureState = EnumTreasureConverter.eTreasureState(mapNode.getEnumTreasureState());
        EFortState eFortState = EnumFortStateConverter.eFortState(mapNode.getEnumFortState());
        EPlayerPositionState ePlayerPositionState = EnumPlayerPositionConverter.convertServerPosition(mapNode.getEnumPlayerPosition());

        return new FullMapNode(eTerrain, ePlayerPositionState,eTreasureState,eFortState, mapNode.getCoordinate().getX(), mapNode.getCoordinate().getY());
    }

    public static MapNode convertClientNode(FullMapNode fullMapNode){

        MapNode mapNode = new MapNode(fullMapNode.getX(), fullMapNode.getY());

        mapNode.setEnumPlayerPosition(EnumPlayerPositionConverter.convertClientPostion(fullMapNode.getPlayerPositionState()));
        mapNode.setEnumField(EnumFieldConverter.fieldClientConverter(fullMapNode.getTerrain()));
        mapNode.setEnumFortState(EnumFortStateConverter.eClientFortState(fullMapNode.getFortState()));
        mapNode.setEnumTreasureState(EnumTreasureConverter.eClientTreasureState(fullMapNode.getTreasureState()));

        return mapNode;
    }
}
