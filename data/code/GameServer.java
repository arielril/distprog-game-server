import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class GameServer {
  private static final int REGISTRY_PORT = 52369;

  private static void run(Game game) {
    long start = System.currentTimeMillis();
    while(true) {
      if (game.isGameReady()) {
        System.out.println("[+] game is ready ;)");
        game.start();
      }

      if (System.currentTimeMillis() - start >= 5000) {
        game.checkPlayers();
        start = System.currentTimeMillis();
      }

      game.processResults();

      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {}
    }
  }

  public static void main(String[] args) throws RemoteException {
    if (args.length != 2) {
      System.out.println("Usage: java GameServer <local_server_ip> <n_of_players>");
      System.exit(1);
    }

    try {
      // start and register the RMI server
      System.setProperty("java.rmi.server.hostname", args[0]);
      LocateRegistry.createRegistry(REGISTRY_PORT);

      System.out.println("[+] game server RMI registered");
    } catch (RemoteException e) {
      System.out.println("[!] game server RMI already registered");
    }

    // register the server in the RMI server
    Game game = new Game(Integer.parseInt(args[1]));
    try {
      String endpoint = String.format("rmi://%s:%d/game_server", args[0], REGISTRY_PORT);
      Naming.rebind(endpoint, game);

      System.out.println("[+] Server is up");
    } catch (Exception e) {
      System.out.println("[!] Server failed: " + e);
    }

    run(game);
  }
}
