package technology.sola.acidrain.game.system;

import technology.sola.acidrain.game.component.RainComponent;
import technology.sola.acidrain.game.rendering.RainRenderer;
import technology.sola.acidrain.game.GameStatistics;
import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.graphics.components.sprite.SpriteComponent;
import technology.sola.engine.physics.component.ColliderComponent;
import technology.sola.acidrain.game.Constants;
import technology.sola.acidrain.game.AcidRainSola;
import technology.sola.acidrain.game.SpriteCache;
import technology.sola.acidrain.game.system.chunk.Chunk;
import technology.sola.acidrain.game.component.TileComponent;
import technology.sola.acidrain.game.component.PlayerComponent;
import technology.sola.math.linear.Vector2D;

import java.util.Random;

public class RainSystem extends EcsSystem {
  public static final int THRESHOLD_ONE = 50;
  public static final int THRESHOLD_TWO = THRESHOLD_ONE + 105;
  public static final int THRESHOLD_THREE = THRESHOLD_TWO + 60;
  public static final int THRESHOLD_FOUR = THRESHOLD_THREE + 80;
  public static final int THRESHOLD_FIVE = THRESHOLD_FOUR + 15;
  public static final int THRESHOLD_SIX = THRESHOLD_FIVE + 10;
  public static final int THRESHOLD_SEVEN = THRESHOLD_SIX + 10;
  public static final int THRESHOLD_EIGHT = THRESHOLD_SEVEN + 5;
  private static final float COMMON_WETNESS_THRESHOLD = 0.25f;
  private static final float SOMEWHAT_CLOSE_WETNESS_THRESHOLD = 0.5f;
  private static final float FAR_WETNESS_THRESHOLD = 0.05f;
  private static final float SOMEWHAT_CLOSE_WETNESS_DISTANCE = 150;
  private static final float FAR_WETNESS_DISTANCE = 400;
  private static final float CLOSE_WETNESS_THRESHOLD = 0.85f;
  private static final float CLOSE_WETNESS_DISTANCE = 50;
  private static final int BASE_DROPS_PER_UPDATE = 10;
  private final Random random = new Random();
  private int dropsPerUpdate = BASE_DROPS_PER_UPDATE;

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
          GameStatistics.incrementIntensityLevel(dt);
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
    final int edge = Chunk.TILE_SIZE * 3;

    for (int i = 0; i < dropsPerUpdate; i++) {
      float x = random.nextFloat(-edge, AcidRainSola.CANVAS_WIDTH + edge);
      float y = random.nextFloat(-edge, AcidRainSola.CANVAS_HEIGHT + edge);

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
      float threshold = COMMON_WETNESS_THRESHOLD;

      if (distance < CLOSE_WETNESS_DISTANCE) {
        threshold = CLOSE_WETNESS_THRESHOLD;
      } else if (distance < SOMEWHAT_CLOSE_WETNESS_DISTANCE) {
        threshold = SOMEWHAT_CLOSE_WETNESS_THRESHOLD;
      }

      if (distance > FAR_WETNESS_DISTANCE) {
        threshold = FAR_WETNESS_THRESHOLD;
      }

      for (int i = 0; i < GameStatistics.getIntensityLevel(); i++) {
        if (random.nextFloat() < threshold) {
          tileComponent.increaseWetness();
        }
      }

      int wetness = tileComponent.getWetness();

      if (wetness > THRESHOLD_EIGHT) {
        if (!spriteComponent.getSpriteId().equals(Constants.Assets.Sprites.ERASED)) {
          spriteComponent.setSpriteKeyFrame(SpriteCache.ERASED);
          view.entity().addComponent(
            ColliderComponent.aabb(Chunk.TILE_SIZE * 0.1f, Chunk.TILE_SIZE * 0.1f, Chunk.TILE_SIZE * 0.8f, Chunk.TILE_SIZE * 0.8f)
              .setSensor(true)
              .setTags(Constants.ColliderTags.TILE).setIgnoreTags(Constants.ColliderTags.TILE)
          );
        }
      } else {
        if (tileComponent.getTileType().isErasable) {
          if (wetness > THRESHOLD_SEVEN) {
            spriteComponent.setSpriteKeyFrame(
              SpriteCache.get(tileComponent.getTileType().assetId, "8")
            );
          } else if (wetness > THRESHOLD_SIX) {
            spriteComponent.setSpriteKeyFrame(
              SpriteCache.get(tileComponent.getTileType().assetId, "7")
            );
          } else if (wetness > THRESHOLD_FIVE) {
            spriteComponent.setSpriteKeyFrame(
              SpriteCache.get(tileComponent.getTileType().assetId, "6")
            );
          } else if (wetness > THRESHOLD_FOUR) {
            spriteComponent.setSpriteKeyFrame(
              SpriteCache.get(tileComponent.getTileType().assetId, "5")
            );
          } else if (wetness > THRESHOLD_THREE) {
            spriteComponent.setSpriteKeyFrame(
              SpriteCache.get(tileComponent.getTileType().assetId, "4")
            );
          } else if (wetness > THRESHOLD_TWO) {
            spriteComponent.setSpriteKeyFrame(
              SpriteCache.get(tileComponent.getTileType().assetId, "3")
            );
            if (tileComponent.getTileType().assetId.startsWith(Constants.Assets.Sprites.DIRT)) {
              view.entity().addComponent(
                ColliderComponent.circle(Chunk.HALF_TILE_SIZE)
                  .setSensor(true)
                  .setTags(Constants.ColliderTags.TILE).setIgnoreTags(Constants.ColliderTags.TILE)
              );
            }
          } else if (wetness > THRESHOLD_ONE) {
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
      if (dropsPerUpdate < (3 * (GameStatistics.getIntensityLevel() - 1) + BASE_DROPS_PER_UPDATE)) {
        dropsPerUpdate++;
      }
    }
  }
}
