package converter;

import messagesbase.messagesfromserver.PlayerState;
import player.Player;

import static converter.EnumPlayerStateConverter.playerClientConverter;

public class PlayerConverter {

    public static Player convertToPlayer(PlayerState playerState){
        Player player = null;
        return new Player(playerState.getUniquePlayerID(), playerState.getFirstName(), playerState.getLastName(), playerState.getUAccount(), playerState.hasCollectedTreasure(), playerClientConverter(playerState.getState()));

    }
}
