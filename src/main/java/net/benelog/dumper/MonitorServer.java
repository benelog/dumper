package net.benelog.dumper;

import net.benelog.dumper.servlet.JpsServlet;
import net.benelog.dumper.servlet.JstackServlet;
import net.benelog.dumper.servlet.StopServlet;

import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.ee11.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;

public class MonitorServer {

	private Server server;

	public void start(int port) throws Exception {
		server = new Server(port);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		context.setContextPath("/");
		JvmAttacher attacher = new JvmAttacher();
		context.addServlet(new ServletHolder(new JpsServlet(attacher)), "/");
		context.addServlet(new ServletHolder(new JstackServlet(attacher)), JstackServlet.PATH);
		context.addServlet(new ServletHolder(new StopServlet(this::stopInBackground)), StopServlet.PATH);
		server.setHandler(context);
		server.start();
	}

	public void join() throws InterruptedException {
		server.join();
	}

	public void stop() throws Exception {
		server.stop();
	}

	/**
	 * Stopping the server from one of its own request threads would deadlock,
	 * so the stop runs on a separate thread after the response is delivered.
	 */
	private void stopInBackground() {
		new Thread(() -> {
			try {
				stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
}
