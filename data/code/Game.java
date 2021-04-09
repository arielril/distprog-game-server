import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Game extends UnicastRemoteObject implements IGame {
  public static final long serialVersionUID = -13213123213123L;
  private final int PLAYER_BASE_PORT = 9990;

  private int numOfPlayers;
  private HashMap<Integer, String> playerList;
  private boolean isRunning;

  private Queue<Result> resultList;

  public Game(int n) throws RemoteException {
    this.numOfPlayers = n;
    this.isRunning = false;

    this.playerList = new HashMap<Integer, String>();
    this.resultList = new LinkedList<Result>();
  }

  public boolean isGameReady() {
    // return false to prevent that the server starts the game again
    if (this.isRunning) { return false; }

    return this.playerList.size() == this.numOfPlayers;
  }

  public void start() {
    this.isRunning = true;

    for (Map.Entry<Integer, String> p : this.playerList.entrySet()) {
      try {
        IPlayer player = (IPlayer) Naming.lookup(p.getValue());
  
        player.start();
      } catch (RemoteException e) {
        System.out.println("[!] failed to start a player: " + e);
      } catch (NotBoundException e) {
        System.out.println("[!] failed to start a player: " + e);
      } catch (MalformedURLException e) {
        System.out.println("[!] failed to start a player: " + e);
      }
    }
  }

  public void checkPlayers() {
    for (Map.Entry<Integer, String> player : this.playerList.entrySet()) {
      try {
        System.out.printf("[.] Checking player (%d) => [%s]\n", player.getKey(), player.getValue());
        IPlayer playerServer = (IPlayer) Naming.lookup(player.getValue());

        playerServer.check();
      } catch (NotBoundException e) {
        System.out.printf("[!] game server couldn't check player (%d): %s\n", player.getKey(), e);
      } catch (MalformedURLException e) {
        System.out.printf("[!] game server couldn't check player (%d): %s\n", player.getKey(), e);
      } catch (RemoteException e) {
        System.out.printf("[!] game server couldn't check player (%d): %s\n", player.getKey(), e);
      }
    }
  }

  public int register() throws RemoteException {
    // return an error (0) if there is more players than supported
    if (playerList.size()+1 > this.numOfPlayers) {
      return 0;
    }

    int playerId = playerList.size()+1;
    
    try {
      // set the host from where the player made the request
      String playerEndpoint = String.format(
        "rmi://%s:%d/player_server", 
        getClientHost(), 
        PLAYER_BASE_PORT + playerId
      );

      this.playerList.put(playerId, playerEndpoint);
      System.out.printf("[+] registered player (%d) -> %s\n", playerId, playerEndpoint);

    } catch (ServerNotActiveException e) {
      System.out.printf("[!] failed to get player (%d) host address: %s\n", playerId, e);
    } 

    return playerId;
  }

  public int play(int playerId) throws RemoteException {
    System.out.printf("[+] player (%d) is playing\n", playerId);

    this.resultList.add(
      new Result(playerId, ResultType.PLAY, 1)
    );

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

  public void processResults() {
    int resultListLen = this.resultList.size();

    for (int i = 0; i < resultListLen; i++) {

      Result r = this.resultList.remove();

      if (r == null) { continue; }

      String playerHostname = this.playerList.get(r.playerId);
  
      try {
        IPlayer player = (IPlayer) Naming.lookup(playerHostname);
  
        player.getResult(r.value, r.type);
      } catch (NotBoundException e) {
        System.out.printf("[!] failed to send result (%s) to player: %s\n", r.type, e);
      } catch (MalformedURLException e) {
        System.out.printf("[!] failed to send result (%s) to player: %s\n", r.type, e);
      } catch (RemoteException e) {
        System.out.printf("[!] failed to send result (%s) to player: %s\n", r.type, e);
      } 
    }
  }

  private void sendResult(Result r) {
    try {
      IPlayer player = (IPlayer) Naming.lookup(this.playerList.get(r.playerId));

      player.getResult(r.value, r.type);
    } catch (NotBoundException e) {
      System.out.printf("[!] failed to send result (%s) to player: %s\n", r.type, e);
    } catch (MalformedURLException e) {
      System.out.printf("[!] failed to send result (%s) to player: %s\n", r.type, e);
    } catch (RemoteException e) {
      System.out.printf("[!] failed to send result (%s) to player: %s\n", r.type, e);
    } 
  }
}
