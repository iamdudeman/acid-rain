package technology.sola.acidrain.game.system;

import technology.sola.acidrain.game.event.GameStatEvent;
import technology.sola.acidrain.game.event.GameStatType;
import technology.sola.acidrain.game.event.GameState;
import technology.sola.acidrain.game.event.GameStateEvent;
import technology.sola.acidrain.game.component.PickupComponent;
import technology.sola.acidrain.game.component.PlayerComponent;
import technology.sola.acidrain.game.GameStatistics;
import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.Entity;
import technology.sola.ecs.World;
import technology.sola.engine.assets.AssetLoader;
import technology.sola.engine.assets.audio.AudioClip;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.event.EventHub;
import technology.sola.engine.graphics.components.SpriteComponent;
import technology.sola.engine.input.Key;
import technology.sola.engine.input.KeyboardInput;
import technology.sola.engine.input.MouseButton;
import technology.sola.engine.input.MouseInput;
import technology.sola.acidrain.game.Constants;
import technology.sola.acidrain.game.AcidRainSola;
import technology.sola.acidrain.game.SpriteCache;
import technology.sola.acidrain.game.component.TileComponent;
import technology.sola.acidrain.game.system.chunk.TileType;
import technology.sola.engine.physics.event.SensorEvent;
import technology.sola.math.linear.Vector2D;

public class PlayerSystem extends EcsSystem {
  private static final float TOUCH_TILE_WIDTH = AcidRainSola.CANVAS_WIDTH / 9f;
  private static final float TOUCH_TILE_HEIGHT = AcidRainSola.CANVAS_HEIGHT / 9f;
  private static final int SUNLIGHT_BAR_HEIGHT = 12;
  private static final int TOUCH_CONTROLS_POWER_THRESHOLD = AcidRainSola.CANVAS_HEIGHT - SUNLIGHT_BAR_HEIGHT - 8;
  private final EventHub eventHub;
  private final KeyboardInput keyboardInput;
  private final MouseInput mouseInput;
  private long lastQuack = System.currentTimeMillis();
  private PlayerMovement previousMouseMovement = null;
  private Vector2D previousTranslate = null;

  public PlayerSystem(EventHub eventHub, KeyboardInput keyboardInput, MouseInput mouseInput, AssetLoader<AudioClip> audioClipAssetLoader) {
    this.eventHub = eventHub;
    this.keyboardInput = keyboardInput;
    this.mouseInput = mouseInput;

    eventHub.add(SensorEvent.class, collisionManifoldEvent -> collisionManifoldEvent.collisionManifold().conditionallyResolveCollision(
      entity -> Constants.EntityNames.PLAYER.equals(entity.getName()),
      entity -> entity.hasComponent(TileComponent.class),
      (player, tileEntity) -> {
        TileComponent tileComponent = tileEntity.getComponent(TileComponent.class);
        TileType tileType = tileComponent.getTileType();

        if (tileComponent.getWetness() > RainSystem.THRESHOLD_EIGHT) {
          eventHub.emit(new GameStateEvent(GameState.GAME_OVER));
        } else if (tileType.assetId.equals(Constants.Assets.Sprites.DIRT)) {
          PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);

          if (tileComponent.getWetness() > RainSystem.THRESHOLD_THREE) {
            playerComponent.setIsSuperSlowed(true);
          } else {
            playerComponent.setIsSlowed(true);
          }
        }
      }
    ));

    eventHub.add(SensorEvent.class, collisionManifoldEvent -> collisionManifoldEvent.collisionManifold().conditionallyResolveCollision(
      entity -> Constants.EntityNames.PLAYER.equals(entity.getName()),
      entity -> entity.hasComponent(PickupComponent.class),
      (player, pickup) -> {
        audioClipAssetLoader.get(Constants.Assets.Audio.QUACK).executeIfLoaded(quack -> {
          long now = System.currentTimeMillis();
          if (lastQuack + 800 < now) {
            quack.stop();
            quack.play();
            lastQuack = now;
          }
        });

        GameStatistics.incrementDonutsConsumed();
        player.getComponent(PlayerComponent.class).pickupDonut();
        pickup.getComponent(PickupComponent.class).hostTile().consumePickup();
        pickup.destroy();
      }
    ));

