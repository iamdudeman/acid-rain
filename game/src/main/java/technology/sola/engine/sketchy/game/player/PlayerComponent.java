package technology.sola.engine.sketchy.game.player;

import technology.sola.ecs.Component;

public class PlayerComponent implements Component {
  public static final int MAX_SUNLIGHT = 5000;
  private int sunlight = 500;
  private float speed = 50;

  public int getSunlight() {
    return sunlight;
  }

  public void useSunlight() {
    this.sunlight--;
  }

  public float getSpeed() {
    return speed;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }
}
