import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdditionInterfaceServer extends Remote {
	public int Add(int a, int b, int port) throws RemoteException;
}
