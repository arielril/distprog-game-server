import java.rmi.Remote;
import java.rmi.RemoteException;

// Interface remota para o exemplo "Hello, world!"
public interface HelloInterface extends Remote {
	// Metodo invocavel remotamente que retorna a mensagem do objeto remoto
	public String say() throws RemoteException;
}

