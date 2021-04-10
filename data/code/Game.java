import java.net.MalformedURLException;
import java.rmi.ConnectException;
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

  public boolean runningWithNoPlayers() {
    return (
      this.isRunning 
      && this.playerList.size() == 0
    );
  }

  public void start() {
    this.isRunning = true;

    for (Map.Entry<Integer, String> p : this.playerList.entrySet()) {
      try {
        IPlayer player = (IPlayer) Naming.lookup(p.getValue());
  
        player.start();
        System.out.printf("[+] started player (%d)\n", p.getKey());
      } catch (RemoteException e) {
        System.out.printf("[!] failed to start a player (%d): %s\n", p.getKey(), e);
      } catch (NotBoundException e) {
        System.out.printf("[!] failed to start a player (%d): %s\n", p.getKey(), e);
      } catch (MalformedURLException e) {
        System.out.printf("[!] failed to start a player (%d): %s\n", p.getKey(), e);
      }
    }
  }

  public void checkPlayers() {
    for (Map.Entry<Integer, String> player : this.playerList.entrySet()) {
      int playerId = player.getKey();
      try {
        System.out.printf("[+] Checking player (%d) => [%s]\n", playerId, player.getValue());
        IPlayer playerServer = (IPlayer) Naming.lookup(player.getValue());

        playerServer.check();
      } catch (NotBoundException e) {
        System.out.printf("[!] game server couldn't check player (%d): %s\n", playerId, e);
      } catch (MalformedURLException e) {
        System.out.printf("[!] game server couldn't check player (%d): %s\n", playerId, e);
      } catch (RemoteException e) {
        System.out.printf("[!] game server couldn't check player (%d): %s\n", playerId, e);
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
      System.out.printf("[+] registered player (%d) => [%s]\n", playerId, playerEndpoint);

    } catch (ServerNotActiveException e) {
      System.out.printf("[!] failed to get player (%d) host address: %s\n", playerId, e);
    } 

    return playerId;
  }

  private boolean sendBonus() {
    int bonus = ThreadLocalRandom.current().nextInt(0, 100);

    return bonus <= 3;
  }

  public int play(int playerId) throws RemoteException {
    System.out.printf("[+] player (%d) is playing\n", playerId);

    this.resultList.add(
      new Result(playerId, ResultType.PLAY, 1)
    );

    if (this.sendBonus()) {
      this.resultList.add(
        new Result(playerId, ResultType.BONUS, 1)
      );
    }

    return 1;
  }

  public int giveUp(int playerId) throws RemoteException {
    System.out.printf("[+] player (%d) gave up\n", playerId);

    this.playerList.remove(playerId);

    return 1;
  }

  public int stop(int playerId) throws RemoteException {
    System.out.printf("[+] player (%d) stopped playing\n", playerId);

    this.resultList.add(
      new Result(playerId, ResultType.STOP, 1)
    );

    return 1;
  }

  public void processResults() {
    int resultListLen = this.resultList.size();

    if (resultListLen == 0) { return; }

    for (int i = 0; i < resultListLen; i++) {

      Result r = this.resultList.remove();

      if (r == null) { continue; }

      String playerHostname = this.playerList.get(r.playerId);
  
      try {
        IPlayer player = (IPlayer) Naming.lookup(playerHostname);
        
        player.getResult(r.value, r.type);
        if (r.type == ResultType.STOP) {
          this.playerList.remove(r.playerId);
        }
        
      } catch (NotBoundException e) {
        System.out.printf("[!] failed to send result (%s) to player: %s\n", r.type, e);
      } catch (MalformedURLException e) {
        System.out.printf("[!] failed to send result (%s) to player: %s\n", r.type, e);
      } catch (RemoteException e) {
        System.out.printf("[!] failed to send result (%s) to player: %s\n", r.type, e);
      }
    }
  }

  public void getResult(int playerId, int result, ResultType resultType) throws RemoteException {
    switch (resultType) {
      case PCHECK:
        if (result > 0) {
          System.out.printf("[+] player (%d) is alive\n", playerId);
        } else {
          System.out.printf("[+] player (%d) isn't alive\n", playerId);
        }
        break;
    
      default:
        break;
    }
  }
}
