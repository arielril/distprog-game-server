import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;


public interface IGame extends Remote {
  public int register() throws RemoteException, ServerNotActiveException;

  public int play(int playerId) throws RemoteException;

  public int giveUp(int playerId) throws RemoteException;

  public int stop(int playerId) throws RemoteException;
}
