all:			HelloClient.class HelloServer.class \
			Hello.class HelloInterface.class

Hello.class:		Hello.java HelloInterface.class
			@javac Hello.java

HelloInterface.class:	HelloInterface.java
			@javac HelloInterface.java

HelloClient.class:	HelloClient.java
			@javac HelloClient.java

HelloServer.class:	HelloServer.java
			@javac HelloServer.java

run:			all
			@java HelloServer &
			@sleep 1
			@java HelloClient

clean:
			@rm -f *.class *~

info:
			@echo "(c) Roland Teodorowitsch (08 abr. 2015)"

