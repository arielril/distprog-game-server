public class Result {
  public ResultType type;
  public int value;
  public int playerId;

  public Result(int playerId, ResultType type, int value) {
    this.type = type;
    this.value = value;
    this.playerId = playerId;
  }
}
