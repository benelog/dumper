Using Dumper
=========

Requirement

    JDK 25 or higher

Download

    wget https://github.com/benelog/dumper/releases/download/v2.0.0/dumper.jar


Execute (Windows, Linux)

    java -jar dumper.jar [port number]


If you don't specify a port number, it will be selected between 10000 and 20000 by random.

A JRE is not enough: Dumper attaches to the other JVMs on the same machine,
so it has to be started with a JDK. The tools.jar of the old JDKs is not needed anymore.

Build

    ./gradlew clean build

The executable jar is created at build/libs/dumper.jar

License

    MIT License. See the LICENSE file for details.
