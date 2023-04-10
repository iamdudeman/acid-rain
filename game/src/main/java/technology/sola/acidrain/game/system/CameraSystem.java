package technology.sola.acidrain.game.system;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.Entity;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.acidrain.game.Constants;
import technology.sola.acidrain.game.AcidRainSola;
import technology.sola.math.linear.Vector2D;

public class CameraSystem extends EcsSystem {
  @Override
  public void update(World world, float dt) {
    Entity cameraEntity = world.findEntityByName(Constants.EntityNames.CAMERA);

    if (cameraEntity != null) {
      Entity playerEntity = world.findEntityByName(Constants.EntityNames.PLAYER);

      if (playerEntity != null) {
        TransformComponent cameraTransform = cameraEntity.getComponent(TransformComponent.class);
        TransformComponent playerTransform = playerEntity.getComponent(TransformComponent.class);

        cameraTransform.setTranslate(
          playerTransform.getTranslate().subtract(new Vector2D(AcidRainSola.HALF_CANVAS_WIDTH, AcidRainSola.HALF_CANVAS_HEIGHT))
        );
      }
    }
  }
}
