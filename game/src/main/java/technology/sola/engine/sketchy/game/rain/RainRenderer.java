package technology.sola.engine.sketchy.game.rain;

import technology.sola.ecs.Entity;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.renderer.BlendMode;
import technology.sola.engine.graphics.renderer.Renderer;
import technology.sola.engine.sketchy.game.Constants;

public class RainRenderer {
  public static final int RAIN_ANIMATION_HEIGHT_THRESHOLD_1 = -2;
  public static final int RAIN_ANIMATION_HEIGHT_THRESHOLD_2 = -4;
  private static final int RAIN_LENGTH = 64;
  private static final Color RAIN_COLOR = new Color(153, 220, 220, 220);
  private boolean animationToggle = false;

  public void render(Renderer renderer, World world) {
    renderer.setBlendMode(BlendMode.NORMAL);

    world.findEntityByName(Constants.EntityNames.CAMERA).ifPresent(cameraEntity -> {
      TransformComponent cameraTransform = cameraEntity.getComponent(TransformComponent.class);

      for (Entity entity : world.findEntitiesWithComponents(RainComponent.class)) {
        drawRain(renderer, entity.getComponent(RainComponent.class), cameraTransform.getX(), cameraTransform.getY());
      }
    });
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
    float height = rainComponent.height;
    float x = rainComponent.x - cameraX;
    float y = rainComponent.y - cameraY;

    if (height > 0) {
      float halfCameraWidth = renderer.getWidth() * 0.5f;
      float halfCameraHeight = renderer.getHeight() * 0.5f;

      float vectorX = (x - (halfCameraWidth)) / halfCameraWidth;
      float vectorY = (y - (halfCameraHeight)) / halfCameraHeight;

      float sqrtHeight = (float) Math.sqrt(height);
      float sqrtHeightLength = (float) Math.sqrt(height + RAIN_LENGTH);

      float lineX = x + vectorX * sqrtHeight;
      float lineY = y + vectorY * sqrtHeight;
      float lineX2 = x + vectorX * sqrtHeightLength;
      float lineY2 = y + vectorY * sqrtHeightLength;

      renderer.drawLine(lineX, lineY, lineX2, lineY2, RAIN_COLOR);
    } else {
      if (height > RAIN_ANIMATION_HEIGHT_THRESHOLD_1) {
        // Initial splash animation
        renderer.fillRect(x, y, 2, 2, RAIN_COLOR);
      } else if (height > RAIN_ANIMATION_HEIGHT_THRESHOLD_2) {
        // End of splash animation
        renderer.fillRect(x - 1, y, 2, 1, RAIN_COLOR);

        // Alternate between two animations to add some flare :)
        if (animationToggle) {
          renderer.fillRect(x + 2, y - 3, 1, 1, RAIN_COLOR);
          renderer.fillRect(x + 1, y + 2, 1, 1, RAIN_COLOR);
        } else {
          renderer.fillRect(x - 2, y + 3, 1, 1, RAIN_COLOR);
          renderer.fillRect(x - 1, y - 2, 1, 1, RAIN_COLOR);
        }

        animationToggle = !animationToggle;
      }
    }
  }
}
