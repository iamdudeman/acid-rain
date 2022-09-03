package technology.sola.engine.sketchy.game.player;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.sketchy.game.Constants;
import technology.sola.engine.sketchy.game.AcidRainSola;
import technology.sola.math.linear.Vector2D;

public class CameraSystem extends EcsSystem {
  @Override
  public void update(World world, float dt) {
    world.findEntityByName(Constants.EntityNames.CAMERA).ifPresent(cameraEntity -> {
      world.findEntityByName(Constants.EntityNames.PLAYER).ifPresent(playerEntity -> {
        TransformComponent cameraTransform = cameraEntity.getComponent(TransformComponent.class);
        TransformComponent playerTransform = playerEntity.getComponent(TransformComponent.class);

        cameraTransform.setTranslate(
          playerTransform.getTranslate().subtract(new Vector2D(AcidRainSola.HALF_CANVAS_WIDTH, AcidRainSola.HALF_CANVAS_HEIGHT))
        );
      });
    });
  }
}
