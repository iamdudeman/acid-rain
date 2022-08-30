package technology.sola.engine.sketchy.game.rain;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.graphics.components.sprite.SpriteComponent;
import technology.sola.engine.graphics.components.sprite.SpriteKeyFrame;
import technology.sola.engine.physics.component.ColliderComponent;
import technology.sola.engine.sketchy.game.Constants;
import technology.sola.engine.sketchy.game.chunk.Chunk;
import technology.sola.engine.sketchy.game.chunk.TileComponent;
import technology.sola.engine.sketchy.game.player.PlayerComponent;
import technology.sola.math.linear.Vector2D;

import java.util.Random;

public class RainSystem extends EcsSystem {
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

      // todo check if raining and maybe even how hard it is raining for this random
      if (random.nextFloat() < threshold) {
        tileComponent.increaseWetness();
      }

      // todo tune these numbers
      if (tileComponent.getWetness() > 300) {
        if (!spriteComponent.getSpriteId().equals(Constants.Assets.Sprites.ERASED)) {
          spriteComponent.setSpriteKeyFrame(new SpriteKeyFrame(
            spriteComponent.getSpriteSheetId(), Constants.Assets.Sprites.ERASED, 0
          ));
          view.entity().addComponent(ColliderComponent.circle(Chunk.HALF_TILE_SIZE));
        }
      } else if (tileComponent.getWetness() > 250) {
        spriteComponent.setSpriteKeyFrame(new SpriteKeyFrame(
          spriteComponent.getSpriteSheetId(), spriteComponent.getSpriteId().replace("-4", "-5"), 0
        ));
      } else if (tileComponent.getWetness() > 195) {
        spriteComponent.setSpriteKeyFrame(new SpriteKeyFrame(
          spriteComponent.getSpriteSheetId(), spriteComponent.getSpriteId().replace("-3", "-4"), 0
        ));
      } else if (tileComponent.getWetness() > 115) {
        spriteComponent.setSpriteKeyFrame(new SpriteKeyFrame(
          spriteComponent.getSpriteSheetId(), spriteComponent.getSpriteId().replace("-2", "-3"), 0
        ));
      } else if (tileComponent.getWetness() > 50) {
        spriteComponent.setSpriteKeyFrame(new SpriteKeyFrame(
          spriteComponent.getSpriteSheetId(), spriteComponent.getSpriteId().replace("-1", "-2"), 0
        ));
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
