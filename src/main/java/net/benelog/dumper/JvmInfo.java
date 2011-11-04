package net.benelog.dumper;

public class JvmInfo {
	private int processId;
	private String mainClass;
	private String mainArguments;
	private String jvmArguments;
	private String jvmFlags;
	public int getProcessId() {
		return processId;
	}
	public void setProcessId(int processId) {
		this.processId = processId;
	}
	public String getMainClass() {
		return mainClass;
	}
	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}
	public String getMainArguments() {
		return mainArguments;
	}
	public void setMainArguments(String mainArguments) {
		this.mainArguments = mainArguments;
	}
	public String getJvmArguments() {
		return jvmArguments;
	}
	public void setJvmArguments(String jvmArguments) {
		this.jvmArguments = jvmArguments;
	}
	public String getJvmFlags() {
		return jvmFlags;
	}
	public void setJvmFlags(String jvmFlags) {
		this.jvmFlags = jvmFlags;
	}
	
	@Override
	public String toString(){
		return String.format("%d %s", processId, mainClass);
	}
}
