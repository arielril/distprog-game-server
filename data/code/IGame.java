import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IGame extends Remote {
  public int register() throws RemoteException;

  public int play(int playerId) throws RemoteException;

  public int giveUp(int playerId) throws RemoteException;

  public int stop(int playerId) throws RemoteException;
}
