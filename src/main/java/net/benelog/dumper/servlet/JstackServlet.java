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

	public JstackServlet(JvmAttacher monitor) {
		this.monitor = monitor;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long pid = parsePid(request.getParameter("pid"));
		if (pid <= 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The 'pid' parameter is missing or not a positive number");
			return;
		}
		String dump;
		try {
			dump = monitor.createThreadDump(pid);
		} catch (IllegalStateException | IOException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Cannot create a thread dump of the JVM " + pid);
			return;
		}
		response.setContentType("application/octet-stream");
		response.addHeader("Content-Disposition", "attachment; filename=" + getFileName(pid));
		ServletOutputStream output = response.getOutputStream();
		output.write(dump.getBytes(StandardCharsets.UTF_8));
		output.flush();
	}

	private long parsePid(String parameter) {
		try {
			return Long.parseLong(parameter);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private String getFileName(long pid) {
		return pid + "_" + TIMESTAMP_FORMAT.format(LocalDateTime.now()) + ".log";
	}
}
