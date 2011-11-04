package net.benelog.dumper;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.List;

import net.benelog.dumper.JvmAttacher;
import net.benelog.dumper.JvmInfo;

import org.eclipse.jetty.io.WriterOutputStream;
import org.junit.Test;

public class JvmAttacherTest {

	JvmAttacher attacher = new JvmAttacher();
	
	@Test
	public void runningJvmShouldBeMonitored(){
		List<JvmInfo> jps = attacher.getRunningJvms();
		
		assertFalse(jps.isEmpty());
	}
	
	@Test
	public void stackDumpShouldBeCreated() throws Exception {
		//given
		String pid = getOneRunningJavaPid(attacher);
		
		StringWriter dumpWriter = new StringWriter ();
		WriterOutputStream out = new WriterOutputStream(dumpWriter);

		//when
		attacher.createThreadDump(pid, out);
		out.close();
		
		//then
		String dumpContent = dumpWriter.toString();
		assertTrue(dumpContent.contains("JNI global references"));
	}

	private String getOneRunningJavaPid(JvmAttacher attacher) {
		List<JvmInfo> jvmInfos = attacher.getRunningJvms();
		JvmInfo info = jvmInfos.get(0);
		return String.valueOf(info.getProcessId());
	}
}
