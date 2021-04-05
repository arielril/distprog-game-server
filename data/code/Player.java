import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Player extends UnicastRemoteObject implements IPlayer {
  public static final long serialVersionUID = -13553123213123L;

  private int id;
  private String gameServerIp;
  private int numOfPlays;

  public Player() throws RemoteException {
    this.numOfPlays = 50;
  }

  public Player(int id) throws RemoteException {
    this.id = id;
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
  
  public void start() throws RemoteException {
    System.out.printf("[+] player (%d) started playing\n", this.id);

    // https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
    // here we need to sleep for [250 - 950]ms (random)
    for (int i = 1; i <= this.numOfPlays; i++) {
      int randSleep = ThreadLocalRandom.current().nextInt(250, 951);
      System.out.printf("[+] player (%d) is sleeping for [%d]ms\n", this.id, randSleep);
    }
  }
  
  public void bonus() throws RemoteException {
    System.out.printf("[+] player (%d) got the bonus\n", this.id);
  }

  public void check() throws RemoteException {
    System.out.printf("[+] player (%d) was checked for liveness\n", this.id);
  }
}
