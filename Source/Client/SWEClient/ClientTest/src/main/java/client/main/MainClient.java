package client.main;

import controller.Controller;
import network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.ResponseEnvelope;
import messagesbase.messagesfromclient.ERequestState;
import messagesbase.messagesfromserver.GameState;
import reactor.core.publisher.Mono;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class MainClient {
	private static Logger logger = LoggerFactory.getLogger(MainClient.class);
	public static void main(String[] args) {
		//System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

		if (args.length < 3) {
			logger.error("Invalid command line arguments.");
			System.err.println(
					"Invalid command line arguments. Usage: java -jar <FileNameClient.jar> <Modus> <BasisUrlServer> <GameId>");
			System.exit(1);
		}

		String serverBaseUrl = args[1];
		String gameId = args[2];

		Network network = new Network(serverBaseUrl, gameId);
		Controller controller = new Controller(network);

		controller.run("malaz","ali", "malaza97");
	}
}
