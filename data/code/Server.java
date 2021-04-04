import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements IGame {
  private static volatile String remoteHostName;

  private static final int REGISTRY_PORT = 52369;

  public Server() throws RemoteException {}

  public int register() throws RemoteException {
    return 1;
  }

  public int play(int playerId) throws RemoteException {
    return 1;
  }

  public int giveUp(int playerId) throws RemoteException {
    return 1;
  }

  public int stop(int playerId) throws RemoteException {
    return 1;
  }

  public static void main(String[] args) throws RemoteException {
    if (args.length != 1) {
      System.out.println("Usage: java Server <server ip>");
      System.exit(1);
    }

    try {
      System.setProperty("java.rmi.server.hostname", args[0]);
      LocateRegistry.createRegistry(REGISTRY_PORT);
      System.out.println("java RMI registered");
    } catch (RemoteException e) {
      System.out.println("duplicated java RMI registry");
    }

    try {
      String server = String.format("rmi://%s:%d/server_if", args[0], REGISTRY_PORT);
      Naming.rebind(server, new Server());
      System.out.println("Server is ready...");
    } catch (Exception e) {
      System.out.println("Server failed: " + e);
    }
  }
}
