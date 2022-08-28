package technology.sola.engine.sketchy.game.rain;

import technology.sola.ecs.World;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.renderer.BlendMode;
import technology.sola.engine.graphics.renderer.Renderer;

public class RainRenderer {
  private static final int RAIN_LENGTH = 64;
  private static final Color RAIN_COLOR = new Color(153, 128, 128, 128);

  public void render(Renderer renderer, World world) {
    BlendMode previousBlendMode = renderer.getBlendMode();
    renderer.setBlendMode(BlendMode.NORMAL);

    for (var view : world.createView().of(RainComponent.class)) {
      RainComponent rainComponent = view.c1();

      drawRain(renderer, rainComponent);
    }

    renderer.setBlendMode(previousBlendMode);
  }

  private void drawRain(Renderer renderer, RainComponent rainComponent) {
    // TODO Get camera values from actual camera in the world
    float cameraX = 0;
    float cameraY = 0;
    float halfCameraWidth = renderer.getWidth() / 2f;
    float halfCameraHeight = renderer.getHeight() / 2f;
    float x = rainComponent.x;
    float y = rainComponent.y;
    float height = rainComponent.height;

    float vectorX = (x - (cameraX + halfCameraWidth)) / halfCameraWidth;
    float vectorY = (y - (cameraY + halfCameraHeight)) / halfCameraHeight;

    float lineX = (float) (x + vectorX * Math.sqrt(height));
    float lineY = (float) (y + vectorY * Math.sqrt(height));
    float lineX2 = (float) (x + vectorX * Math.sqrt(height + RAIN_LENGTH));
    float lineY2 = (float) (y + vectorY * Math.sqrt(height + RAIN_LENGTH));

    renderer.drawLine(lineX, lineY, lineX2, lineY2, RAIN_COLOR);
  }
}
