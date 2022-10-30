package technology.sola.acidrain.game.state;

public class GameStatistics {
  private static final int DONUT_THRESHOLD = 3;
  private static final float INTENSITY_SECONDS = 10;
  private static int donutsConsumed;
  private static double distanceTraveled;
  private static int intensityLevel = 1;
  private static float intensityAccumulator = 0f;
  private static int decreaseIntensityCounter = 0;

  public static void reset() {
    donutsConsumed = 0;
    distanceTraveled = 0;
    intensityLevel = 1;
    intensityAccumulator = 0;
    decreaseIntensityCounter = 0;
  }

  public static void incrementDonutsConsumed() {
    donutsConsumed++;

    decreaseIntensityCounter++;

    if (decreaseIntensityCounter >= DONUT_THRESHOLD) {
      decreaseIntensityCounter -= DONUT_THRESHOLD;
      intensityLevel--;
      intensityAccumulator = 0;

      if (intensityLevel <= 0) {
        intensityLevel = 1;
      }
    }
  }

  public static void increaseDistanceTraveled(double distanceTraveled) {
    GameStatistics.distanceTraveled += distanceTraveled;
  }

  public static void incrementIntensityLevel(float dt) {
    intensityAccumulator += dt;

    if (intensityAccumulator > INTENSITY_SECONDS) {
      intensityLevel++;
      intensityAccumulator -= INTENSITY_SECONDS;
    }
  }

  public static int getDonutsConsumed() {
    return donutsConsumed;
  }

  public static double getDistanceTraveled() {
    return distanceTraveled;
  }

  public static int getIntensityLevel() {
    return intensityLevel;
  }

  private GameStatistics() {
  }
}
