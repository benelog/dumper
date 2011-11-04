package net.benelog.dumper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.tools.attach.HotSpotVirtualMachine;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

/**
 * @author benelog@gmail.com
 *
 */
public class JvmAttacher {

	public List<JvmInfo> getRunningJvms() {
		List<JvmInfo> jvmInfos = new ArrayList<JvmInfo>();
		MonitoredHost monitoredHost = getHost();

		Set<Integer> processIds = getActiveJavaPid(monitoredHost);

		for (Integer pid : processIds) {
			try {
				MonitoredVm vm = getMonitoredVm(monitoredHost, pid);
				JvmInfo info = createJvmInfo(pid, vm);
				jvmInfos.add(info);
				monitoredHost.detach(vm);
			} catch (MonitorException e) {
				throwsFailToMonitor(pid, e);
			} catch (URISyntaxException e) {
				throwsFailToMonitor(pid, e);
			}
		}
		return jvmInfos;
	}

	private void throwsFailToMonitor(Integer pid, Exception e) {
		throw new IllegalStateException("fail to monitor pid " + pid, e);
	}

	@SuppressWarnings("unchecked")
	private Set<Integer> getActiveJavaPid(MonitoredHost monitoredHost) {
		Set<Integer> processIds = null;
		try {
			processIds = monitoredHost.activeVms();
		} catch (MonitorException e) {
			throw new IllegalStateException("fail to get pid", e);
		}
		return processIds;
	}

	private MonitoredHost getHost() {
		MonitoredHost monitoredHost = null;
		try {
			HostIdentifier hostId = new HostIdentifier("localhost");
			monitoredHost = MonitoredHost.getMonitoredHost(hostId);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("fail to indentify localhost", e);
		} catch (MonitorException e) {
			throw new IllegalStateException("fail to monitor localhost", e);
		}

		return monitoredHost;
	}

	private JvmInfo createJvmInfo(Integer pid, MonitoredVm vm) throws MonitorException {
		JvmInfo info = new JvmInfo();
		info.setProcessId(pid);
		info.setMainClass(MonitoredVmUtil.mainClass(vm, true));
		info.setMainArguments(MonitoredVmUtil.mainArgs(vm));
		info.setJvmArguments(MonitoredVmUtil.jvmArgs(vm));
		info.setJvmFlags(MonitoredVmUtil.jvmFlags(vm));
		return info;
	}

	private MonitoredVm getMonitoredVm(MonitoredHost monitoredHost, Integer pid) throws URISyntaxException, MonitorException {
		String vmId = "//" + pid + "?mode=r";
		VmIdentifier id = new VmIdentifier(vmId);
		return monitoredHost.getMonitoredVm(id, 0);

	}

	public void createThreadDump(String pid, OutputStream out) throws IOException {
		VirtualMachine vm;
		try {
			vm = VirtualMachine.attach(pid);
		} catch (AttachNotSupportedException e) {
			throw new IllegalStateException("fail attach pid:" + pid, e);
		}
		transferDump(vm, out);
		vm.detach();
	}

	private void transferDump(VirtualMachine vm, OutputStream out) throws IOException {
		InputStream in = null;
		try {
			in = ((HotSpotVirtualMachine)vm).remoteDataDump(new Object[0]);
			byte buffer[] = new byte[256];
			int n;
			do {
				n = in.read(buffer);
				if (n > 0) {
					out.write(buffer, 0, n);
				}
			} while (n > 0);
		} catch (Exception e) {
			throw new IllegalStateException("fail to transfer dump", e);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

}
