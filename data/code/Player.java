import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class Player extends UnicastRemoteObject implements IPlayer {
  public static final long serialVersionUID = -13553123213123L;
  private final int GAME_SERVER_PORT = 52369;

  private int id;
  private String gameServer;
  // format: <ip>:<port>
  private String host;
  private int port;
  private int numOfPlays;
  private boolean started;
  private Queue<Result> resultList;

  public Player() throws RemoteException {
    this.numOfPlays = 50;
    this.gameServer = "";
    this.host = "";
    this.port = 0;
    this.started = false;
    this.resultList = new LinkedList<Result>();
  }

  public Player(int id) throws RemoteException {
    this.id = id;
    this.numOfPlays = 50;
    this.gameServer = "";
    this.host = "";
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setGameServer(String gameServer) {
    this.gameServer = gameServer;
  }

  public String getGameServer() {
    return this.gameServer;
  }

  public String getHost() throws RemoteException {
    return this.host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getPort() {
    return this.port;
  }

  public String getHostname() throws RemoteException {
    return String.format("%s:%d", this.host, this.port);
  }

  public void setStarted(boolean started) {
    this.started = started;
  }

  public boolean getStarted() {
    return this.started;
  }

  public int timesToPlay() {
    return this.numOfPlays;
  }

  public void play() {
    if (this.numOfPlays <= 0) {
      return;
    }
    
    // https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
    // here we need to sleep for [250 - 950]ms (random)
    try {
      // Play
      IGame game = (IGame) Naming.lookup(
        String.format("rmi://%s:%d/game_server", this.gameServer, GAME_SERVER_PORT)
      );

      game.play(this.id);
      this.numOfPlays--;

      System.out.printf("[+] Player (%d) have (%d) plays left\n", this.id, this.numOfPlays);
    } catch (NotBoundException e) {
      System.out.printf("[!] player (%d) failed to play: %s\n", this.id, e);
    } catch (MalformedURLException e) {
      System.out.printf("[!] player (%d) failed to play: %s\n", this.id, e);
    } catch (RemoteException e) {
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
    
  public void start() throws RemoteException {
    System.out.printf("[+] player (%d) started playing\n", this.id);

    this.setStarted(true);
  }

  public void bonus() throws RemoteException {
    System.out.printf("[+] player (%d) got a bonus\n", this.id);
  }

  public void check() throws RemoteException {
    System.out.printf("[+] player (%d) was checked for liveness\n", this.id);

    this.resultList.add(
      new Result(this.id, ResultType.PCHECK, 1)
    );
  }

  public void getResult(int result, ResultType resultType) throws RemoteException {
    switch (resultType) {
      case PLAY:
        if (result > 0) {
          System.out.printf("[!] player (%d) played\n", this.id);
        } else {
          System.out.printf("[!] player (%d) failed to play\n", this.id);
        }
        break;
    
      case STOP:
        if (result > 0) {
          System.out.printf("[+] Player (%d) is stopping\n", this.id);
        } else {
          System.out.printf("[!] player (%d) couldn't stop from playing\n", this.id);
        }

      default:
        break;
    }
  }

  public void stopPlaying() {
    try {
      System.out.printf("[+] player (%d) is stopping\n", this.id);
      IGame game = (IGame) Naming.lookup(this.getGameServer());
      
      game.stop(this.id);
    } catch (NotBoundException e) {
      System.out.printf("[!] failed to stop the player (%d) from playing: %s\n", this.id, e);
    } catch (MalformedURLException e) {
      System.out.printf("[!] failed to stop the player (%d) from playing: %s\n", this.id, e);
    } catch (RemoteException e) {
      System.out.printf("[!] failed to stop the player (%d) from playing: %s\n", this.id, e);
    }
  }

  public void processResults() {
    int resultListLen = this.resultList.size();

    if (resultListLen < 1) { return; }

    for (int i = 0; i < resultListLen; i++) {
      Result r = this.resultList.remove();

      if (r == null) { continue; }

      try {
        IGame game = (IGame) Naming.lookup(this.getGameServer());

        game.getResult(this.id, r.value, r.type);
      } catch (NotBoundException e) {
        System.out.printf("[!] failed to send result (%s) to the server: %s\n", r.type, e);
      } catch (MalformedURLException e) {
        System.out.printf("[!] failed to send result (%s) to the server: %s\n", r.type, e);
      } catch (RemoteException e) {
        System.out.printf("[!] failed to send result (%s) to the server: %s\n", r.type, e);
      } 
    }
  }
}
