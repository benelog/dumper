package net.benelog.dumper;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.sun.tools.attach.VirtualMachine;

public class JvmAttacherTest {

	JvmAttacher attacher = new JvmAttacher();

	@Test
	public void runningJvmShouldBeMonitored(){
		List<JvmInfo> jps = attacher.getRunningJvms();

		assertFalse(jps.isEmpty());
	}

	@Test
	public void stackDumpOfItselfShouldBeCreated() throws Exception {
		String dumpContent = attacher.createThreadDump(ProcessHandle.current().pid());

		assertTrue(dumpContent.contains("Full thread dump"));
	}

	@Test
	public void stackDumpOfAnotherJvmShouldBeCreated() throws Exception {
		Process target = startAnotherJvm();
		try {
			String dumpContent = attacher.createThreadDump(target.pid());

			assertTrue(dumpContent.contains("Full thread dump"));
		} finally {
			target.destroyForcibly();
		}
	}

	private Process startAnotherJvm() throws Exception {
		String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		Process process = new ProcessBuilder(java, "-cp", System.getProperty("java.class.path"),
				SleepingJvm.class.getName()).start();
		waitUntilAttachable(process);
		return process;
	}

	/**
	 * The attach mechanism of the target JVM is not ready right after the start.
	 */
	private void waitUntilAttachable(Process process) throws Exception {
		String pid = String.valueOf(process.pid());
		for (int i = 0; i < 50; i++) {
			if (VirtualMachine.list().stream().anyMatch(descriptor -> pid.equals(descriptor.id()))) {
				return;
			}
			Thread.sleep(200);
		}
		fail("the JVM " + pid + " is not started");
	}
}
