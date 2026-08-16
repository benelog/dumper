package net.benelog.dumper.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StopServlet extends HttpServlet {

	public static final String PATH = "/stop";

	private static final long serialVersionUID = 2229123705581054665L;

	private final Runnable shutdownAction;

	public StopServlet(Runnable shutdownAction) {
		this.shutdownAction = shutdownAction;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		response.getWriter().println("Server stopped");
		response.flushBuffer();
		shutdownAction.run();
	}
}
