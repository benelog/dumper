import java.net.InetAddress;
import java.net.UnknownHostException;

import net.benelog.dumper.MonitorServer;
import net.benelog.dumper.RandomNumberGenernator;

/**
 * java -cp "dumper.jar;%JAVA_HOME%/lib/tools.jar" Start
 * @author benelog@gmail.com
 *
 */
public class Start {

	static int randomPortMin = 10000;
	static int randomPortMax = 20000;
	
	public static void main(String[] args) throws Exception {
		
		printUsage();
		int port = selectPort(args);
		printServerAddressInfo(port);
		MonitorServer server = new MonitorServer();
		addShutdownHook(server);
		server.start(port);
	}

	private static int selectPort(String[] args) {
		int port = 0;
		if ((args.length == 0) || (args[0] == null)) {
			System.out.println("The port is selected by ramdom.");
			port = new RandomNumberGenernator().pick(randomPortMin, randomPortMax);
		} else {
			port = Integer.parseInt(args[0]);
		}
		return port;
	}

	private static void printServerAddressInfo(int port)
			throws UnknownHostException {
		InetAddress localhost =InetAddress.getLocalHost();
		String ip = localhost.getHostAddress();
		System.out.println("Web address: http://" + ip +":" + port);
	}

	private static void addShutdownHook(final MonitorServer server) {
		Runtime.getRuntime().addShutdownHook(
			new Thread(){
				public void run(){
					try {
						System.out.println("Server stop");
						server.stop();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		});
	}

	private static void printUsage() {
		System.out.println("-----------------------------");
		System.out.println("Usage:");
		System.out
				.println("Prompt>java -cp \"dumper.jar;%JAVA_HOME%/lib/tools.jar\" Start [port]");
		System.out.println();
		System.out.println("-----------------------------");
		System.out.println();
	}
}
