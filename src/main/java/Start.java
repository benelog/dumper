import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

import net.benelog.dumper.MonitorServer;

/**
 * java -jar dumper.jar [port]
 *
 * It requires JDK 25 or higher.
 *
 * @author benelog@gmail.com
 *
 */
public class Start {

	private static final int RANDOM_PORT_MIN = 10000;
	private static final int RANDOM_PORT_MAX = 20000;

	public static void main(String[] args) throws Exception {

		printUsage();
		int port = selectPort(args);
		MonitorServer server = new MonitorServer();
		server.start(port);
		printServerAddressInfo(port);
		server.join();
	}

	private static int selectPort(String[] args) {
		if (args.length == 0) {
			System.out.println("The port is selected by random.");
			return ThreadLocalRandom.current().nextInt(RANDOM_PORT_MIN, RANDOM_PORT_MAX);
		}
		return Integer.parseInt(args[0]);
	}

	private static void printServerAddressInfo(int port) {
		String host;
		try {
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			host = "localhost";
		}
		System.out.println("Web address: http://" + host + ":" + port);
	}

	private static void printUsage() {
		System.out.println("-----------------------------");
		System.out.println("Usage:");
		System.out.println("   Prompt>java -jar dumper.jar [port]");
		System.out.println(" (It requires JDK 25 or higher.)");

		System.out.println();
		System.out.println("-----------------------------");
		System.out.println();
	}
}
