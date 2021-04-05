import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Fatorial extends UnicastRemoteObject implements FatorialInterface {
	public Fatorial() throws RemoteException {
	}

	public int obtemFatorial(String valor) throws RemoteException {
		int i, val;
		
		val = Integer.parseInt(valor);
		
		for (i = val - 1; i > 0; i--)
			val *= i;
			
		return val;
	}
}
