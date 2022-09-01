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
        player.getComponent(PlayerComponent.class).pickupSunlight();
        pickup.getComponent(PickupComponent.class).hostTile().consumePickup();
        pickup.destroy();
      }
    ), CollisionManifoldEvent.class);
  }

  @Override
  public void update(World world, float dt) {
    world.findEntityByName(Constants.EntityNames.PLAYER).ifPresent(entity -> {
      PlayerComponent playerComponent = entity.getComponent(PlayerComponent.class);
      TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
      SpriteComponent theDuck = entity.getComponent(SpriteComponent.class);
      float speed = playerComponent.getSpeed();

      boolean wentUp = false;
      boolean wentDown = false;
      boolean wentLeft = false;
      boolean wentRight = false;

      if (keyboardInput.isKeyHeld(Key.W)) {
        wentUp = true;
        transformComponent.setTranslate(
          transformComponent.getTranslate().add(new Vector2D(0, -speed).scalar(dt))
        );
      }

      if (keyboardInput.isKeyHeld(Key.S)) {
        wentDown = true;
        transformComponent.setTranslate(
          transformComponent.getTranslate().add(new Vector2D(0, speed).scalar(dt))
        );
      }

      if (keyboardInput.isKeyHeld(Key.A)) {
        wentLeft = true;
        transformComponent.setTranslate(
          transformComponent.getTranslate().add(new Vector2D(-speed, 0).scalar(dt))
        );
      }

      if (keyboardInput.isKeyHeld(Key.D)) {
        wentRight = true;
        transformComponent.setTranslate(
          transformComponent.getTranslate().add(new Vector2D(speed, 0).scalar(dt))
        );
      }

      if (wentUp) {
        theDuck.setSpriteKeyFrame(SpriteCache.get(Constants.Assets.Sprites.DUCK, "top"));
      }

      if (wentDown) {
        theDuck.setSpriteKeyFrame(SpriteCache.get(Constants.Assets.Sprites.DUCK, "bottom"));
      }

      if (wentLeft) {
        if (wentUp) {
          theDuck.setSpriteKeyFrame(SpriteCache.get(Constants.Assets.Sprites.DUCK, "top-left"));
        } else if (wentDown) {
          theDuck.setSpriteKeyFrame(SpriteCache.get(Constants.Assets.Sprites.DUCK, "bottom-left"));
        } else {
          theDuck.setSpriteKeyFrame(SpriteCache.get(Constants.Assets.Sprites.DUCK, "left"));
        }
      }

      if (wentRight) {
        if (wentUp) {
          theDuck.setSpriteKeyFrame(SpriteCache.get(Constants.Assets.Sprites.DUCK, "top-right"));
        } else if (wentDown) {
          theDuck.setSpriteKeyFrame(SpriteCache.get(Constants.Assets.Sprites.DUCK, "bottom-right"));
        } else {
          theDuck.setSpriteKeyFrame(SpriteCache.get(Constants.Assets.Sprites.DUCK, "right"));
        }
      }

      playerComponent.setUsingSunlight(keyboardInput.isKeyHeld(Key.SPACE));

      if (playerComponent.isUsingSunlight()) {
        playerComponent.useSunlight();
      }

      playerComponent.setIsSlowed(false);
    });
  }
}
