package net.benelog.dumper.servlet;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;

import net.benelog.dumper.JvmAttacher;
import net.benelog.dumper.JvmInfo;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


public class JpsServletTest {
	JvmAttacher monitor = mock(JvmAttacher.class);
	JpsServlet servlet = new JpsServlet(monitor);
	MockHttpServletRequest request = new MockHttpServletRequest();
	MockHttpServletResponse response = new MockHttpServletResponse();

	@Test
	public void noActiveVms() throws ServletException, IOException {

		//when
		servlet.doGet(request, response);

		//then
		String content = response.getContentAsString();
		assertTrue(content.contains("<h1>Java processes</h1>"));
		assertFalse(content.contains("<a href='" + JstackServlet.PATH));
	}


	@Test
	public void oneActiveVms() throws ServletException, IOException {
		//given
		JvmInfo info = new JvmInfo();
		info.setProcessId(1);
		info.setJvmArguments("-Xmx1024m");
		info.setMainClass("Start");
		info.setJvmFlags("-server");
		info.setMainArguments("8080");
		given(monitor.getRunningJvms()).willReturn(List.of(info));

		//when
		servlet.doGet(request, response);

		//then
		String content = response.getContentAsString();
		assertTrue(content.contains("<a href='" + JstackServlet.PATH + "?pid=1'>1</a>"));
		assertTrue(content.contains("<td>Start</td>"));
		assertTrue(content.contains("<td>8080</td>"));
		assertTrue(content.contains("<td>-Xmx1024m</td>"));
		assertTrue(content.contains("<td>-server</td>"));
	}

	@Test
	public void markupInProcessInfoShouldBeEscaped() throws ServletException, IOException {
		//given
		JvmInfo info = new JvmInfo();
		info.setProcessId(1);
		info.setMainClass("Evil");
		info.setMainArguments("<img src=x onerror=fetch('/stop')>");
		info.setJvmArguments("");
		info.setJvmFlags("");
		given(monitor.getRunningJvms()).willReturn(List.of(info));

		//when
		servlet.doGet(request, response);

		//then
		String content = response.getContentAsString();
		assertFalse(content.contains("<img"));
		assertTrue(content.contains("&lt;img src=x onerror=fetch(&#39;/stop&#39;)&gt;"));
	}
}
