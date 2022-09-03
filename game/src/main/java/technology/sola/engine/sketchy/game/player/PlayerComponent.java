package technology.sola.engine.sketchy.game.player;

import technology.sola.ecs.Component;

public class PlayerComponent implements Component {
  private static final float SPEED = 50;
  private static final float SLOW_SPEED = SPEED / 2;
  private static final float SUPER_SLOW_SPEED = SPEED / 4;
  private boolean isSlowed = false;
  private boolean isSuperSlowed = false;
  private int donutsConsumed = 0;

  public void pickupDonut() {
    donutsConsumed++;
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

  public int getDonutsConsumed() {
    return donutsConsumed;
  }
}
