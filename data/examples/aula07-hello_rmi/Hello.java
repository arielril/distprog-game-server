import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// Classe remota para o exemplo "Hello, world!"
public class Hello extends UnicastRemoteObject implements HelloInterface {
	private static final long serialVersionUID = 7896795898928782846L;
	private String message;

	// Constroi um objeto remoto armazenando nele o String recebido
	public Hello (String msg) throws RemoteException {
		message = msg;
	}

	// Implementa o metodo invocavel remotamente, que retorna a mensagem armazenada no objeto
	public String say() throws RemoteException {
		return message;
	}
}

