Using dumper
=========

Download

    wget benelog.net/dumper.jar
  

Execute (Windows)

    java -cp "dumper.jar;%JAVA_HOME%/lib/tools.jar" Start [port number]

Execute (Linux)

    java -cp dumper.jar:$JAVA_HOME/lib/tools.jar Start [port number]


If you don't specify a port nubmer, it will be selected between 10000 and 20000 by random.