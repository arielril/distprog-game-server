import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPlayer extends Remote {
  public void start() throws RemoteException;
  
  public void bonus() throws RemoteException;

  public void check() throws RemoteException;

  public String getServerIp() throws RemoteException;

  public int getId() throws RemoteException;
}
