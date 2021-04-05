import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FatorialInterface extends Remote {
	public int obtemFatorial(String valor) throws RemoteException;
}

