all:			FatorialClient.class FatorialServer.class \
			Fatorial.class FatorialInterface.class

Fatorial.class:		Fatorial.java FatorialInterface.class
			@javac Fatorial.java

FatorialInterface.class:	FatorialInterface.java
			@javac FatorialInterface.java

FatorialClient.class:	FatorialClient.java
			@javac FatorialClient.java

FatorialServer.class:	FatorialServer.java
			@javac FatorialServer.java

clean:
			@rm -f *.class *~
