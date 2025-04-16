package network;

import clientMap.ClientHalfMap;
import converter.ClientGameConverter;
import converter.EnumMoveConverter;
import data.EnumMoves;
import data.EnumPlayerState;
import game.ClientGameState;
import messagesbase.ResponseEnvelope;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.*;
import messagesbase.messagesfromserver.GameState;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static converter.MapConverter.convertMap;

public class Network {

    private static Logger logger = LoggerFactory.getLogger(Network.class);

    private WebClient baseWebClient;
    private String gameId;
    private String playerId;
    String serverBaseUrl;
    private EnumMoveConverter enumMoveConverter;

    public Network(WebClient baseWebClient, String gameId, EnumMoveConverter enumMoveConverter) {
        this.baseWebClient = baseWebClient;
        this.gameId = gameId;
        this.enumMoveConverter = enumMoveConverter;
    }


    public Network(String serverBaseUrl, String gameId) {
        this.baseWebClient = WebClient.builder().baseUrl(serverBaseUrl + "/games")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE).build();
        this.gameId = gameId;
        this.serverBaseUrl = serverBaseUrl;
        this.enumMoveConverter = new EnumMoveConverter();
    }

    public String sendPlayerRegistration(String firstName, String lastName, String uAccount){

        PlayerRegistration playerReg = new PlayerRegistration(firstName, lastName, uAccount);

        Mono<ResponseEnvelope<UniquePlayerIdentifier>> webAccess = baseWebClient.method(HttpMethod.POST)
                .uri("/" + gameId + "/players").body(BodyInserters.fromValue(playerReg)).retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseEnvelope<UniquePlayerIdentifier>>() {
                });
        ResponseEnvelope<UniquePlayerIdentifier> resultReg = webAccess.block();

        if (resultReg.getState() == ERequestState.Error) {
            System.err.println("Client error, errormessage: " + resultReg.getExceptionMessage());
        } else {
            UniquePlayerIdentifier uniqueID = resultReg.getData().get();
            System.out.println("My Player ID: " + uniqueID.getUniquePlayerID());
        }
        UniquePlayerIdentifier uniqueId = resultReg.getData().get();
        playerId = uniqueId.getUniquePlayerID();
        return playerId;
    }
    public ClientGameState getGameState() throws InterruptedException {

            Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.GET)
                    .uri("/" + gameId + "/states/" + playerId)
                    .retrieve().bodyToMono(ResponseEnvelope.class);

            ResponseEnvelope<GameState> requestResult = webAccess.block();

            if (requestResult.getState() == ERequestState.Error) {
                logger.error("Client error, errormessage: " + requestResult.getExceptionMessage());
                throw new IllegalStateException("Fehler beim Abrufen des Spielstatus");
            }

            if (!requestResult.getData().isPresent()) {
                logger.error("No game state data received from server.");
                throw new IllegalStateException("Kein Spielstatus verfügbar");
            }

            GameState currentServerGameState = requestResult.getData().orElse(null);
            ClientGameConverter converter = new ClientGameConverter();


            return converter.convertToClientState(currentServerGameState);
    }



    public String getPlayerId() {
        return playerId;
    }

    public void sendMap(ClientHalfMap clientHalfMap){
        System.out.println("sendMap() wurde aufgerufen!");
        PlayerHalfMap playerHalfMap = convertMap(playerId, clientHalfMap);

        Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + gameId + "/halfmaps")
                .body(BodyInserters.fromValue(playerHalfMap)).retrieve().bodyToMono(ResponseEnvelope.class);
        ResponseEnvelope<PlayerHalfMap> responseEnvelope = webAccess.block();

        String errorMessage = responseEnvelope.getExceptionMessage();
        String errorName = responseEnvelope.getExceptionName();
        if(responseEnvelope.getState() == ERequestState.Error){
            System.out.println("Fehler beim Versenden der Karte"+ errorName + " - " + errorMessage);
        }else{
            System.out.println("Die Karte wurde erfolgreich verschickt");
        }
    }
    public void sendMove(EnumMoves moves){
        System.out.println("Sending move");
        PlayerMove playerMove = enumMoveConverter.convertToPlayerMove(moves, playerId);

        Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + gameId + "/moves")
                .body(BodyInserters.fromValue(playerMove)).retrieve().bodyToMono(ResponseEnvelope.class);
        ResponseEnvelope<Void> responseEnvelope = webAccess.block();
        if(responseEnvelope.getState() == ERequestState.Error){
            System.out.println("Bewegung konnte nicht geschickt werden");
        }else{
            System.out.println("Bewegung wurde geschickt: "+playerMove);
        }

    }
}
