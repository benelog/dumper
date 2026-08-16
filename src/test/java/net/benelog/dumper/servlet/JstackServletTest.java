package net.benelog.dumper.servlet;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;

import jakarta.servlet.ServletException;

import net.benelog.dumper.JvmAttacher;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class JstackServletTest {
	JvmAttacher monitor = mock(JvmAttacher.class);
	JstackServlet servlet = new JstackServlet(monitor);
	MockHttpServletRequest request = new MockHttpServletRequest();
	MockHttpServletResponse response = new MockHttpServletResponse();

	@Test
	public void missingPidShouldBeRejected() throws ServletException, IOException {

		//when
		servlet.doGet(request, response);

		//then
		assertEquals(400, response.getStatus());
	}

	@Test
	public void nonNumericPidShouldBeRejected() throws ServletException, IOException {
		//given
		request.setParameter("pid", "abc");

		//when
		servlet.doGet(request, response);

		//then
		assertEquals(400, response.getStatus());
	}

	@Test
	public void negativePidShouldBeRejected() throws ServletException, IOException {
		//given
		request.setParameter("pid", "-1");

		//when
		servlet.doGet(request, response);

		//then
		assertEquals(400, response.getStatus());
	}

	@Test
	public void unattachablePidShouldBeReportedWithoutDownloadHeaders() throws ServletException, IOException {
		//given
		request.setParameter("pid", "99999");
		given(monitor.createThreadDump(99999L)).willThrow(new IllegalStateException("fail attach"));

		//when
		servlet.doGet(request, response);

		//then
		assertEquals(404, response.getStatus());
		assertNull(response.getHeader("Content-Disposition"));
	}

	@Test
	public void threadDumpShouldBeDownloaded() throws ServletException, IOException {
		//given
		request.setParameter("pid", "1");
		given(monitor.createThreadDump(1L)).willReturn("Full thread dump");

		//when
		servlet.doGet(request, response);

		//then
		assertEquals(200, response.getStatus());
		assertEquals("Full thread dump", response.getContentAsString());
		assertTrue(response.getHeader("Content-Disposition").startsWith("attachment; filename=1_"));
	}
}
