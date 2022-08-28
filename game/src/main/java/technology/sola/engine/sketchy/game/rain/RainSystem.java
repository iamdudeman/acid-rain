package technology.sola.engine.sketchy.game.rain;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;

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
    for (var view : world.createView().of(RainComponent.class)) {
      RainComponent rainComponent = view.c1();

      rainComponent.height--;

      if (rainComponent.height <= 0) {
        // todo create entity with particles for a splash

        view.entity().destroy();
      }
    }

    createRain(world);
  }

  private void createRain(World world) {
    final int edge = 200;
    final int dropsPerUpdate = 40;

    for (int i = 0; i < dropsPerUpdate; i++) {
      float x = random.nextFloat(-edge, rendererWidth + edge);
      float y = random.nextFloat(-edge, rendererHeight + edge);

      world.createEntity(
        new RainComponent(x, y)
      );
    }
  }
}
