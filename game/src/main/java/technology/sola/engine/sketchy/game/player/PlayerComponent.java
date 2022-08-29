package technology.sola.engine.sketchy.game.player;

import technology.sola.ecs.Component;

public class PlayerComponent implements Component {
  public static final int MAX_SUNLIGHT = 1000;
  private int sunlight = 100;
  private float speed = 50;
  private boolean isUsingSunlight = false;

  public int getSunlight() {
    return sunlight;
  }

  public void useSunlight() {
    this.sunlight--;
  }

  public boolean isUsingSunlight() {
    return isUsingSunlight && sunlight > 0;
  }

  public void setUsingSunlight(boolean usingSunlight) {
    isUsingSunlight = usingSunlight;
  }

  public float getSpeed() {
    return speed;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }
}
