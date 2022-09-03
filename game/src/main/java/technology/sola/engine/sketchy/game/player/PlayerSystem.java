package technology.sola.engine.sketchy.game.player;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.Entity;
import technology.sola.ecs.SolaEcs;
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
import technology.sola.engine.sketchy.game.SpriteCache;
import technology.sola.engine.sketchy.game.chunk.TileComponent;
import technology.sola.engine.sketchy.game.chunk.TileType;
import technology.sola.engine.sketchy.game.rain.RainSystem;
import technology.sola.engine.sketchy.game.sunlight.SunlightBarComponent;
import technology.sola.math.linear.Vector2D;

import java.util.Optional;

public class PlayerSystem extends EcsSystem {
  private final KeyboardInput keyboardInput;
  private final MouseInput mouseInput;
  private long lastQuack = System.currentTimeMillis();

  public PlayerSystem(SolaEcs solaEcs, EventHub eventHub, KeyboardInput keyboardInput, MouseInput mouseInput, AssetLoader<AudioClip> audioClipAssetLoader) {
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
        //TODO: Any better way to do this?
        Optional<Entity> sunlightEntityOptional = solaEcs.getWorld().findEntityByName(Constants.EntityNames.SUNLIGHT);
        sunlightEntityOptional.ifPresent(entity -> {
          Optional<SunlightBarComponent> sunlightBarComponent = entity.getOptionalComponent(SunlightBarComponent.class);
          sunlightBarComponent.ifPresent(SunlightBarComponent::incrementSunlight);
          });
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
        PlayerMovement manipulations = manipulateModsByMouse(xMod, yMod);
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

      playerComponent.resetSlowed();
    });
  }

  private PlayerMovement manipulateModsByMouse(int xMod, int yMod)
  {
/*      cursor click box map
     _____________________________________________________
    |  ↖  |  ↖  |  ↖  |  ↑  |  ↑  |  ↑  |  ↗  |  ↗  |  ↗  |
    +-----+-----+-----+-----+-----+-----+-----+-----+-----+
    |  ↖  |  ↖  |  ↖  |  ↑  |  ↑  |  ↑  |  ↗  |  ↗  |  ↗  |
    +-----+-----+-----+-----+-----+-----+-----+-----+-----+
    |  ↖  |  ↖  |  ↖  |  ↑  |  ↑  |  ↑  |  ↗  |  ↗  |  ↗  |
    +-----+-----+-----+-----+-----+-----+-----+-----+-----+
    |  ←  |  ←  |  ←  |  ↖  |  ↑  |  ↗  |  →  |  →  |  →  |
    +-----+-----+-----+-----+-----+-----+-----+-----+-----+
    |  ←  |  ←  |  ←  |  ←  |duck |  →  |  →  |  →  |  →  |
    +-----+-----+-----+-----+-----+-----+-----+-----+-----+
    |  ←  |  ←  |  ←  |  ↙  |  ↓  |  ↘  |  →  |  →  |  →  |
    +-----+-----+-----+-----+-----+-----+-----+-----+-----+
    |  ↙  |  ↙  |  ↙  |  ↓  |  ↓  |  ↓  |  ↘  |  ↘  |  ↘  |
    +-----+-----+-----+-----+-----+-----+-----+-----+-----+
    |  ↙  |  ↙  |  ↙  |  ↓  |  ↓  |  ↓  |  ↘  |  ↘  |  ↘  |
    +-----+-----+-----+-----+-----+-----+-----+-----+-----+
    |  ↙  |  ↙  |  ↙  |  ↓  |  ↓  |  ↓  |  ↘  |  ↘  |  ↘  |
    |_____________________________________________________|
*/
//  9x9 - if we want click actions near the duck
    float tileWidth = 480f / 9; // TODO Tim make consts
    float tileHeight = 320f / 9;
    Vector2D gridPosition = getGridPosition(tileWidth, tileHeight);
    int x = (int) gridPosition.x;
    int y = (int) gridPosition.y;
    boolean isInDuckX = x >= 3 && x <= 5;
    boolean isInDuckY = y >= 3 && y <= 5;
    if (y < 3 || (y == 3 && isInDuckX)) {
      //move up
      yMod--;
    }
    if (y >= 6  || (y == 5 && isInDuckX)) {
      //move down
      yMod++;
    }
    if (x < 3 || (x == 3 && isInDuckY)) {
      //move left
      xMod--;
    }
    if (x > 5 || (x == 5 && isInDuckY)) {
      //move right
      xMod++;
    }
//    3x3 grid if we want simpler logic and don't think the user will click near the duck
//    Vector2D gridPosition = getGridPosition(160, 106.66F);
//    if (gridPosition.y == 0) {
//      //move up
//      yMod--;
//    }
//    if (gridPosition.y == 2) {
//      //move down
//      yMod++;
//    }
//    if (gridPosition.x == 0) {
//      //move left
//      xMod--;
//    }
//    if (gridPosition.x == 2) {
//      //move right
//      xMod++;
//    }

    return new PlayerMovement(xMod, yMod);
  }

  public Vector2D getGridPosition(float tileWidth, float tileHeight)
  {
    Vector2D mousePosition = mouseInput.getMousePosition();
    return new Vector2D(mousePosition.x / tileWidth, mousePosition.y / tileHeight);
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

