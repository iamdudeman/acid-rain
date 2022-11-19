package technology.sola.acidrain.game.component;

import technology.sola.ecs.Component;

public class RainComponent implements Component {
  public float height = 40;
  public final float x;
  public final float y;

  public RainComponent(float x, float y) {
    this.x = x;
    this.y = y;
  }
}
