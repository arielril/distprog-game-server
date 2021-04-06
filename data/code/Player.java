import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ThreadLocalRandom;

public class Player extends UnicastRemoteObject implements IPlayer {
  public static final long serialVersionUID = -13553123213123L;
  private final int GAME_SERVER_PORT = 52369;

  private int id;
  private String gameServerIp;
  private String serverIp;
  private int numOfPlays;

  public Player() throws RemoteException {
    this.numOfPlays = 50;
    this.gameServerIp = "";
    this.serverIp = "";
  }

  public Player(int id) throws RemoteException {
    this.id = id;
    this.numOfPlays = 50;
    this.gameServerIp = "";
    this.serverIp = "";
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setGameServerIp(String ip) {
    this.gameServerIp = ip;
  }

  public String getGameServerIp() {
    return this.gameServerIp;
  }

  public String getServerIp() throws RemoteException {
    return this.serverIp;
  }

  public void setServerIp(String serverIp) {
    this.serverIp = serverIp;
  }
    
  public void start() throws RemoteException {
    System.out.printf("[+] player (%d) started playing\n", this.id);

    // https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
    // here we need to sleep for [250 - 950]ms (random)
    for (int i = 1; i <= this.numOfPlays; i++) {
      try {
        // TODO make a way for the players to have individual URLs. add a default+random value

        // Play
        IGame game = (IGame) Naming.lookup(
          String.format("//%s:%d/game_server", this.gameServerIp,GAME_SERVER_PORT)
        );
  
        int playResult = game.play(this.id);
        if (playResult != 1) {
          System.out.printf("[!] player (%d) failed to play\n", this.id);
        }
      } catch (NotBoundException e) {
        System.out.printf("[!] player (%d) failed to play: %s\n", this.id, e);
      } catch (MalformedURLException e) {
        System.out.printf("[!] player (%d) failed to play: %s\n", this.id, e);
      }

      int randSleep = ThreadLocalRandom.current().nextInt(250, 951);
      System.out.printf("[+] player (%d) is sleeping for [%d]ms\n", this.id, randSleep);
      try {
        Thread.sleep(randSleep);
      } catch (InterruptedException e) {
        System.out.printf("[!] player (%d) failed to sleep for [%d]ms\n", this.id, randSleep);
      }
    }
  }
  
  public void bonus() throws RemoteException {
    System.out.printf("[+] player (%d) got the bonus\n", this.id);
  }

  public void check() throws RemoteException {
    System.out.printf("[+] player (%d) was checked for liveness\n", this.id);
  }
}
