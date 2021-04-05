// run with java -Djava.rmi.server.hostname=<ip address> FatorialServer

import java.rmi.Naming;
import java.rmi.RemoteException;

public class FatorialServer {

	public static void main (String[] args) {
		try {
			java.rmi.registry.LocateRegistry.createRegistry(1099);
			System.out.println("RMI registry ready.");
		} catch (RemoteException e) {
			System.out.println("RMI registry already running.");
		}
		try {
			Naming.rebind ("FatorialChuchu", new Fatorial());
			System.out.println ("Server is ready.");
		} catch (Exception e) {
			System.out.println ("Server failed:");
			e.printStackTrace();
		}
	}

}
