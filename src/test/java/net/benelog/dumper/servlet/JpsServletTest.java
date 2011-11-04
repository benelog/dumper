package net.benelog.dumper.servlet;

import static org.mockito.BDDMockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import net.benelog.dumper.JvmAttacher;
import net.benelog.dumper.JvmInfo;

import org.apache.commons.io.FileUtils;
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
		String fileName = "target/jps_empty.html";
		write(content, fileName);
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
		List<JvmInfo> monitored = Arrays.asList(info);
		given(monitor.getRunningJvms()).willReturn(monitored);

		//when
		servlet.doGet(request, response);

		//then
		String content = response.getContentAsString();
		String fileName = "target/jps_pid1.html";
		write(content, fileName);
	}

	private void write(String html, String fileName) throws IOException {
		System.out.println(html);
		File htmlFile = new File(fileName);
		FileUtils.writeStringToFile(htmlFile, html);
	}
}
