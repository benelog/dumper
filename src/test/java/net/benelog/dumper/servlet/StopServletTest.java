package net.benelog.dumper.servlet;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.servlet.ServletException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class StopServletTest {
	AtomicInteger called = new AtomicInteger();
	MockHttpServletRequest request = new MockHttpServletRequest();
	MockHttpServletResponse response = new MockHttpServletResponse();
	StopServlet servlet = new StopServlet(() -> {
		assertTrue("the response should be flushed before the shutdown action runs", response.isCommitted());
		called.incrementAndGet();
	});

	@Test
	public void shutdownActionShouldRunOnceAfterResponse() throws ServletException, IOException {

		//when
		servlet.doGet(request, response);

		//then
		assertEquals("Server stopped", response.getContentAsString().trim());
		assertEquals(1, called.get());
	}
}
