package technology.sola.engine.sketchy.game.sunlight;

import technology.sola.ecs.Component;

public class SunlightBarComponent  implements Component {
  public static final int MAX_SUNLIGHT = 1000;
  private static final int PICKUP_VALUE = MAX_SUNLIGHT / 12;
  private int sunlight = 0;
  private boolean isDraining = false;

  public void incrementSunlight() {
    sunlight += PICKUP_VALUE;

    if (sunlight > MAX_SUNLIGHT) {
      sunlight = MAX_SUNLIGHT;
    }
  }

  public void setIsDraining(boolean isDraining) {
    this.isDraining = isDraining;
  }

  public boolean isDraining() {
    return isDraining && sunlight > 0;
  }

  public void startDraining() {
    this.sunlight--;
  }

  public int getSunlight() {
    return sunlight;
  }
}
