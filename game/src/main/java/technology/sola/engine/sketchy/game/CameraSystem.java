package technology.sola.engine.sketchy.game;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.math.linear.Vector2D;

public class CameraSystem extends EcsSystem {
  private final float rendererHalfWidth;
  private final float rendererHalfHeight;

  public CameraSystem(int rendererWidth, int rendererHeight) {
    this.rendererHalfWidth = rendererWidth / 2f;
    this.rendererHalfHeight = rendererHeight / 2f;
  }

  @Override
  public void update(World world, float dt) {
    world.findEntityByName(EntityNames.CAMERA).ifPresent(cameraEntity -> {
      world.findEntityByName(EntityNames.PLAYER).ifPresent(playerEntity -> {
        TransformComponent cameraTransform = cameraEntity.getComponent(TransformComponent.class);
        TransformComponent playerTransform = playerEntity.getComponent(TransformComponent.class);

        cameraTransform.setTranslate(
          playerTransform.getTranslate().subtract(new Vector2D(rendererHalfWidth, rendererHalfHeight))
        );
      });
    });
  }
}
