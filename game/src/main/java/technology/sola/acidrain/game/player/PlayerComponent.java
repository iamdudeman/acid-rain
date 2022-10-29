package technology.sola.acidrain.game.player;

import technology.sola.ecs.Component;

public class PlayerComponent implements Component {
  public static final int MAX_SUNLIGHT = 720;
  private static final int PICKUP_VALUE = MAX_SUNLIGHT / 12;
  private static final float SPEED = 50;
  private static final float SLOW_SPEED = SPEED / 2;
  private static final float SUPER_SLOW_SPEED = SPEED / 4;
  private int sunlight = 0;
  private boolean isUsingSunlight = false;
  private boolean isSlowed = false;
  private boolean isSuperSlowed = false;

  public int getSunlight() {
    return sunlight;
  }

  public void useSunlight() {
    this.sunlight--;

    if (this.sunlight == 0) {
      this.isUsingSunlight = false;
    }
  }

  public boolean isUsingSunlight() {
    return isUsingSunlight && sunlight > 0;
  }

  public void pickupDonut() {
    sunlight += PICKUP_VALUE;

    if (sunlight > MAX_SUNLIGHT) {
      sunlight = MAX_SUNLIGHT;
    }
  }

  public void setUsingSunlight(boolean usingSunlight) {
    isUsingSunlight = usingSunlight && sunlight > 0;
  }

  public float getSpeed() {
    if (isSuperSlowed) {
      return SUPER_SLOW_SPEED;
    }

    return isSlowed ? SLOW_SPEED : SPEED;
  }

  public void setIsSlowed(boolean isSlowed) {
    this.isSlowed = isSlowed;
  }

  public void resetSlowed() {
    this.isSlowed = false;
    this.isSuperSlowed = false;
  }

  public void setIsSuperSlowed(boolean isSuperSlowed) {
    this.isSuperSlowed = isSuperSlowed;
  }
}
