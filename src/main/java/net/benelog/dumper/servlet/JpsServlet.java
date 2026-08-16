package net.benelog.dumper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.benelog.dumper.JvmAttacher;
import net.benelog.dumper.JvmInfo;

/**
 * @author benelog@gmail.com
 *
 */
public class JpsServlet extends HttpServlet {

	public static final String PATH = "/";

	private static final long serialVersionUID = 5827074916926280433L;

	private final JvmAttacher monitor;

	public JpsServlet(JvmAttacher monitor) {
		this.monitor = monitor;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		List<JvmInfo> jvmInfos = monitor.getRunningJvms();
		print(out, jvmInfos);
	}

	private void print(PrintWriter out, List<JvmInfo> jvmInfos) {
		out.println("""
				<h1>Java processes</h1>
				<table>
				<tr>
				<th>pid</th>
				<th>Main class</th>
				<th>Main args</th>
				<th>VM args</th>
				<th>VM flags</th>
				</tr>""");
		for (JvmInfo info : jvmInfos) {
			String pid = Integer.toString(info.getProcessId());
			out.printf("""
					<tr>
					<td><a href='%s?pid=%s'>%s</a></td>
					<td>%s</td>
					<td>%s</td>
					<td>%s</td>
					<td>%s</td>
					</tr>%n""",
					JstackServlet.PATH, pid, pid,
					escape(info.getMainClass()), escape(info.getMainArguments()),
					escape(info.getJvmArguments()), escape(info.getJvmFlags()));
		}
		out.println("</table>");
		out.println("<p><a href='" + StopServlet.PATH + "'>stop</a></p>");
	}

	/**
	 * The listed values come from other processes' command lines, so they
	 * must not reach the page as markup.
	 */
	private String escape(String text) {
		if (text == null) {
			return "";
		}
		return text.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}
}
