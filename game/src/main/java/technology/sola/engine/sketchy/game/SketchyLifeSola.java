package technology.sola.engine.sketchy.game;

import technology.sola.ecs.World;
import technology.sola.engine.core.Sola;
import technology.sola.engine.core.SolaConfiguration;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.core.module.graphics.SolaGraphics;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.components.RectangleRendererComponent;
import technology.sola.engine.graphics.renderer.Renderer;
import technology.sola.engine.graphics.screen.AspectMode;
import technology.sola.engine.sketchy.game.rain.RainRenderer;
import technology.sola.engine.sketchy.game.rain.RainSystem;

public class SketchyLifeSola extends Sola {
  private SolaGraphics solaGraphics;
  private final int rows = 16;
  private final int columns = 24;
  private final int size = 20;
  private final RainRenderer rainRenderer = new RainRenderer();

  @Override
  protected SolaConfiguration getConfiguration() {
    return new SolaConfiguration("Sketchy Life", 480, 320, 30, true);
  }

  @Override
  protected void onInit() {
    platform.getViewport().setAspectMode(AspectMode.MAINTAIN);

    solaGraphics = SolaGraphics.createInstance(solaEcs, platform.getRenderer(), assetLoaderProvider);

    solaEcs.addSystems(new RainSystem(platform.getRenderer()));

    solaEcs.setWorld(buildWorld());
  }

  @Override
  protected void onRender(Renderer renderer) {
    renderer.clear();

    solaGraphics.render();

    // TODO remove this temporary grid
    for (int row = 0; row <= rows; row++) {
      renderer.drawLine(0, row * size, renderer.getWidth(), row * size, Color.BLACK);

      for (int column = 0; column < columns; column++) {
        renderer.drawLine(column * size, 0, column * size, renderer.getHeight(), Color.BLACK);
      }
    }

    rainRenderer.render(renderer, solaEcs.getWorld());
  }

  private World buildWorld() {
    World world = new World(10000);

    // TODO remove this temporary grid
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        world.createEntity(
          new TransformComponent(column * size, row * size, size, size),
          new RectangleRendererComponent(new Color(51, 51, 51))
        );
      }
    }

    return world;
  }
}
