package net.benelog.dumper;

import net.benelog.dumper.servlet.JpsServlet;
import net.benelog.dumper.servlet.JstackServlet;
import net.benelog.dumper.servlet.StopServlet;

import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.ee11.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;

public class MonitorServer {

	Server server;
	public void start(int port) throws Exception, InterruptedException {
		server = new Server(port);
		ServletContextHandler context = new ServletContextHandler(	ServletContextHandler.NO_SESSIONS);
		context.setContextPath("/");
		context.addServlet(new ServletHolder(new JpsServlet()),"/");
		context.addServlet(new ServletHolder(new JstackServlet()),"/jstack");		
		context.addServlet(new ServletHolder(new StopServlet()),"/stop");		
		server.setHandler(context);
		server.start();
		server.join();
	}
	
	public void stop() throws Exception{
		server.stop();
	}
}
