package technology.sola.acidrain.game.rendering;

import technology.sola.acidrain.game.AcidRainSola;
import technology.sola.acidrain.game.component.RainCloudComponent;
import technology.sola.ecs.World;
import technology.sola.ecs.view.View;
import technology.sola.ecs.view.View1Entry;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.defaults.graphics.modules.SolaEntityGraphicsModule;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.renderer.Renderer;

public class RainRendererEntityGraphicsModule extends SolaEntityGraphicsModule<View1Entry<RainCloudComponent>> {
  public static final int RAIN_ANIMATION_HEIGHT_THRESHOLD_1 = -2;
  public static final int RAIN_ANIMATION_HEIGHT_THRESHOLD_2 = -4;
  private static final int RAIN_LENGTH = 64;
  private static final Color RAIN_COLOR = new Color(255, 220, 220, 220);
  private boolean animationToggle = false;

  @Override
  public View<View1Entry<RainCloudComponent>> getViewToRender(World world) {
    return world.createView().of(RainCloudComponent.class);
  }

  @Override
  public void renderEntity(Renderer renderer, View1Entry<RainCloudComponent> viewEntry, TransformComponent cameraModifiedEntityTransform) {
    viewEntry.c1()
      .rainDrops()
      .forEach(drop -> drawRain(
          renderer,
          cameraModifiedEntityTransform.getX() + drop.x,
          cameraModifiedEntityTransform.getY() + drop.y,
          drop.getHeight()
        )
      );
  }

  /**
   * @param renderer
   * @param x
   * @param y
   * @param height
   * @see <a href="https://www.youtube.com/watch?v=66f6bI2uIdQ&list=WL&index=39&ab_channel=CameronPenner">Math based on this video</a>
   */
  private void drawRain(Renderer renderer, float x, float y, float height) {
    if (height > 0) {
      float halfCameraWidth = AcidRainSola.CANVAS_WIDTH * 0.5f;
      float halfCameraHeight = AcidRainSola.CANVAS_HEIGHT * 0.5f;

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
