package net.benelog.dumper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.benelog.dumper.JvmAttacher;

public class JstackServlet extends HttpServlet {

	private static final long serialVersionUID = 7770547840512828314L;
	JvmAttacher monitor = new JvmAttacher();

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String pid = request.getParameter("pid");
		if (pid == null) {
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			PrintWriter out = response.getWriter();
			out.println("<h1>Cannot find the 'pid' parameter</h1>");
			return;
		}
		String filename = getFileName(pid);
		response.addHeader("Content-Type", "application/octet-stream");
		response.addHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletOutputStream output = response.getOutputStream();
		monitor.createThreadDump(pid, output);
		output.close();
	}

	private String getFileName(String pid) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MMdd-HHmmss");
		String timestamp = formatter.format(new Date());
		String filename = pid + "_" + timestamp + ".log";
		return filename;
	}

}
