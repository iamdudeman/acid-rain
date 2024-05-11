package technology.sola.acidrain.game.component;

import technology.sola.acidrain.game.rendering.RainRendererEntityGraphicsModule;
import technology.sola.ecs.Component;

import java.util.LinkedList;
import java.util.List;

public class RainCloudComponent implements Component {
  private final List<RainDrop> dropList = new LinkedList<>();

  public List<RainDrop> rainDrops() {
    return dropList;
  }

  public void createDrop(float x, float y) {
    dropList.add(new RainDrop(x, y));
  }

  public void updateDrops(boolean showAnimation) {
    var dropIterator = dropList.iterator();

    while (dropIterator.hasNext()) {
      var drop = dropIterator.next();

      drop.height--;
      int heightThreshold = showAnimation ? RainRendererEntityGraphicsModule.RAIN_ANIMATION_HEIGHT_THRESHOLD_2 - 2 : 0;

      if (drop.height <= heightThreshold) {
        dropIterator.remove();
      }
    }
  }

  public static class RainDrop {
    private int height = 40;
    public final float x;
    public final float y;

    public RainDrop(float x, float y) {
      this.x = x;
      this.y = y;
    }

    public int getHeight() {
      return height;
    }
  }
}
