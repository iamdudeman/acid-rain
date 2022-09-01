package technology.sola.engine.sketchy.game.player;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.event.EventHub;
import technology.sola.engine.graphics.components.sprite.SpriteComponent;
import technology.sola.engine.input.Key;
import technology.sola.engine.input.KeyboardInput;
import technology.sola.engine.physics.CollisionManifold;
import technology.sola.engine.physics.event.CollisionManifoldEvent;
import technology.sola.engine.sketchy.game.Constants;
import technology.sola.engine.sketchy.game.SpriteCache;
import technology.sola.engine.sketchy.game.chunk.TileComponent;
import technology.sola.engine.sketchy.game.chunk.TileType;
import technology.sola.math.linear.Vector2D;

public class PlayerSystem extends EcsSystem {
  private final KeyboardInput keyboardInput;

  public PlayerSystem(EventHub eventHub, KeyboardInput keyboardInput) {
    this.keyboardInput = keyboardInput;

    eventHub.add(collisionManifoldEvent -> collisionManifoldEvent.getMessage().conditionallyResolveCollision(
      entity -> Constants.EntityNames.PLAYER.equals(entity.getName()),
      entity -> entity.hasComponent(TileComponent.class),
      (player, erasedTile) -> {
        TileComponent tileComponent = erasedTile.getComponent(TileComponent.class);

        TileType tileType = tileComponent.getTileType();

        if (tileType.assetId.equals(Constants.Assets.Sprites.CLIFF)) {
          CollisionManifold collisionManifold = collisionManifoldEvent.getMessage();
          int scalar = collisionManifold.entityA() == player ? -1 : 1;
          TransformComponent playerTransform = player.getComponent(TransformComponent.class);

          playerTransform.setTranslate(
            playerTransform.getTranslate().add(collisionManifold.normal().scalar(scalar * collisionManifold.penetration()))
          );
        } else if (tileType.assetId.equals(Constants.Assets.Sprites.DIRT)) {
          player.getComponent(PlayerComponent.class).setIsSlowed(true);
        }
      }
    ), CollisionManifoldEvent.class);

    eventHub.add(collisionManifoldEvent -> collisionManifoldEvent.getMessage().conditionallyResolveCollision(
      entity -> Constants.EntityNames.PLAYER.equals(entity.getName()),
      entity -> entity.hasComponent(PickupComponent.class),
      (player, pickup) -> {
        player.getComponent(PlayerComponent.class).pickupDonut();
        pickup.getComponent(PickupComponent.class).hostTile().consumePickup();
        pickup.destroy();
      }
    ), CollisionManifoldEvent.class);
  }

  @Override
  public void update(World world, float dt) {
    world.findEntityByName(Constants.EntityNames.PLAYER).ifPresent(entity -> {
      PlayerComponent playerComponent = entity.getComponent(PlayerComponent.class);
      int xMod = 0;
      int yMod = 0;

      if (keyboardInput.isKeyHeld(Key.W)) {
        yMod--;
      }

      if (keyboardInput.isKeyHeld(Key.S)) {
        yMod++;
      }

      if (keyboardInput.isKeyHeld(Key.A)) {
        xMod--;
      }

      if (keyboardInput.isKeyHeld(Key.D)) {
        xMod++;
      }

      if (xMod != 0 || yMod != 0) {
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        SpriteComponent theDuck = entity.getComponent(SpriteComponent.class);
        String variation = getSpriteVariation(xMod, yMod);
        float speed = playerComponent.getSpeed();

        Vector2D velocity = new Vector2D(xMod * speed, yMod * speed).scalar(dt);
        transformComponent.setTranslate(transformComponent.getTranslate().add(velocity));
        theDuck.setSpriteKeyFrame(SpriteCache.get(Constants.Assets.Sprites.DUCK, variation));
      }

      playerComponent.setUsingSunlight(keyboardInput.isKeyHeld(Key.SPACE));

      if (playerComponent.isUsingSunlight()) {
        playerComponent.useSunlight();
      }

      playerComponent.setIsSlowed(false);
    });
  }

  private String getSpriteVariation(int xMod, int yMod) {
    if (xMod == 0) {
      return yMod < 0 ? "top" : "bottom";
    } else if (xMod > 0) {
      if (yMod == 0) {
        return "right";
      } else if (yMod < 0) {
        return "top-right";
      } else {
        return "bottom-right";
      }
    } else {
      if (yMod == 0) {
        return "left";
      } else if (yMod < 0) {
        return "top-left";
      } else {
        return "bottom-left";
      }
    }
  }
}
