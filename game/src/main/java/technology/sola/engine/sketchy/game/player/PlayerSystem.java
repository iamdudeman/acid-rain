package technology.sola.engine.sketchy.game.player;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.event.EventHub;
import technology.sola.engine.event.EventListener;
import technology.sola.engine.graphics.components.sprite.SpriteComponent;
import technology.sola.engine.input.Key;
import technology.sola.engine.input.KeyboardInput;
import technology.sola.engine.physics.event.CollisionManifoldEvent;
import technology.sola.engine.sketchy.game.Constants;
import technology.sola.engine.sketchy.game.event.GameState;
import technology.sola.engine.sketchy.game.event.GameStateEvent;
import technology.sola.math.linear.Vector2D;

public class PlayerSystem extends EcsSystem implements EventListener<CollisionManifoldEvent> {
  private final EventHub eventHub;
  private final KeyboardInput keyboardInput;
  private final float speed = 50f;

  public PlayerSystem(EventHub eventHub, KeyboardInput keyboardInput) {
    this.eventHub = eventHub;
    this.keyboardInput = keyboardInput;
  }

  @Override
  public void update(World world, float dt) {
    world.findEntityByName(Constants.EntityNames.PLAYER).ifPresent(entity -> {
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

  @Override
  public void onEvent(CollisionManifoldEvent collisionManifoldEvent) {
    System.out.println("collision event");

    collisionManifoldEvent.getMessage().conditionallyResolveCollision(
      entity -> Constants.EntityNames.PLAYER.equals(entity.getName()),
      entity -> {
        SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
        if (spriteComponent != null) {
          return spriteComponent.getSpriteId().equals(Constants.Assets.Sprites.ERASED);
        }
        return false;
      },
      (player, erasedTile) -> {
        System.out.println("woot");
        eventHub.emit(new GameStateEvent(GameState.GAME_OVER));
      }
    );

  }
}
