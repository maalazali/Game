package converter;

import data.EnumMoves;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.PlayerMove;
import player.Player;

public class EnumMoveConverter {

    public static EnumMoves convertClientMove(EMove eMove){
        return switch(eMove){
            case Up -> EnumMoves.UP;
            case Down -> EnumMoves.DOWN;
            case Left -> EnumMoves.LEFT;
            case Right -> EnumMoves.RIGHT;
            default -> throw new IllegalArgumentException("server mvoe has an invalid value: " + eMove.toString());
        };
    }
    public EMove convertServerMove(EnumMoves enumMoves){
        return switch(enumMoves){
            case RIGHT -> EMove.Right;
            case LEFT -> EMove.Left;
            case DOWN -> EMove.Down;
            case UP -> EMove.Up;
            default -> throw new IllegalArgumentException("Client move has an invalid value: " + enumMoves.toString());
        };
    }

    public PlayerMove convertToPlayerMove(EnumMoves moves, String playerId){
        return PlayerMove.of(playerId, convertServerMove(moves));
    }
}
