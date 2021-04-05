import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class PlayerServer {
  private static final int REGISTRY_PORT = 9999;
  private static final int GAME_SERVER_PORT = 52369;

  private static void run(Player player) {
    try {
      IGame game = (IGame) Naming.lookup(
        String.format("//%s:%d/game_server", player.getGameServerIp(), GAME_SERVER_PORT)
      );

      int playerReg = game.register();
      if (playerReg == 0) {
        System.out.println("[X] the game server is full!");
        System.exit(0);
      }
      player.setId(playerReg);

      System.out.printf("Player registered (%d)\n", playerReg);
    } catch (Exception e) {
      System.out.println("[!] failed to run the player server" + e);
      System.exit(0);
    }
  }

  public static void main(String[] args) throws RemoteException {
    if (args.length != 2) {
      System.out.println("Usage: java Player <local_server_ip> <game_server_ip>");
      System.exit(0);
    }

    try {
      System.setProperty("java.rmi.server.hostname", args[0]);
      LocateRegistry.createRegistry(REGISTRY_PORT);
      System.out.println("[+] client RMI registered");
    } catch (RemoteException e) {
      System.out.println("[!] client RMI already exists");
    }

    Player player = new Player();
    try {
      String endpoint = String.format("rmi://%s:%d/player_server", args[0], REGISTRY_PORT);
      Naming.rebind(endpoint, player);

      player.setGameServerIp(args[1]);

      System.out.println("[+] player is registered");
    } catch (Exception e) {
      System.out.println("[!] player RMI register failed: " + e);
    }
    
    run(player);
  }
}
