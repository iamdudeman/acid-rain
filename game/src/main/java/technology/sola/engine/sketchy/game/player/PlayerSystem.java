package technology.sola.engine.sketchy.game.player;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.Entity;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.input.Key;
import technology.sola.engine.input.KeyboardInput;
import technology.sola.engine.sketchy.game.EntityNames;
import technology.sola.math.linear.Vector2D;

public class PlayerSystem extends EcsSystem {
  private final KeyboardInput keyboardInput;
  private final float speed = 50f;

  public PlayerSystem(KeyboardInput keyboardInput) {
    this.keyboardInput = keyboardInput;
  }

  @Override
  public void update(World world, float dt) {
    world.findEntityByName(EntityNames.PLAYER).ifPresent(entity -> {
      TransformComponent transformComponent = entity.getComponent(TransformComponent.class);

      if (keyboardInput.isKeyHeld(Key.W)) {
        transformComponent.setTranslate(
          transformComponent.getTranslate().add(new Vector2D(0, -speed).scalar(dt))
        );
      }

      if (keyboardInput.isKeyHeld(Key.S)) {
        transformComponent.setTranslate(
          transformComponent.getTranslate().add(new Vector2D(0, speed).scalar(dt))
        );
      }

      if (keyboardInput.isKeyHeld(Key.A)) {
        transformComponent.setTranslate(
          transformComponent.getTranslate().add(new Vector2D(-speed, 0).scalar(dt))
        );
      }

      if (keyboardInput.isKeyHeld(Key.D)) {
        transformComponent.setTranslate(
          transformComponent.getTranslate().add(new Vector2D(speed, 0).scalar(dt))
        );
      }
    });
  }
}
