all:
	rpcgen addsub.x
	gcc server.c addsub_svc.c  addsub_xdr.c -o server -lnsl
	gcc client.c addsub_clnt.c addsub_xdr.c -o client -lnsl

clean:
	rm -f client server addsub_svc.c addsub_clnt.c addsub_xdr.c addsub.h

