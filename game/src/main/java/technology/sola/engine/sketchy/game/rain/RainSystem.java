package technology.sola.engine.sketchy.game.rain;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.graphics.renderer.Renderer;

import java.util.Random;

public class RainSystem extends EcsSystem {
  private final Random random = new Random();
  private final Renderer renderer;

  public RainSystem(Renderer renderer) {
    this.renderer = renderer;
  }

  @Override
  public void update(World world, float v) {
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
    final int edge = 0;
    final int dropsPerUpdate = 40;

    for (int i = 0; i < dropsPerUpdate; i++) {
      float x = random.nextFloat(-edge, renderer.getWidth() + edge);
      float y = random.nextFloat(-edge, renderer.getHeight() + edge);

      world.createEntity(
        new RainComponent(x, y)
      );
    }
  }
}
