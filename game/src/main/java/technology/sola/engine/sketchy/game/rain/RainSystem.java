package technology.sola.engine.sketchy.game.rain;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.graphics.components.sprite.SpriteComponent;
import technology.sola.engine.graphics.components.sprite.SpriteKeyFrame;
import technology.sola.engine.sketchy.game.Constants;
import technology.sola.engine.sketchy.game.chunk.TileComponent;
import technology.sola.math.linear.Vector2D;

import java.util.Random;

public class RainSystem extends EcsSystem {
  private final Random random = new Random();
  private final int rendererWidth;
  private final int rendererHeight;

  public RainSystem(int rendererWidth, int rendererHeight) {
    this.rendererWidth = rendererWidth;
    this.rendererHeight = rendererHeight;
  }

  @Override
  public void update(World world, float dt) {
    updateRainEffect(world);

    world.findEntityByName(Constants.EntityNames.PLAYER).ifPresent(playerEntity -> {
      TransformComponent playerTransform = playerEntity.getComponent(TransformComponent.class);
      Vector2D playerTranslate = playerTransform.getTranslate();

      updateTileWetness(world, playerTranslate);
    });
  }

  private void updateRainEffect(World world) {
    world.findEntityByName(Constants.EntityNames.CAMERA).ifPresent(cameraEntity -> {
      TransformComponent cameraTransform = cameraEntity.getComponent(TransformComponent.class);

      for (var view : world.createView().of(RainComponent.class)) {
        RainComponent rainComponent = view.c1();

        rainComponent.height--;

        if (rainComponent.height <= 0) {
          // todo create entity with particles for a splash

          view.entity().destroy();
        }
      }

      createRain(world, cameraTransform.getX(), cameraTransform.getY());
    });
  }

  private void createRain(World world, float cameraX, float cameraY) {
    final int edge = 200;
    final int dropsPerUpdate = 40;

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

      float distanceSquared = playerTranslate.distance(transformComponent.getTranslate());
      float threshold = 0.25f;

      // todo tune these numbers
      if (distanceSquared < 20) {
        threshold = 0.85f;
      } else if (distanceSquared < 50) {
        threshold = 0.5f;
      }

      // todo check if raining and maybe even how hard it is raining for this random
      if (random.nextFloat() < threshold) {
        tileComponent.increaseWetness();
      }

      // todo tune these numbers
      if (tileComponent.getWetness() > 300) {
        if (!spriteComponent.getSpriteId().endsWith("-5")) {
          spriteComponent.setSpriteKeyFrame(new SpriteKeyFrame(
            spriteComponent.getSpriteSheetId(), spriteComponent.getSpriteId().replace("-4", "-5"), 0
          ));
        }
      } else if (tileComponent.getWetness() > 205) {
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
}
