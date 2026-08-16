package net.benelog.dumper;

import net.benelog.dumper.servlet.JpsServlet;
import net.benelog.dumper.servlet.JstackServlet;
import net.benelog.dumper.servlet.StopServlet;

import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.ee11.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.GracefulHandler;

public class MonitorServer {

	private static final long STOP_TIMEOUT_MILLIS = 2000;

	private volatile Server server;

	public void start(int port) throws Exception {
		Server newServer = new Server(port);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		context.setContextPath("/");
		JvmAttacher attacher = new JvmAttacher();
		context.addServlet(new ServletHolder(new JpsServlet(attacher)), JpsServlet.PATH);
		context.addServlet(new ServletHolder(new JstackServlet(attacher)), JstackServlet.PATH);
		context.addServlet(new ServletHolder(new StopServlet(this::stopInBackground)), StopServlet.PATH);
		GracefulHandler graceful = new GracefulHandler();
		graceful.setHandler(context);
		newServer.setHandler(graceful);
		newServer.setStopTimeout(STOP_TIMEOUT_MILLIS);
		newServer.setStopAtShutdown(true);
		server = newServer;
		newServer.start();
	}

	public void join() throws InterruptedException {
		Server current = server;
		if (current != null) {
			current.join();
		}
	}

	public void stop() throws Exception {
		Server current = server;
		if (current != null) {
			current.stop();
		}
	}

	/**
	 * Stopping the server from one of its own request threads would deadlock,
	 * so the stop runs on a separate thread; the graceful stop timeout lets
	 * the in-flight /stop response complete first. If the graceful stop fails,
	 * the process is halted so /stop always terminates the server.
	 */
	private void stopInBackground() {
		new Thread(() -> {
			try {
				stop();
			} catch (Exception e) {
				e.printStackTrace();
				Runtime.getRuntime().halt(1);
			}
		}, "monitor-server-stopper").start();
	}
}
