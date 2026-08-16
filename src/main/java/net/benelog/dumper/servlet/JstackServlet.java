package net.benelog.dumper.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.benelog.dumper.JvmAttacher;

public class JstackServlet extends HttpServlet {

	public static final String PATH = "/jstack";

	private static final long serialVersionUID = 7770547840512828314L;
	private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MMdd-HHmmss");

	private final JvmAttacher monitor;

	public JstackServlet() {
		this(new JvmAttacher());
	}

	public JstackServlet(JvmAttacher monitor) {
		this.monitor = monitor;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String pid = request.getParameter("pid");
		if (pid == null || !pid.matches("\\d+")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The 'pid' parameter is missing or not a number");
			return;
		}
		response.setContentType("application/octet-stream");
		response.addHeader("Content-Disposition", "attachment; filename=" + getFileName(pid));
		ServletOutputStream output = response.getOutputStream();
		output.write(monitor.createThreadDump(pid).getBytes(StandardCharsets.UTF_8));
		output.flush();
	}

	private String getFileName(String pid) {
		return pid + "_" + TIMESTAMP_FORMAT.format(LocalDateTime.now()) + ".log";
	}
}
