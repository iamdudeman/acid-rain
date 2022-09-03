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
import technology.sola.engine.sketchy.game.player.PlayerComponent;
import technology.sola.engine.sketchy.game.sunlight.SunlightBarComponent;
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
  private static final float SOMEWHAT_CLOSE_WETNESS_DISTANCE = 150;
  private static final float CLOSE_WETNESS_THRESHOLD = 0.85f;
  private static final float CLOSE_WETNESS_DISTANCE = 50;
  private static final int MAX_DROPS_PER_UPDATE = 10;

  private final Random random = new Random();
  private final int rendererWidth;
  private final int rendererHeight;
  private int dropsPerUpdate = MAX_DROPS_PER_UPDATE;

  public RainSystem(int rendererWidth, int rendererHeight) {
    this.rendererWidth = rendererWidth;
    this.rendererHeight = rendererHeight;
  }

  @Override
  public void update(World world, float dt) {
    var sunlightEntityOptional = world.findEntityByName(Constants.EntityNames.SUNLIGHT);

    sunlightEntityOptional.ifPresentOrElse(
      sunlightBarEntity -> {
        Vector2D sunlightTranslate = sunlightBarEntity.getComponent(TransformComponent.class).getTranslate();
        SunlightBarComponent sunlightBarComponent = sunlightBarEntity.getComponent(SunlightBarComponent.class);

        updateDropsPerUpdateForSunlight(sunlightBarComponent.isDraining());
        updateRainHeight(world, true);

        if (!sunlightBarComponent.isDraining()) {
          createNewRain(world);
          updateTileWetness(world, sunlightTranslate);
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
      float threshold = COMMON_WETNESS_THRESHOLD;

      if (distance < CLOSE_WETNESS_DISTANCE) {
        threshold = CLOSE_WETNESS_THRESHOLD;
      } else if (distance < SOMEWHAT_CLOSE_WETNESS_DISTANCE) {
        threshold = SOMEWHAT_CLOSE_WETNESS_THRESHOLD;
      }

      if (random.nextFloat() < threshold) {
        tileComponent.increaseWetness();
      }

      int wetness = tileComponent.getWetness();

      if (wetness > THRESHOLD_EIGHT) {
        if (!spriteComponent.getSpriteId().equals(Constants.Assets.Sprites.ERASED)) {
          spriteComponent.setSpriteKeyFrame(SpriteCache.ERASED);
          view.entity().addComponent(ColliderComponent.circle(Chunk.HALF_TILE_SIZE));
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
              view.entity().addComponent(ColliderComponent.circle(Chunk.HALF_TILE_SIZE));
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
      if (dropsPerUpdate < MAX_DROPS_PER_UPDATE) {
        dropsPerUpdate++;
      }
    }
  }
}
