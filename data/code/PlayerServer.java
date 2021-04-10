import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class PlayerServer {
  private static final int REGISTRY_PORT = 9990;
  private static final int GAME_SERVER_PORT = 52369;

  private static void registration(Player player) {
    try {
      IGame game = getGameServer(player);

      int playerReg = game.register();
      if (playerReg == 0) {
        System.out.println("[X] the game server is full!");
        System.exit(0);
      }
      player.setId(playerReg);
      
      // unbind old naming registration
      String endpoint = String.format(
        "rmi://%s/player_server", 
        player.getHostname()
      );
      Naming.unbind(endpoint);
        
      // bind new naming registration
      player.setPort(REGISTRY_PORT + playerReg);
      LocateRegistry.createRegistry(player.getPort());
      endpoint = String.format(
        "rmi://%s/player_server", 
        player.getHostname()
      );
      Naming.rebind(endpoint, player);

      System.out.printf("[+] Player registered (%d) | endpoint = [%s]\n", playerReg, endpoint);
    } catch (Exception e) {
      System.out.println("[!] failed to run the player server: " + e);
      System.exit(0);
    }
  }

  private static void run(Player player) {
    registration(player);
    
    while(true) {
      if (player.getStarted()) {
        player.play();
      }

      if (player.timesToPlay() <= 0) {
        player.stopPlaying();
      }

      player.processResults();

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {}
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
      System.out.println("[!] client RMI already exists: " + e);
    }

    Player player = new Player();
    try {
      String endpoint = String.format("rmi://%s:%d/player_server", args[0], REGISTRY_PORT);
      Naming.rebind(endpoint, player);

      player.setHost(args[0]);
      player.setPort(REGISTRY_PORT);

      player.setGameServer(
        String.format("rmi://%s:%d/game_server", args[1], GAME_SERVER_PORT)
      );

      System.out.println("[+] player is registered");
    } catch (Exception e) {
      System.out.println("[!] player RMI register failed: " + e);
    }
    
    run(player);
  }

  public static IGame getGameServer(Player player) {
    try {
      return (IGame) Naming.lookup(player.getGameServer());
    } catch (Exception e) {
      System.out.println("[!] failed to get the game server naming: " + e);
      System.exit(0);
    }
    return null;
  }
}
