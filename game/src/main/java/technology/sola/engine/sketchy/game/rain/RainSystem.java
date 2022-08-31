package technology.sola.engine.sketchy.game.rain;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.graphics.components.sprite.SpriteComponent;
import technology.sola.engine.physics.component.ColliderComponent;
import technology.sola.engine.sketchy.game.Constants;
import technology.sola.engine.sketchy.game.SpriteCache;
import technology.sola.engine.sketchy.game.chunk.Chunk;
import technology.sola.engine.sketchy.game.chunk.TileComponent;
import technology.sola.engine.sketchy.game.chunk.TileType;
import technology.sola.engine.sketchy.game.player.PlayerComponent;
import technology.sola.math.linear.Vector2D;

import java.util.Random;

public class RainSystem extends EcsSystem {
  // todo tune these numbers
  public static final int THRESHOLD_FIVE = 300;
  public static final int THRESHOLD_FOUR = 250;
  public static final int THRESHOLD_THREE = 195;
  public static final int THRESHOLD_TWO = 115;
  public static final int THRESHOLD_ONE = 50;

  private final Random random = new Random();
  private final int maxDropsPerUpdate = 40;
  private final int rendererWidth;
  private final int rendererHeight;
  private int dropsPerUpdate = maxDropsPerUpdate;

  public RainSystem(int rendererWidth, int rendererHeight) {
    this.rendererWidth = rendererWidth;
    this.rendererHeight = rendererHeight;
  }

  @Override
  public void update(World world, float dt) {
    var playerEntityOptional = world.findEntityByName(Constants.EntityNames.PLAYER);

    playerEntityOptional.ifPresentOrElse(
      playerEntity -> {
        Vector2D playerTranslate = playerEntity.getComponent(TransformComponent.class).getTranslate();
        PlayerComponent playerComponent = playerEntity.getComponent(PlayerComponent.class);

        updateDropsPerUpdateForSunlight(playerComponent.isUsingSunlight());
        updateRainHeight(world, true);

        if (!playerComponent.isUsingSunlight()) {
          createNewRain(world);
          updateTileWetness(world, playerTranslate);
        }
      },
      () -> {
        updateDropsPerUpdateForSunlight(false);
        updateRainHeight(world, false);
        createNewRain(world);
        updateTileWetness(world, null);
      }
    );
  }

  private void updateRainHeight(World world, boolean showAnimation) {
    for (var view : world.createView().of(RainComponent.class)) {
      RainComponent rainComponent = view.c1();

      rainComponent.height--;

      int heightThreshold = showAnimation ? RainRenderer.RAIN_ANIMATION_HEIGHT_THRESHOLD_2 - 2 : 0;

      if (rainComponent.height <= heightThreshold) {
        view.entity().destroy();
      }
    }
  }

  private void createNewRain(World world) {
    world.findEntityByName(Constants.EntityNames.CAMERA).ifPresent(cameraEntity -> {
      TransformComponent cameraTransform = cameraEntity.getComponent(TransformComponent.class);

      createRain(world, cameraTransform.getX(), cameraTransform.getY());
    });
  }

  private void createRain(World world, float cameraX, float cameraY) {
    final int edge = 200;

    for (int i = 0; i < dropsPerUpdate; i++) {
      float x = random.nextFloat(-edge, rendererWidth + edge);
      float y = random.nextFloat(-edge, rendererHeight + edge);

      world.createEntity(
        new RainComponent(x + cameraX, y + cameraY)
      );
    }
  }

  private void updateTileWetness(World world, Vector2D playerTranslate) {
    for (var view : world.createView().of(TileComponent.class, SpriteComponent.class, TransformComponent.class)) {
      TileComponent tileComponent = view.c1();
      SpriteComponent spriteComponent = view.c2();
      TransformComponent transformComponent = view.c3();

      float distance = playerTranslate == null ? 40 : playerTranslate.distance(transformComponent.getTranslate());
      float threshold = 0.25f;

      // todo tune these numbers
      if (distance < 20) {
        threshold = 0.85f;
      } else if (distance < 50) {
        threshold = 0.5f;
      }

      if (random.nextFloat() < threshold) {
        tileComponent.increaseWetness();
      }

      if (tileComponent.getWetness() > THRESHOLD_FIVE) {
        if (!spriteComponent.getSpriteId().equals(Constants.Assets.Sprites.ERASED)) {
          spriteComponent.setSpriteKeyFrame(SpriteCache.ERASED);
          view.entity().addComponent(ColliderComponent.circle(Chunk.HALF_TILE_SIZE));
        }
      } else {

        if (tileComponent.getTileType().isErasable) {
          if (tileComponent.getWetness() > THRESHOLD_FOUR) {
            spriteComponent.setSpriteKeyFrame(
              SpriteCache.get(tileComponent.getTileType().assetId, "5")
            );
          } else if (tileComponent.getWetness() > THRESHOLD_THREE) {
            spriteComponent.setSpriteKeyFrame(
              SpriteCache.get(tileComponent.getTileType().assetId, "4")
            );
            if (tileComponent.getTileType() == TileType.DIRT) {
              view.entity().addComponent(ColliderComponent.circle(Chunk.HALF_TILE_SIZE));
            }
          } else if (tileComponent.getWetness() > THRESHOLD_TWO) {
            spriteComponent.setSpriteKeyFrame(
              SpriteCache.get(tileComponent.getTileType().assetId, "3")
            );
          } else if (tileComponent.getWetness() > THRESHOLD_ONE) {
            spriteComponent.setSpriteKeyFrame(
              SpriteCache.get(tileComponent.getTileType().assetId, "2")
            );
          }
        }
      }
    }
  }

  private void updateDropsPerUpdateForSunlight(boolean isDecreasing) {
    if (isDecreasing) {
      if (dropsPerUpdate > 0) {
        dropsPerUpdate--;
      }
    } else {
      if (dropsPerUpdate < maxDropsPerUpdate) {
        dropsPerUpdate++;
      }
    }
  }
}
