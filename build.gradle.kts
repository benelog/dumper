plugins {
	java
	id("com.gradleup.shadow") version "9.6.1"
}

group = "net.benelog"
version = "2.0.0"

val jdkVersion = 25
val jettyVersion = "12.1.12"
val springVersion = "7.0.8"
val mainClassName = "Start"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.eclipse.jetty:jetty-server:$jettyVersion")
	implementation("org.eclipse.jetty.ee11:jetty-ee11-servlet:$jettyVersion")
	implementation("org.slf4j:slf4j-simple:2.0.17")

	testImplementation("junit:junit:4.13.2")
	testImplementation("org.mockito:mockito-core:5.23.0")
	testImplementation("org.springframework:spring-test:$springVersion")
	testImplementation("org.springframework:spring-web:$springVersion")
}

tasks.withType<JavaCompile>().configureEach {
	options.release = jdkVersion
	options.encoding = "UTF-8"
}

tasks.test {
	useJUnit()
}

tasks.shadowJar {
	archiveFileName = "dumper.jar"
	duplicatesStrategy = DuplicatesStrategy.INCLUDE
	manifest {
		attributes("Main-Class" to mainClassName)
	}
	mergeServiceFiles()
	exclude("module-info.class")
	exclude("META-INF/versions/*/module-info.class")
	exclude("META-INF/*.SF")
	exclude("META-INF/*.DSA")
	exclude("META-INF/*.RSA")
}
