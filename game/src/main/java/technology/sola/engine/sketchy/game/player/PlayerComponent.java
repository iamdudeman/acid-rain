package technology.sola.engine.sketchy.game.player;

import technology.sola.ecs.Component;

public class PlayerComponent implements Component {
  // TODO tune these values
  public static final int MAX_SUNLIGHT = 1000;
  private final int pickupValue = MAX_SUNLIGHT / 20;
  private int sunlight = MAX_SUNLIGHT / 4;
  private final float speed = 50;
  private final float slowSpeed = speed / 5;
  private boolean isUsingSunlight = false;
  private boolean isSlowed = false;

  public int getSunlight() {
    return sunlight;
  }

  public void useSunlight() {
    this.sunlight--;
  }

  public boolean isUsingSunlight() {
    return isUsingSunlight && sunlight > 0;
  }

  public void pickupSunlight() {
    sunlight += pickupValue;

    if (sunlight > MAX_SUNLIGHT) {
      sunlight = MAX_SUNLIGHT;
    }
  }

  public void setUsingSunlight(boolean usingSunlight) {
    isUsingSunlight = usingSunlight;
  }

  public float getSpeed() {
    return isSlowed ? slowSpeed : speed;
  }

  public void setIsSlowed(boolean isSlowed) {
    this.isSlowed = isSlowed;
  }
}
