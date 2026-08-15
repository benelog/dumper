package net.benelog.dumper;

/**
 * A target JVM which is attached by {@link JvmAttacherTest}.
 * It stops by itself when the test fails to destroy it.
 */
public class SleepingJvm {
	public static void main(String[] args) throws Exception {
		Thread.sleep(60000);
	}
}
