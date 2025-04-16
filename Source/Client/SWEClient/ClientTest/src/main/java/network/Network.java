package network;

import clientmap.ClientMap;
import converter.GameStateConverter;
import data.ClientGameState;
import data.EnumMoves;
import messagesbase.ResponseEnvelope;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.*;
import messagesbase.messagesfromserver.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import servermap.ServerMap;

import java.time.Duration;

import static converter.MapConverter.convertMap;

public class Network {
    private static Logger logger = LoggerFactory.getLogger(Network.class);

    private WebClient baseWebClient;
    private String gameId;
    private String playerId;

    String serverBaseUrl;

    public Network(String serverBaseUrl, String gameId) {
        this.baseWebClient = WebClient.builder().baseUrl(serverBaseUrl + "/games")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE).build();
        this.gameId = gameId;
        this.serverBaseUrl = serverBaseUrl;
    }
    public Network(String serverBaseUrl) {
        this.baseWebClient = WebClient.builder().baseUrl(serverBaseUrl + "/games")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE).build();
        this.serverBaseUrl = serverBaseUrl;
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

    public String getPlayerId() {
        return playerId;
    }
    public void sendMap(ClientMap clientMap){
        System.out.println("sendMap() wurde aufgerufen!");
        PlayerHalfMap playerHalfMap = convertMap(playerId, clientMap);

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
    public void sendMove(String playerId, EMove move) {
        try {
            PlayerMove playerMove= PlayerMove.of(getPlayerId(), move);
            // HTTP POST-Anfrage an den Server senden
            Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST)
                    .uri("/" + gameId + "/moves")
                    .body(BodyInserters.fromValue(playerMove))
                    .retrieve()
                    .bodyToMono(ResponseEnvelope.class);

            ResponseEnvelope response = webAccess.block();

            if (response.getState() == messagesbase.messagesfromclient.ERequestState.Error) {
                logger.error("Fehler beim Senden des Moves: {} - {}", response.getExceptionName(), response.getExceptionMessage());
                throw new IllegalStateException("Fehler beim Senden des Moves: " + response.getExceptionMessage());
            }

            logger.info("Move erfolgreich gesendet: {}", move);
            System.out.println("Move wurde erfolgreich verschickt");
        } catch (Exception e) {
            logger.error("Fehler beim Senden des Moves: " + e.getMessage());
            throw new IllegalStateException("Fehler beim Senden des Moves", e);
        }
    }
    public ClientGameState getGameState() throws InterruptedException {

        Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.GET)
                .uri("/" + gameId + "/states/" + playerId)
                .retrieve()
                .bodyToMono(ResponseEnvelope.class);

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
        GameStateConverter converter = new GameStateConverter();

        return converter.convertGameState(currentServerGameState, playerId);
    }


}
