all:		servidor cliente

cliente:	cliente.c const.h
		@gcc -o cliente cliente.c

servidor:	servidor.c const.h
		@gcc -o servidor servidor.c

clean:
		@rm -f cliente servidor *~

