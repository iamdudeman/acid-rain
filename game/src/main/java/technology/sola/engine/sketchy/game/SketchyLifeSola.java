package technology.sola.engine.sketchy.game;

import technology.sola.engine.assets.audio.AudioClip;
import technology.sola.engine.assets.graphics.SpriteSheet;
import technology.sola.engine.core.Sola;
import technology.sola.engine.core.SolaConfiguration;
import technology.sola.engine.core.module.graphics.SolaGraphics;
import technology.sola.engine.core.module.graphics.gui.SolaGui;
import technology.sola.engine.graphics.renderer.Renderer;
import technology.sola.engine.graphics.screen.AspectMode;
import technology.sola.engine.physics.system.CollisionDetectionSystem;
import technology.sola.engine.sketchy.game.chunk.ChunkSystem;
import technology.sola.engine.sketchy.game.event.GameState;
import technology.sola.engine.sketchy.game.event.GameStateEvent;
import technology.sola.engine.sketchy.game.player.CameraSystem;
import technology.sola.engine.sketchy.game.player.PlayerSystem;
import technology.sola.engine.sketchy.game.rain.RainRenderer;
import technology.sola.engine.sketchy.game.rain.RainSystem;
import technology.sola.engine.sketchy.game.state.GameUiRenderer;
import technology.sola.engine.sketchy.game.state.GameStateSystem;

public class SketchyLifeSola extends Sola {
  private final RainRenderer rainRenderer = new RainRenderer();
  private GameSettings gameSettings;
  private SolaGraphics solaGraphics;
  private SolaGui solaGui;
  private GameUiRenderer gameUiRenderer;

  @Override
  protected SolaConfiguration getConfiguration() {
    return new SolaConfiguration("Sketchy Life", 480, 320, 30, false);
  }

  @Override
  protected void onInit() {
    gameSettings = new GameSettings(solaEcs);

    // Initialize stuff for rendering
    gameUiRenderer = new GameUiRenderer(eventHub);
    solaGraphics = SolaGraphics.createInstance(solaEcs, platform.getRenderer(), assetLoaderProvider);
    platform.getViewport().setAspectMode(AspectMode.MAINTAIN);
    platform.getRenderer().createLayers(
      Constants.Layers.BACKGROUND,
      Constants.Layers.FOREGROUND
    );

    // Load assets
    assetLoaderProvider.get(SpriteSheet.class).addAssetMapping(Constants.Assets.Sprites.SPRITE_SHEET_ID, "assets/sprites.json");
    // TODO play menu music first
    assetLoaderProvider.get(AudioClip.class).getNewAsset(Constants.Assets.Audio.MAP, "assets/Test.wav")
      .executeWhenLoaded(audioClip -> {
        audioClip.setVolume(.5f);

        audioClip.loop(-1);
      });

    // Ecs setup
    ChunkSystem chunkSystem = new ChunkSystem();
    PlayerSystem playerSystem = new PlayerSystem(keyboardInput);
    CollisionDetectionSystem collisionDetectionSystem = new CollisionDetectionSystem(eventHub);
    eventHub.add(chunkSystem, GameStateEvent.class);
    solaEcs.addSystems(
      chunkSystem,
      new GameStateSystem(solaEcs, mouseInput, eventHub, platform.getRenderer().getWidth(), platform.getRenderer().getHeight()),
      new RainSystem(platform.getRenderer().getWidth(), platform.getRenderer().getHeight()),
      new CameraSystem(platform.getRenderer().getWidth(), platform.getRenderer().getHeight()),
      playerSystem,
      collisionDetectionSystem
    );
    eventHub.emit(new GameStateEvent(GameState.RESTART));
    eventHub.add(gameStateEvent -> collisionDetectionSystem.setActive(gameStateEvent.getMessage() == GameState.RESTART), GameStateEvent.class);

    // Initialize gui stuff
    // TODO do not enable solaGui until BrowserPlatform bug is fixed!
    // solaGui = SolaGui.createInstance(assetLoaderProvider, platform);
    // solaGui.setGuiRoot(MainMenuGui.buildRootElement(solaGui, gameSettings));

    // TODO temporarily now showing the menu first
    // gameSettings.showMenu();
  }

  @Override
  protected void onRender(Renderer renderer) {
    renderer.clear();

    if (gameSettings.isPlaying()) {
      solaGraphics.render();

      rainRenderer.render(renderer, solaEcs.getWorld());
      gameUiRenderer.render(renderer, solaEcs.getWorld());
    }

    if (gameSettings.isShowMenu()) {
      solaGui.render();
    }
  }
}
