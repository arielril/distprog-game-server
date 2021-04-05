import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Game extends UnicastRemoteObject implements IGame {
  public static final long serialVersionUID = -13213123213123L;

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

  public int register() throws RemoteException {
    // return an error (0) if there is more players than supported
    if (playerList.size()+1 > this.numOfPlayers) {
      return 0;
    }

    Player p = new Player();
    playerList.add(p);
    int playerId = playerList.size();
    p.setId(playerId);

    System.out.printf("[+] registered player (%d)\n", playerId);

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
