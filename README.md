Using dumder
=========

Download

    wget benelog.net/dumper.jar
  

Execute (Windows)

    java -cp "dumper.jar;%JAVA_HOME%/lib/tools.jar" Start [port number]

Execute (Linux)

    java -cp dumper.jar:$JAVA_HOME/lib/tools.jar Start [port number]


If you not specify a port nubmer, it will be selected between 1000 and 2000 by random.