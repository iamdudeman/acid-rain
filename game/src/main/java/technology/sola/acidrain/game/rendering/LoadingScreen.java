package technology.sola.acidrain.game.rendering;

import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.renderer.Renderer;

import java.util.Arrays;

public class LoadingScreen {
  private final int maxDots = 6;
  private int loadingDotCount = 0;
  private long lastUpdate = System.currentTimeMillis();

  public void drawLoading(Renderer renderer) {
    long delay = loadingDotCount + 1 < maxDots ? 300 : 1300;

    if (System.currentTimeMillis() - lastUpdate > delay) {
      loadingDotCount = (loadingDotCount + 1) % maxDots;
      lastUpdate = System.currentTimeMillis();
    }

    String[] dotArray = new String[loadingDotCount];
    Arrays.fill(dotArray, ".");

    renderer.clear();
    renderer.drawString("Loading" + String.join("", dotArray), 20, renderer.getHeight() - 50, Color.WHITE);
  }
}
