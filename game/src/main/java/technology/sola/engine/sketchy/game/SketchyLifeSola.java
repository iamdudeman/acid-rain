package technology.sola.engine.sketchy.game;

import technology.sola.ecs.World;
import technology.sola.engine.assets.audio.AudioClip;
import technology.sola.engine.assets.graphics.SpriteSheet;
import technology.sola.engine.core.Sola;
import technology.sola.engine.core.SolaConfiguration;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.core.module.graphics.SolaGraphics;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.components.CameraComponent;
import technology.sola.engine.graphics.components.CircleRendererComponent;
import technology.sola.engine.graphics.components.LayerComponent;
import technology.sola.engine.graphics.renderer.Renderer;
import technology.sola.engine.graphics.screen.AspectMode;
import technology.sola.engine.sketchy.game.chunk.ChunkSystem;
import technology.sola.engine.sketchy.game.player.CameraSystem;
import technology.sola.engine.sketchy.game.player.PlayerSystem;
import technology.sola.engine.sketchy.game.rain.RainRenderer;
import technology.sola.engine.sketchy.game.rain.RainSystem;

public class SketchyLifeSola extends Sola {
  private SolaGraphics solaGraphics;
  private final RainRenderer rainRenderer = new RainRenderer();

  @Override
  protected SolaConfiguration getConfiguration() {
    return new SolaConfiguration("Sketchy Life", 480, 320, 30, false);
  }

  @Override
  protected void onInit() {
    // Initialize stuff for rendering
    solaGraphics = SolaGraphics.createInstance(solaEcs, platform.getRenderer(), assetLoaderProvider);
    platform.getViewport().setAspectMode(AspectMode.MAINTAIN);
    platform.getRenderer().createLayers(
      Constants.Layers.BACKGROUND,
      Constants.Layers.FOREGROUND
    );

    // Load assets
    assetLoaderProvider.get(SpriteSheet.class).addAssetMapping(Constants.Assets.Sprites.SPRITE_SHEET_ID, "assets/sprites.json");
    assetLoaderProvider.get(AudioClip.class).getNewAsset(Constants.Assets.Audio.MAP, "assets/Test.wav")
      .executeWhenLoaded(audioClip -> {
        audioClip.setVolume(.5f);

        audioClip.loop(-1);
      });

    // Ecs setup
    solaEcs.addSystems(
      new ChunkSystem(),
      new RainSystem(platform.getRenderer().getWidth(), platform.getRenderer().getHeight()),
      new CameraSystem(platform.getRenderer().getWidth(), platform.getRenderer().getHeight()),
      new PlayerSystem(keyboardInput)
    );
    solaEcs.setWorld(buildWorld());
  }

  @Override
  protected void onRender(Renderer renderer) {
    renderer.clear();

    solaGraphics.render();

    rainRenderer.render(renderer, solaEcs.getWorld());
  }

  private World buildWorld() {
    World world = new World(10000);

    world.createEntity(
      new TransformComponent(platform.getRenderer().getWidth() / 2f, platform.getRenderer().getHeight() / 2f, 15),
      new CircleRendererComponent(Color.RED, true),
      new LayerComponent(Constants.Layers.FOREGROUND)
    ).setName(Constants.EntityNames.PLAYER);

    world.createEntity(
      new TransformComponent(),
      new CameraComponent()
    ).setName(Constants.EntityNames.CAMERA);

    return world;
  }
}
