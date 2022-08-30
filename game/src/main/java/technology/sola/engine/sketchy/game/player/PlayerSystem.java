package technology.sola.engine.sketchy.game.player;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.event.EventHub;
import technology.sola.engine.input.Key;
import technology.sola.engine.input.KeyboardInput;
import technology.sola.engine.physics.event.CollisionManifoldEvent;
import technology.sola.engine.sketchy.game.Constants;
import technology.sola.engine.sketchy.game.chunk.TileComponent;
import technology.sola.math.linear.Vector2D;

public class PlayerSystem extends EcsSystem {
  private final KeyboardInput keyboardInput;

  public PlayerSystem(EventHub eventHub, KeyboardInput keyboardInput) {
    this.keyboardInput = keyboardInput;

    eventHub.add(collisionManifoldEvent -> collisionManifoldEvent.getMessage().conditionallyResolveCollision(
      entity -> Constants.EntityNames.PLAYER.equals(entity.getName()),
      entity -> {
        TileComponent tileComponent = entity.getComponent(TileComponent.class);
        if (tileComponent != null) {
          return tileComponent.getTileType().assetId.equals(Constants.Assets.Sprites.CLIFF);
        }
        return false;
      },
      (player, erasedTile) -> {
        // todo adjust player position
        System.out.println("cliff collision");
      }
    ), CollisionManifoldEvent.class);
  }

  @Override
  public void update(World world, float dt) {
    world.findEntityByName(Constants.EntityNames.PLAYER).ifPresent(entity -> {
      PlayerComponent playerComponent = entity.getComponent(PlayerComponent.class);
      TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
      float speed = playerComponent.getSpeed();

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

      playerComponent.setUsingSunlight(keyboardInput.isKeyHeld(Key.SPACE));

      if (playerComponent.isUsingSunlight()) {
        playerComponent.useSunlight();
      }
    });
  }
}
