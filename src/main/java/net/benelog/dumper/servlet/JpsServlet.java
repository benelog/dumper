package net.benelog.dumper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.benelog.dumper.JvmAttacher;
import net.benelog.dumper.JvmInfo;

/**
 * @author benelog@gmail.com
 *
 */
public class JpsServlet extends HttpServlet {

	private static final long serialVersionUID = 5827074916926280433L;
	JvmAttacher monitor = new JvmAttacher();

	public JpsServlet(){
		super();
	}
	
	public JpsServlet(JvmAttacher monitor){
		super();
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
		out.println("<h1>Java processes</h1>");

		out.println("<table>");
		out.println("<tr>");
		out.println("<th>pid</th>");
		out.println("<th>Main class</th>");
		out.println("<th>Main args</th>");
		out.println("<th>VM args</th>");
		out.println("<th>VM flags</th>");
		out.println("</tr>");
		for (JvmInfo info : jvmInfos) {
			out.println("<tr>");
			out.println("<td>");
			out.println("<a href='/jstack?pid=" + info.getProcessId() +"'>");			
			out.println(info.getProcessId());
			out.println("</a>");
			out.println("</td>");
			out.println("<td>");
			out.println(info.getMainClass());
			out.println("</td>");
			out.println("<td>");
			out.println(info.getMainArguments());
			out.println("</td>");
			out.println("<td>");
			out.println(info.getJvmArguments());
			out.println("</td>");
			out.println("<td>");
			out.println(info.getJvmFlags());
			out.println("</td>");
			out.println("</tr>");
		}
		out.println("</table>");
		out.println("<p><a href='/stop'>stop</a></p>");
	}
}
