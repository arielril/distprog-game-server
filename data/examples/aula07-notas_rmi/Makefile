all:			NotasClient.class NotasServer.class \
			Notas.class NotasInterface.class

Notas.class:		Notas.java NotasInterface.class
			@javac Notas.java

NotasInterface.class:	NotasInterface.java
			@javac NotasInterface.java

NotasClient.class:	NotasClient.java
			@javac NotasClient.java

NotasServer.class:	NotasServer.java
			@javac NotasServer.java

run:			all
			@java NotasServer &
			@sleep 1
			@java NotasClient

clean:
			@rm -f *.class *~

info:
			@echo "(c) Roland Teodorowitsch (08 abr. 2015)"

