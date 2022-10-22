package technology.sola.acidrain.game.state;

public class GameStatistics {
  private static int donutsConsumed;
  private static double distanceTraveled;

  public static void reset() {
    donutsConsumed = 0;
    distanceTraveled = 0;
  }

  public static void incrementDonutsConsumed() {
    donutsConsumed++;
  }

  public static void increaseDistanceTraveled(double distanceTraveled) {
    GameStatistics.distanceTraveled += distanceTraveled;
  }

  public static int getDonutsConsumed() {
    return donutsConsumed;
  }

  public static double getDistanceTraveled() {
    return distanceTraveled;
  }

  private GameStatistics() {
  }
}
