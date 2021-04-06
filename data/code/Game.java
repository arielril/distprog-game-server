import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Game extends UnicastRemoteObject implements IGame {
  public static final long serialVersionUID = -13213123213123L;
  private final int PLAYER_SERVER_PORT = 9999;

  private int numOfPlayers;
  private List<IPlayer> playerList;
  private boolean isRunning;


  public Game(int n) throws RemoteException {
    this.numOfPlayers = n;
    this.isRunning = false;

    this.playerList = new ArrayList<IPlayer>(this.numOfPlayers);
  }

  public int getNumberOfRegisteredPlayers() {
    return this.playerList.size();
  }

  public boolean isGameReady() {
    // return false to prevent that the server starts the game again
    if (this.isRunning) { return false; }

    return this.playerList.size() == this.numOfPlayers;
  }

  public void start() {
    this.isRunning = true;

    for (IPlayer p : this.playerList) {
      try {
        p.start();
      } catch (RemoteException e) {
        System.out.println("[!] failed to start a player: " + e);
      }
    }
  }

  public void checkPlayers() {
    for (IPlayer player : this.playerList) {
      int currentPlayerId = 0;
      try {
        currentPlayerId = player.getId();
        IPlayer playerServer = (IPlayer) Naming.lookup(
          String.format(
            "//%s:%d/player_server%d", 
            player.getServerIp(), 
            PLAYER_SERVER_PORT,
            currentPlayerId
          )
        );

        playerServer.check();
      } catch (NotBoundException e) {
        System.out.printf("[!] game server couldn't check player (%d): %s\n", currentPlayerId, e);
      } catch (MalformedURLException e) {
        System.out.printf("[!] game server couldn't check player (%d): %s\n", currentPlayerId, e);
      } catch (RemoteException e) {
        System.out.printf("[!] game server couldn't check player (%d): %s\n", currentPlayerId, e);
      }
    }
  }

  public int register() throws RemoteException {
    // return an error (0) if there is more players than supported
    if (playerList.size()+1 > this.numOfPlayers) {
      return 0;
    }

    Player p = new Player();
    playerList.add(p);
    int playerId = playerList.size();
    
    p.setId(playerId);
    try {
      // set the host from where the player made the request
      String phost = getClientHost();
      p.setServerIp(phost);
    } catch (ServerNotActiveException e) {
      System.out.printf("[!] failed to get player (%d) host address: %s\n", playerId, e);
    }

    System.out.printf("[+] registered player (%d) -> %s\n", playerId, p.getServerIp());

    return playerId;
  }

  public int play(int playerId) throws RemoteException {
    System.out.printf("[+] player (%d) is playing\n", playerId);

    return 1;
  }

  public int giveUp(int playerId) throws RemoteException {
    System.out.printf("[+] player (%d) gave up\n", playerId);
    return 1;
  }

  public int stop(int playerId) throws RemoteException {
    System.out.printf("[+] player (%d) stopped playing\n", playerId);
    return 1;
  }
}
