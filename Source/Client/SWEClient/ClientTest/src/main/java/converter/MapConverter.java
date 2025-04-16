package converter;

import clientmap.ClientMap;
import clientmap.Coordinate;
import clientmap.MapNode;
import data.EnumField;
import data.EnumPlayerPosition;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapConverter {

    public static PlayerHalfMap convertMap(String uniquePlayerIdentifier, ClientMap clientHalfMap){

        Collection<PlayerHalfMapNode> nodes = new ArrayList<>();

        for(MapNode mapNode: clientHalfMap.getClientMap().values()){
            var x = mapNode.getCoordinate().getX();
            var y = mapNode.getCoordinate().getY();
            var fortState = EnumFortStateConverter.eFortState(mapNode.getEnumFortState()) == EFortState.MyFortPresent ;
            var treasureState = EnumTreasureConverter.eTreasureState(mapNode.getEnumTreasureState());
            var terrainState = EnumFieldConverter.fieldConverter(mapNode.getEnumField());
            PlayerHalfMapNode node = new PlayerHalfMapNode(x, y, fortState, terrainState);
            nodes.add(node);
        }

        return new PlayerHalfMap(uniquePlayerIdentifier, nodes);
    }

    public static ClientMap convertToGameMap(FullMap fullMap){

        Map<String,MapNode> clientMapNodes = new HashMap<>();

        for(FullMapNode fullMapNode: fullMap.getMapNodes()){
            int x = fullMapNode.getX();
            int y = fullMapNode.getY();
            Coordinate coordinate = new Coordinate(x, y);

            var fortState = EnumFortStateConverter.eClientFortState(fullMapNode.getFortState());
            var treasureState = EnumTreasureConverter.eClientTreasureState(fullMapNode.getTreasureState());
            EnumField terrain = EnumFieldConverter.fieldClientConverter(fullMapNode.getTerrain());
            EnumPlayerPosition enumPlayerPosition = EnumPlayerPositionConverter.convertClientPostion(fullMapNode.getPlayerPositionState());

            MapNode mapNode = new MapNode(terrain, fortState, treasureState,coordinate);
            mapNode.setEnumPlayerPosition(enumPlayerPosition);
            String key = x + "" + y;
            clientMapNodes.put(key, mapNode);
        }
        return new ClientMap(clientMapNodes);
    }
}
