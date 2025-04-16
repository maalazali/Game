package client.main;

import controller.Controller;
import network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pathFinder.FindPath;
import pathFinder.PathManager;

public class MainClient {
	private static Logger logger = LoggerFactory.getLogger(MainClient.class);

	public static void main(String[] args) {

		logger.info("Starting client application...");

		if (args.length < 3) {
			logger.error("Invalid command line arguments.");
			System.err.println(
					"Invalid command line arguments. Usage: java -jar <FileNameClient.jar> <Modus> <BasisUrlServer> <GameId>");
			System.exit(1);
		}

		String serverBaseUrl = args[1];
		String gameId = args[2];

		String firstName = "firstName";
		String lastName = "lastName";
		String uAccount = "uAccount";


		PathManager pathManager = new PathManager();
		Network network = new Network(serverBaseUrl, gameId);
		Controller controller = new Controller(network, pathManager);

		controller.run("malaz", "ali", "malaza97");

	}

}
























