package net.benelog.dumper;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * Collects information of the running JVMs and creates thread dumps.
 * It only uses the public API of the jdk.attach module, so no tools.jar
 * and no --add-exports option is needed.
 *
 * @author benelog@gmail.com
 *
 */
public class JvmAttacher {

	private static final String DIAGNOSTIC_COMMAND_MBEAN = "com.sun.management:type=DiagnosticCommand";
	private static final String THREAD_PRINT_OPERATION = "threadPrint";

	public List<JvmInfo> getRunningJvms() {
		List<JvmInfo> jvmInfos = new ArrayList<JvmInfo>();
		for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
			jvmInfos.add(createJvmInfo(descriptor));
		}
		return jvmInfos;
	}

	private JvmInfo createJvmInfo(VirtualMachineDescriptor descriptor) {
		JvmInfo info = new JvmInfo();
		info.setProcessId(Integer.parseInt(descriptor.id()));
		if (isCurrentProcess(descriptor.id())) {
			fillWithCurrentProcess(info);
		} else if (!fillWithAgentProperties(info, descriptor)) {
			fillWithDisplayName(info, descriptor.displayName());
		}
		return info;
	}

	private boolean isCurrentProcess(String pid) {
		return String.valueOf(ProcessHandle.current().pid()).equals(pid.trim());
	}

	private void fillWithCurrentProcess(JvmInfo info) {
		fillWithCommand(info, System.getProperty("sun.java.command", ""));
		info.setJvmArguments(String.join(" ", ManagementFactory.getRuntimeMXBean().getInputArguments()));
		info.setJvmFlags("");
	}

	/**
	 * The attachment can fail when the target JVM is owned by another user
	 * or does not allow to be attached. In that case the caller falls back
	 * to the information of the descriptor.
	 */
	private boolean fillWithAgentProperties(JvmInfo info, VirtualMachineDescriptor descriptor) {
		try {
			VirtualMachine vm = VirtualMachine.attach(descriptor);
			try {
				Properties agentProperties = vm.getAgentProperties();
				fillWithCommand(info, agentProperties.getProperty("sun.java.command", ""));
				info.setJvmArguments(agentProperties.getProperty("sun.jvm.args", ""));
				info.setJvmFlags(agentProperties.getProperty("sun.jvm.flags", ""));
			} finally {
				vm.detach();
			}
			return true;
		} catch (AttachNotSupportedException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	private void fillWithDisplayName(JvmInfo info, String displayName) {
		fillWithCommand(info, displayName == null ? "" : displayName);
		info.setJvmArguments("");
		info.setJvmFlags("");
	}

	/**
	 * 'sun.java.command' holds the main class and its arguments in one line.
	 */
	private void fillWithCommand(JvmInfo info, String command) {
		int separator = command.indexOf(' ');
		if (separator < 0) {
			info.setMainClass(command);
			info.setMainArguments("");
		} else {
			info.setMainClass(command.substring(0, separator));
			info.setMainArguments(command.substring(separator + 1));
		}
	}

	public void createThreadDump(String pid, OutputStream out) throws IOException {
		String dump = isCurrentProcess(pid) ? readOwnThreadDump() : readThreadDump(pid);
		out.write(dump.getBytes(StandardCharsets.UTF_8));
		out.flush();
	}

	/**
	 * A JVM cannot attach to itself unless it is started with
	 * -Djdk.attach.allowAttachSelf=true, so its own MBean server is used.
	 */
	private String readOwnThreadDump() throws IOException {
		return invokeThreadPrint(ManagementFactory.getPlatformMBeanServer());
	}

	private String readThreadDump(String pid) throws IOException {
		VirtualMachine vm;
		try {
			vm = VirtualMachine.attach(pid);
		} catch (AttachNotSupportedException e) {
			throw new IllegalStateException("fail attach pid:" + pid, e);
		}
		try {
			JMXServiceURL url = new JMXServiceURL(vm.startLocalManagementAgent());
			JMXConnector connector = JMXConnectorFactory.connect(url);
			try {
				return invokeThreadPrint(connector.getMBeanServerConnection());
			} finally {
				connector.close();
			}
		} finally {
			vm.detach();
		}
	}

	/**
	 * Runs the 'Thread.print' diagnostic command of the target JVM,
	 * which produces the same output as jstack.
	 */
	private String invokeThreadPrint(MBeanServerConnection connection) throws IOException {
		try {
			return (String) connection.invoke(new ObjectName(DIAGNOSTIC_COMMAND_MBEAN),
					THREAD_PRINT_OPERATION,
					new Object[] { new String[] { "-l" } },
					new String[] { String[].class.getName() });
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException("fail to create thread dump", e);
		}
	}
}
