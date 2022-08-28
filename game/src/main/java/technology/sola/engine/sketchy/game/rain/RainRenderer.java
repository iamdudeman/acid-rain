package technology.sola.engine.sketchy.game.rain;

import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.renderer.BlendMode;
import technology.sola.engine.graphics.renderer.Renderer;
import technology.sola.engine.sketchy.game.EntityNames;

public class RainRenderer {
  private static final int RAIN_LENGTH = 64;
  private static final Color RAIN_COLOR = new Color(153, 128, 128, 128);

  public void render(Renderer renderer, World world) {
    BlendMode previousBlendMode = renderer.getBlendMode();
    renderer.setBlendMode(BlendMode.NORMAL);

    world.findEntityByName(EntityNames.CAMERA).ifPresent(cameraEntity -> {
      TransformComponent cameraTransform = cameraEntity.getComponent(TransformComponent.class);

      for (var view : world.createView().of(RainComponent.class)) {
        RainComponent rainComponent = view.c1();

        drawRain(renderer, rainComponent, cameraTransform.getX(), cameraTransform.getY());
      }
    });

    renderer.setBlendMode(previousBlendMode);
  }

  /**
   *
   * @see <a href="https://www.youtube.com/watch?v=66f6bI2uIdQ&list=WL&index=39&ab_channel=CameronPenner">Math based on this video</a>
   * @param renderer
   * @param rainComponent
   * @param cameraX
   * @param cameraY
   */
  private void drawRain(Renderer renderer, RainComponent rainComponent, float cameraX, float cameraY) {
    float halfCameraWidth = renderer.getWidth() * 0.5f;
    float halfCameraHeight = renderer.getHeight() * 0.5f;
    float x = rainComponent.x - cameraX;
    float y = rainComponent.y - cameraY;
    float height = rainComponent.height;

    float vectorX = (x - (cameraX + halfCameraWidth)) / halfCameraWidth;
    float vectorY = (y - (-cameraY + halfCameraHeight)) / halfCameraHeight;

    float sqrtHeight = (float) Math.sqrt(height);
    float sqrtHeightLength = (float) Math.sqrt(height + RAIN_LENGTH);

    float lineX = x + vectorX * sqrtHeight;
    float lineY = y + vectorY * sqrtHeight;
    float lineX2 = x + vectorX * sqrtHeightLength;
    float lineY2 = y + vectorY * sqrtHeightLength;

    renderer.drawLine(lineX, lineY, lineX2, lineY2, RAIN_COLOR);
  }
}
