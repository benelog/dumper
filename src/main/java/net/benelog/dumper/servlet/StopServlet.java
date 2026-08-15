package net.benelog.dumper.servlet;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class StopServlet extends HttpServlet {

	private static final long serialVersionUID = 2229123705581054665L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.exit(0);
	}
}
