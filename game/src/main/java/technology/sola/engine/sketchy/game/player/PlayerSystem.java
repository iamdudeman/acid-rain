package technology.sola.engine.sketchy.game.player;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.assets.AssetLoader;
import technology.sola.engine.assets.audio.AudioClip;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.event.EventHub;
import technology.sola.engine.graphics.components.sprite.SpriteComponent;
import technology.sola.engine.input.Key;
import technology.sola.engine.input.KeyboardInput;
import technology.sola.engine.input.MouseButton;
import technology.sola.engine.input.MouseInput;
import technology.sola.engine.physics.CollisionManifold;
import technology.sola.engine.physics.event.CollisionManifoldEvent;
import technology.sola.engine.sketchy.game.Constants;
import technology.sola.engine.sketchy.game.SketchyLifeSola;
import technology.sola.engine.sketchy.game.SpriteCache;
import technology.sola.engine.sketchy.game.chunk.TileComponent;
import technology.sola.engine.sketchy.game.chunk.TileType;
import technology.sola.engine.sketchy.game.rain.RainSystem;
import technology.sola.math.linear.Vector2D;

public class PlayerSystem extends EcsSystem {
  private static final float TOUCH_TILE_WIDTH = SketchyLifeSola.CANVAS_WIDTH / 9f;
  private static final float TOUCH_TILE_HEIGHT = SketchyLifeSola.CANVAS_HEIGHT / 9f;
  private final KeyboardInput keyboardInput;
  private final MouseInput mouseInput;
  private long lastQuack = System.currentTimeMillis();


  public PlayerSystem(EventHub eventHub, KeyboardInput keyboardInput, MouseInput mouseInput, AssetLoader<AudioClip> audioClipAssetLoader) {
    this.keyboardInput = keyboardInput;
    this.mouseInput = mouseInput;

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

          audioClipAssetLoader.get(Constants.Assets.Audio.QUACK).executeIfLoaded(quack -> {
            long now = System.currentTimeMillis();
            if (lastQuack + 1000 < now) {
              quack.stop();
              quack.play();
              lastQuack = now;
            }
          });
        } else if (tileType.assetId.equals(Constants.Assets.Sprites.DIRT)) {
          PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);

          if (tileComponent.getWetness() > RainSystem.THRESHOLD_THREE) {
            playerComponent.setIsSuperSlowed(true);
          } else {
            playerComponent.setIsSlowed(true);
          }
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

      if (mouseInput.isMouseDragged(MouseButton.PRIMARY)) {
        PlayerMovement manipulations = manipulateModsByMouse();
        xMod = manipulations.xMod();
        yMod = manipulations.yMod();
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

      playerComponent.resetSlowed();
    });
  }

  private PlayerMovement manipulateModsByMouse() {
    /*      cursor click box map
         _____________________________________________________
        |  ↖  |  ↖  |  ↖  |  ↑  |  ↑  |  ↑  |  ↗  |  ↗  |  ↗  |
        +-----+-----+-----+-----+-----+-----+-----+-----+-----+
        |  ↖  |  ↖  |  ↖  |  ↑  |  ↑  |  ↑  |  ↗  |  ↗  |  ↗  |
        +-----+-----+-----+-----+-----+-----+-----+-----+-----+
        |  ↖  |  ↖  |  ↖  |  ↑  |  ↑  |  ↑  |  ↗  |  ↗  |  ↗  |
        +-----+-----+-----+-----+-----+-----+-----+-----+-----+
        |  <  |  <  |  <  |  ↖  |  ↑  |  ↗  |  →  |  →  |  →  |
        +-----+-----+-----+-----+-----+-----+-----+-----+-----+
        |  <  |  <  |  <  |  <  |duck |  →  |  →  |  →  |  →  |
        +-----+-----+-----+-----+-----+-----+-----+-----+-----+
        |  <  |  <  |  <  |  ↙  |  ↓  |  ↘  |  →  |  →  |  →  |
        +-----+-----+-----+-----+-----+-----+-----+-----+-----+
        |  ↙  |  ↙  |  ↙  |  ↓  |  ↓  |  ↓  |  ↘  |  ↘  |  ↘  |
        +-----+-----+-----+-----+-----+-----+-----+-----+-----+
        |  ↙  |  ↙  |  ↙  |  ↓  |  ↓  |  ↓  |  ↘  |  ↘  |  ↘  |
        +-----+-----+-----+-----+-----+-----+-----+-----+-----+
        |  ↙  |  ↙  |  ↙  |  ↓  |  ↓  |  ↓  |  ↘  |  ↘  |  ↘  |
        |_____________________________________________________|
    */

    int xMod = 0;
    int yMod = 0;
    Vector2D mousePosition = mouseInput.getMousePosition();

    int x = (int) (mousePosition.x / TOUCH_TILE_WIDTH);
    int y = (int) (mousePosition.y / TOUCH_TILE_HEIGHT);
    boolean isInDuckX = x >= 3 && x <= 5;
    boolean isInDuckY = y >= 3 && y <= 5;

    if (y < 3 || (y == 3 && isInDuckX)) {
      yMod--;
    }

    if (y >= 6 || (y == 5 && isInDuckX)) {
      yMod++;
    }

    if (x < 3 || (x == 3 && isInDuckY)) {
      xMod--;
    }

    if (x > 5 || (x == 5 && isInDuckY)) {
      xMod++;
    }

    return new PlayerMovement(xMod, yMod);
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

  private record PlayerMovement(int xMod, int yMod) {
  }
}