    eventHub.add(GameStateEvent.class, gameStateEvent -> {
      if (gameStateEvent.gameState() == GameState.RESTART) {
        setActive(true);
        previousTranslate = null;
        previousMouseMovement = null;
      } else if (gameStateEvent.gameState() == GameState.GAME_OVER) {
        setActive(false);
      }
    });
  }

  @Override
  public void update(World world, float dt) {
    Entity playerEntity = world.findEntityByName(Constants.EntityNames.PLAYER);

    if (playerEntity != null) {
      if (previousTranslate == null) {
        previousTranslate = playerEntity.getComponent(TransformComponent.class).getTranslate();
      }

      Vector2D currentTranslate = playerEntity.getComponent(TransformComponent.class).getTranslate();

      GameStatistics.increaseDistanceTraveled(currentTranslate.distance(previousTranslate));
      previousTranslate = currentTranslate;

      PlayerComponent playerComponent = playerEntity.getComponent(PlayerComponent.class);
      int xMod = 0;
      int yMod = 0;

      if (keyboardInput.isKeyHeld(Key.W) || keyboardInput.isKeyHeld(Key.UP)) {
        yMod--;
        previousMouseMovement = null;
      }

      if (keyboardInput.isKeyHeld(Key.S) || keyboardInput.isKeyHeld(Key.DOWN)) {
        yMod++;
        previousMouseMovement = null;
      }

      if (keyboardInput.isKeyHeld(Key.A) || keyboardInput.isKeyHeld(Key.LEFT)) {
        xMod--;
        previousMouseMovement = null;
      }

      if (keyboardInput.isKeyHeld(Key.D) || keyboardInput.isKeyHeld(Key.RIGHT)) {
        xMod++;
        previousMouseMovement = null;
      }

      if (mouseInput.isMouseClicked(MouseButton.PRIMARY)) {
        PlayerMovement temp = manipulateModsByMouse();

        if (temp != null) {
          previousMouseMovement = temp;
        }
      }

      if (previousMouseMovement != null) {
        xMod = previousMouseMovement.xMod();
        yMod = previousMouseMovement.yMod();
      }

      if (xMod != 0 || yMod != 0) {
        TransformComponent transformComponent = playerEntity.getComponent(TransformComponent.class);
        SpriteComponent theDuck = playerEntity.getComponent(SpriteComponent.class);
        String variation = getSpriteVariation(xMod, yMod);
        float speed = playerComponent.getSpeed();

        Vector2D velocity = new Vector2D(xMod * speed, yMod * speed).scalar(dt);

        transformComponent.setTranslate(transformComponent.getTranslate().add(velocity));
        theDuck.setSpriteKeyFrame(SpriteCache.get(Constants.Assets.Sprites.DUCK, variation));
      }

      if (keyboardInput.isKeyPressed(Key.SPACE) || (mouseInput.isMouseClicked(MouseButton.PRIMARY) && mouseInput.getMousePosition().y() > (TOUCH_CONTROLS_POWER_THRESHOLD))) {
        playerComponent.setUsingSunlight(!playerComponent.isUsingSunlight());
      }

      if (playerComponent.isUsingSunlight()) {
        playerComponent.useSunlight();
      }

      playerComponent.resetSlowed();

      eventHub.emit(new GameStatEvent(GameStatType.SUNLIGHT, playerComponent.getSunlight()));
    }
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

    if (mousePosition.y() > TOUCH_CONTROLS_POWER_THRESHOLD) {
      return null;
    }

    int x = (int) (mousePosition.x() / TOUCH_TILE_WIDTH);
    int y = (int) (mousePosition.y() / TOUCH_TILE_HEIGHT);
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

